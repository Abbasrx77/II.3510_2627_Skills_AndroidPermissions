package fr.isep.permdemo.system

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import fr.isep.permdemo.MainActivity
import fr.isep.permdemo.R

object NotificationHelper {
    const val CHANNEL_ID = "permdemo_demo"

    fun createChannel(context: Context) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            context.getString(R.string.notif_channel_name),
            NotificationManager.IMPORTANCE_HIGH,
        )
        context.getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    fun canNotify(context: Context): Boolean =
        NotificationManagerCompat.from(context).areNotificationsEnabled()

    @SuppressLint("MissingPermission")
    fun show(context: Context, id: Int, title: String, text: String): Boolean {
        if (!canNotify(context)) return false
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(text)
            .setContentIntent(openAppIntent(context))
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(context).notify(id, notification)
        return true
    }

    private fun openAppIntent(context: Context): PendingIntent = PendingIntent.getActivity(
        context, 0, Intent(context, MainActivity::class.java), PendingIntent.FLAG_IMMUTABLE,
    )
}
