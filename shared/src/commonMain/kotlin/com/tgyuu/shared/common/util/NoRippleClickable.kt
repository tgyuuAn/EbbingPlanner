package com.tgyuu.shared.common.util

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed

/**
 * Android core/common-ui `com.tgyuu.common.util.clickable` 포팅.
 * 기본은 리플(indication) 없이 클릭만 처리해요. isRipple = true면 기본 indication을 써요.
 */
fun Modifier.noRippleClickable(
    enabled: Boolean = true,
    isRipple: Boolean = false,
    onClick: () -> Unit,
): Modifier = composed {
    this.clickable(
        indication = if (isRipple) LocalIndication.current else null,
        interactionSource = remember { MutableInteractionSource() },
        enabled = enabled,
        onClick = onClick,
    )
}
