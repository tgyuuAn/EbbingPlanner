package com.tgyuu.ebbingplanner

import android.app.Application
import com.tgyuu.alarm.NotificationHelper
import com.tgyuu.common.initializer.Initializer
import com.tgyuu.common.suspendRunCatching
import com.tgyuu.common.util.MemoryAnimationController
import com.tgyuu.ebbingplanner.di.appModules
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class EbbingPlannerApplication : Application() {
    private val applicationScope = MainScope()

    private val notificationHelper: NotificationHelper by inject()
    private val initializers: Set<Initializer> by inject()

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@EbbingPlannerApplication)
            modules(appModules)
        }

        notificationHelper.createNotificationChannel(this)
        initialize()
    }

    private fun initialize() {
        applicationScope.launch {
            initializers
                .sortedBy { it.priority }
                .forEach { suspendRunCatching { it.initialize() } }
        }
    }

    override fun onLowMemory() {
        super.onLowMemory()
        onTrimMemory(TRIM_MEMORY_UI_HIDDEN)
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        MemoryAnimationController.onTrimMemory(level)
    }
}
