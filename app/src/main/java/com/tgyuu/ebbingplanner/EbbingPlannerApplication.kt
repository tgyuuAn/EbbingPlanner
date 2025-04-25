package com.tgyuu.ebbingplanner

import android.app.Application
import com.tgyuu.alarm.NotificationHelper
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class EbbingPlannerApplication : Application() {
    @Inject
    lateinit var notificationHelper: NotificationHelper

    override fun onCreate() {
        super.onCreate()
        notificationHelper.createNotificationChannel(this)
    }
}
