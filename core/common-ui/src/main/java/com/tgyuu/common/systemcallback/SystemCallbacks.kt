package com.tgyuu.common.systemcallback

import android.content.ComponentCallbacks2
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
