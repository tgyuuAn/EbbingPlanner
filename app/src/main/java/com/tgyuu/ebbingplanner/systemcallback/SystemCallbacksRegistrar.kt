package com.tgyuu.ebbingplanner.systemcallback

import android.content.ComponentCallbacks2
import android.content.ComponentCallbacks2.TRIM_MEMORY_BACKGROUND
import android.content.ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN
import android.content.res.Configuration
import com.tgyuu.domain.model.HeapLogger
import com.tgyuu.common.systemcallback.MemoryAnimationController
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SystemCallbacksRegistrar @Inject constructor(
    private val heapLogger: HeapLogger,
) : ComponentCallbacks2 {
    override fun onLowMemory() = onTrimMemory(TRIM_MEMORY_UI_HIDDEN)
    override fun onTrimMemory(level: Int) {
        MemoryAnimationController.onTrimMemory(level)
        if (level >= TRIM_MEMORY_BACKGROUND) {
            heapLogger.logHeapDump()
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {}
}
