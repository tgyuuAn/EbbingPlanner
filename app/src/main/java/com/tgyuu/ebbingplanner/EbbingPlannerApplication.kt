package com.tgyuu.ebbingplanner

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.tgyuu.alarm.NotificationHelper
import com.tgyuu.common.systemcallback.MemoryAnimationController
import com.tgyuu.domain.model.HeapLogger
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class EbbingPlannerApplication : Application(), Configuration.Provider {
    @Inject
    lateinit var notificationHelper: NotificationHelper

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var heapLogger: HeapLogger

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        notificationHelper.createNotificationChannel(this)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        onTrimMemory(TRIM_MEMORY_UI_HIDDEN)
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        MemoryAnimationController.onTrimMemory(level)
        if (level >= TRIM_MEMORY_BACKGROUND) {
            heapLogger.logHeapDump()
        }
    }
}
