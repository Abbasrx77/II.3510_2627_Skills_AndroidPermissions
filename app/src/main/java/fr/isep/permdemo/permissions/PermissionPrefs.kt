package fr.isep.permdemo.permissions

import android.content.Context
import androidx.core.content.edit

class PermissionPrefs(context: Context) {
    private val prefs = context.getSharedPreferences("permission_prefs", Context.MODE_PRIVATE)

    fun wasAsked(permissions: List<String>): Boolean =
        permissions.any { prefs.getBoolean(it, false) }

    fun markAsked(permissions: List<String>) = prefs.edit {
        permissions.forEach { putBoolean(it, true) }
    }
}
