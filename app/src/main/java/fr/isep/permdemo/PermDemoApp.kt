package fr.isep.permdemo

import android.app.Application
import fr.isep.permdemo.system.NotificationHelper

class PermDemoApp : Application() {
    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createChannel(this)
    }
}
