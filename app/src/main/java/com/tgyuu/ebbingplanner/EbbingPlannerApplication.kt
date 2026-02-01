package com.tgyuu.ebbingplanner

import android.app.Application
import com.tgyuu.alarm.NotificationHelper
import com.tgyuu.common.initializer.Initializer
import com.tgyuu.common.suspendRunCatching
import com.tgyuu.common.util.MemoryAnimationController
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class EbbingPlannerApplication : Application() {
    private val applicationScope = MainScope()

    @Inject
    lateinit var notificationHelper: NotificationHelper

    @Inject
    lateinit var initializers: Set<@JvmSuppressWildcards Initializer>

    override fun onCreate() {
        super.onCreate()
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
