package fr.isep.permdemo.demos

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import fr.isep.permdemo.R
import fr.isep.permdemo.permissions.DemoCase
import fr.isep.permdemo.permissions.EventLogger
import fr.isep.permdemo.permissions.PermissionFlow
import fr.isep.permdemo.permissions.PermissionPrefs
import fr.isep.permdemo.permissions.canScheduleExactAlarms
import fr.isep.permdemo.permissions.isDeclaredInManifest
import fr.isep.permdemo.system.AlarmReceiver
import fr.isep.permdemo.ui.components.PillButton
import fr.isep.permdemo.ui.components.RationaleSheet
import fr.isep.permdemo.ui.components.ResultCard
import fr.isep.permdemo.ui.theme.colors
import java.time.LocalTime

@Composable
fun ExactAlarmDemo(case: DemoCase, flow: PermissionFlow) {
    val context = LocalContext.current
    val log = flow.log
    var explaining by remember { mutableStateOf(false) }
    var waitingForSettings by remember { mutableStateOf(false) }
    var plannedFor by remember { mutableStateOf<String?>(null) }

    fun schedule() {
        plannedFor = scheduleExactAlarm(context, inSeconds = 10)
        log.step(5, "setExactAndAllowWhileIdle(RTC_WAKEUP, +10 s) → prévue à $plannedFor")
    }

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        if (waitingForSettings) {
            waitingForSettings = false
            val granted = canScheduleExactAlarms(context)
            log.step(4, "Retour des Paramètres → canScheduleExactAlarms() = $granted")
            if (granted) schedule()
        }
    }

    PillButton(
        text = stringResource(R.string.alarm_button),
        onClick = {
            val declared = isDeclaredInManifest(context, case.permissions.first())
            log.step(0, "<uses-permission> SCHEDULE_EXACT_ALARM → ${if (declared) "déclarée" else "ABSENTE"}")
            if (Build.VERSION.SDK_INT < 31) {
                log.step(1, "Android ${Build.VERSION.SDK_INT} < 12 → permission non nécessaire")
                return@PillButton schedule()
            }
            val granted = canScheduleExactAlarms(context)
            log.step(1, "canScheduleExactAlarms() → $granted")
            if (granted) schedule() else {
                explaining = true
                log.waitFor(2)
            }
        },
        containerColor = case.type.colors.strong,
    )
    plannedFor?.let { ResultCard(stringResource(R.string.alarm_scheduled, it)) }

    if (explaining) {
        RationaleSheet(
            case = case,
            onContinue = {
                explaining = false
                log.step(2, "Feuille d'explication lue")
                openExactAlarmSettings(context, log, case.permissions)
                waitingForSettings = true
            },
            onDismiss = {
                explaining = false
                log.step(2, "Explication fermée → pas d'ouverture des Paramètres")
            },
        )
    }
}

private fun openExactAlarmSettings(context: Context, log: EventLogger, permissions: List<String>) {
    PermissionPrefs(context).markAsked(permissions)
    log.step(3, "ACTION_REQUEST_SCHEDULE_EXACT_ALARM (package:${context.packageName})")
    log.waitFor(4)
    // TODO 7: Send the user to the exact alarm settings (Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM with the URI "package:" + context.packageName).
}

private fun scheduleExactAlarm(context: Context, inSeconds: Long): String {
    val plannedFor = LocalTime.now().plusSeconds(inSeconds).format(EventLogger.TIME_FORMAT)
    val intent = Intent(context, AlarmReceiver::class.java).putExtra(AlarmReceiver.EXTRA_PLANNED_FOR, plannedFor)
    val pending = PendingIntent.getBroadcast(
        context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
    )
    val triggerAt = System.currentTimeMillis() + inSeconds * 1000
    context.getSystemService(AlarmManager::class.java)
        .setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pending)
    return plannedFor
}
