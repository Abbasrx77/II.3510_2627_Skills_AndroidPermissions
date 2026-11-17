package fr.isep.permdemo.system

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import fr.isep.permdemo.R
import fr.isep.permdemo.permissions.EventLogger
import fr.isep.permdemo.permissions.EventLogs

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val plannedFor = intent.getStringExtra(EXTRA_PLANNED_FOR).orEmpty()
        val receivedAt = EventLogger.now()
        val log = EventLogs.of("exact_alarm")
        log.step(5, "AlarmReceiver.onReceive → prévue $plannedFor, reçue $receivedAt")

        val shown = NotificationHelper.show(
            context,
            NOTIFICATION_ID,
            context.getString(R.string.alarm_notif_title),
            context.getString(R.string.alarm_notif_text, plannedFor, receivedAt),
        )
        if (!shown) log.step(5, "Notification non affichée : notifications bloquées")
    }

    companion object {
        const val EXTRA_PLANNED_FOR = "planned_for"
        private const val NOTIFICATION_ID = 6
    }
}
