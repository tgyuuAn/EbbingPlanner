package com.tgyuu.common.systemcallback

import android.content.ComponentCallbacks2
import android.content.ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL
import android.content.res.Configuration
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf

val LocalAnimationsEnabled = staticCompositionLocalOf { true }

object MemoryAnimationController {
    var animationsEnabled by mutableStateOf(true)

    fun onTrimMemory(level: Int) {
        animationsEnabled = level < ComponentCallbacks2.TRIM_MEMORY_BACKGROUND
    }
}

class SystemCallbacks : ComponentCallbacks2 {
    override fun onConfigurationChanged(newConfig: Configuration) {}
    override fun onLowMemory() = onTrimMemory(TRIM_MEMORY_RUNNING_CRITICAL)
    override fun onTrimMemory(level: Int) {
        MemoryAnimationController.onTrimMemory(level)
    }
}
