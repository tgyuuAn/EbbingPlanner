package com.tgyuu.ebbingplanner

import android.app.Application
import com.tgyuu.alarm.NotificationHelper
import com.tgyuu.common.systemcallback.MemoryAnimationController
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class EbbingPlannerApplication : Application() {
    @Inject
    lateinit var notificationHelper: NotificationHelper

    override fun onCreate() {
        super.onCreate()
        notificationHelper.createNotificationChannel(this)
        cleanupOldHeapDumps()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        onTrimMemory(TRIM_MEMORY_UI_HIDDEN)
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        MemoryAnimationController.onTrimMemory(level)
    }

    private fun cleanupOldHeapDumps() {
        filesDir.listFiles { file ->
            file.name.endsWith(".hprof")
        }?.onEach { it.delete() }
    }
}
