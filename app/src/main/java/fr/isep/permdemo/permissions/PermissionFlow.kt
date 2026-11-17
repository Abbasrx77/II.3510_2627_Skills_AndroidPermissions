package fr.isep.permdemo.permissions

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect

class PermissionFlow(
    private val activity: ComponentActivity,
    private val case: DemoCase,
    private val prefs: PermissionPrefs,
    val log: EventLogger,
    private val requestNote: String? = null,
) {
    var status by mutableStateOf(case.readStatus(activity, prefs))
        private set
    var rationaleVisible by mutableStateOf(false)
        private set

    internal var launcher: ActivityResultLauncher<Array<String>>? = null
    private var onGranted: () -> Unit = {}
    private val names = case.permissions.joinToString { it.substringAfterLast('.') }

    fun refresh() {
        status = case.readStatus(activity, prefs)
    }

    fun start(onGranted: () -> Unit) {
        this.onGranted = onGranted
        checkManifest()
        if (Build.VERSION.SDK_INT < case.minSdk) {
            log.step(1, "Android ${Build.VERSION.SDK_INT} < ${case.minSdk} → rien à demander")
            return use()
        }
        val granted = case.permissions.map { isGranted(activity, it) }
        log.step(1, "checkSelfPermission($names) → ${describe(granted)}")
        if (granted.any { it }) return use()

        val rationale = shouldExplain()
        val alreadyAsked = prefs.wasAsked(case.permissions)
        val why = when {
            rationale && alreadyAsked -> "déjà refusée une fois"
            rationale -> "le système conseille d'expliquer d'abord"
            alreadyAsked -> "refus définitif"
            else -> "première demande"
        }
        log.step(2, "shouldShowRequestPermissionRationale → $rationale ($why)")
        when {
            rationale -> showRationale()
            alreadyAsked -> handlePermanentDenial()
            else -> request()
        }
    }

    fun onRationaleAccepted() {
        rationaleVisible = false
        log.step(2, "Explication lue → on redemande")
        request()
    }

    fun onRationaleDismissed() {
        rationaleVisible = false
        log.step(2, "Explication fermée → pas de nouvelle demande")
    }

    fun openAppSettings() {
        log.step(5, "Settings.ACTION_APPLICATION_DETAILS_SETTINGS")
        // TODO 5: Open this app's details page in the system Settings (Settings.ACTION_APPLICATION_DETAILS_SETTINGS with a "package:" URI).
    }

    private fun request() {
        prefs.markAsked(case.permissions)
        val note = requestNote?.let { " → $it" }.orEmpty()
        log.step(3, "launcher.launch($names)$note")
        log.waitFor(4)
        launcher?.launch(case.permissions.toTypedArray())
    }

    internal fun onResult(result: Map<String, Boolean>) {
        // TODO 4: Handle the answer: refresh() the status, log step 4 with the raw result and the new status, then call use() if status.canUseFeature.
    }

    private fun shouldExplain(): Boolean {
        // TODO 3: Return true if shouldShowRequestPermissionRationale is true for at least one permission of the case.
        return false
    }

    private fun showRationale() {
        // TODO 3: Show the explanation sheet (set rationaleVisible to true) and mark step 2 as in progress with log.waitFor(2).
    }

    private fun handlePermanentDenial() {
        refresh()
        log.step(4, "Refus définitif : le système n'affichera plus la boîte de dialogue")
        log.waitFor(5)
    }

    private fun use() {
        log.step(5, "Accès autorisé → on utilise la fonctionnalité")
        onGranted()
    }

    private fun checkManifest() {
        val ok = case.permissions.all { isDeclaredInManifest(activity, it) }
        log.step(0, "<uses-permission> $names → ${if (ok) "déclarée" else "ABSENTE du Manifest"}")
    }

    private fun describe(granted: List<Boolean>): String =
        if (granted.size == 1) {
            if (granted[0]) "PERMISSION_GRANTED" else "PERMISSION_DENIED"
        } else {
            granted.joinToString { if (it) "GRANTED" else "DENIED" }
        }
}

@Suppress("DEPRECATION")
fun isDeclaredInManifest(context: Context, permission: String): Boolean =
    context.packageManager
        .getPackageInfo(context.packageName, PackageManager.GET_PERMISSIONS)
        .requestedPermissions.orEmpty()
        .contains(permission)

@Composable
fun rememberPermissionFlow(case: DemoCase, requestNote: String? = null): PermissionFlow {
    val activity = LocalActivity.current as ComponentActivity
    val flow = remember(case.id, case.permissions) {
        PermissionFlow(activity, case, PermissionPrefs(activity), EventLogs.of(case.id), requestNote)
    }
    // TODO 4: Register a RequestMultiplePermissions launcher with rememberLauncherForActivityResult, store it in flow.launcher and send its result to flow.onResult.
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { flow.refresh() }
    return flow
}
