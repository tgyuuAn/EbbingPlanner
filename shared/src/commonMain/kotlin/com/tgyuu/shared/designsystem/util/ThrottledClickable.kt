package com.tgyuu.shared.designsystem.util

import androidx.compose.foundation.clickable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed

fun Modifier.throttledClickable(
    throttleTime: Long = 500L,
    enabled: Boolean = true,
    onClick: () -> Unit,
): Modifier = composed {
    var lastClickTime by remember { mutableLongStateOf(0L) }

    clickable(enabled = enabled) {
        val now = kotlin.time.Clock.System.now().toEpochMilliseconds()
        if (now - lastClickTime >= throttleTime) {
            lastClickTime = now
            onClick()
        }
    }
}
