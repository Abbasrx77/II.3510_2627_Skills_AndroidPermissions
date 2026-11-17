package fr.isep.permdemo.permissions

import android.app.Activity
import android.content.Context
import android.os.Build
import androidx.annotation.StringRes
import fr.isep.permdemo.R

enum class PermissionStatus(@StringRes val label: Int) {
    NEVER_ASKED(R.string.status_never_asked),
    GRANTED(R.string.status_granted),
    APPROXIMATE(R.string.status_approximate),
    PARTIAL(R.string.status_partial),
    DENIED(R.string.status_denied),
    PERMANENTLY_DENIED(R.string.status_permanently_denied),
    NOT_NEEDED(R.string.status_not_needed);

    val canUseFeature: Boolean
        get() = this == GRANTED || this == APPROXIMATE || this == PARTIAL || this == NOT_NEEDED
}

fun readStatus(
    activity: Activity,
    permissions: List<String>,
    prefs: PermissionPrefs,
    partialStatus: PermissionStatus = PermissionStatus.GRANTED,
    minSdk: Int = 1,
): PermissionStatus {
    if (Build.VERSION.SDK_INT < minSdk) return PermissionStatus.NOT_NEEDED
    val granted = permissions.filter { isGranted(activity, it) }
    return when {
        granted.size == permissions.size -> PermissionStatus.GRANTED
        granted.isNotEmpty() -> partialStatus
        !prefs.wasAsked(permissions) -> PermissionStatus.NEVER_ASKED
        isPermanentlyDenied(activity, permissions) -> PermissionStatus.PERMANENTLY_DENIED
        else -> PermissionStatus.DENIED
    }
}

fun isGranted(context: Context, permission: String): Boolean {
    // TODO 2: Check the permission with ContextCompat.checkSelfPermission and return true only if it is PackageManager.PERMISSION_GRANTED.
    return false
}

fun isPermanentlyDenied(activity: Activity, permissions: List<String>): Boolean {
    // TODO 4: This is called after a request was refused. Return true (permanent denial) when shouldShowRequestPermissionRationale is false for every permission.
    return false
}

fun canScheduleExactAlarms(context: Context): Boolean {
    // TODO 7: Return true before Android 12 (API 31). From Android 12, return AlarmManager.canScheduleExactAlarms().
    return false
}

fun readExactAlarmStatus(activity: Activity, prefs: PermissionPrefs, permission: String): PermissionStatus {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return PermissionStatus.NOT_NEEDED
    return when {
        canScheduleExactAlarms(activity) -> PermissionStatus.GRANTED
        !prefs.wasAsked(listOf(permission)) -> PermissionStatus.NEVER_ASKED
        else -> PermissionStatus.DENIED
    }
}
