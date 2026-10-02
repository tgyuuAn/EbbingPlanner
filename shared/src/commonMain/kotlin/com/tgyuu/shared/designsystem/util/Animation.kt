package com.tgyuu.shared.designsystem.util

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Android core/common-ui `EbbingVisibleAnimation`과 동일한 등장/퇴장 연출.
 * enter = fadeIn() + slideInVertically(), exit = fadeOut() + slideOutVertically().
 */
@Composable
fun EbbingVisibleAnimation(
    visible: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable AnimatedVisibilityScope.() -> Unit,
) = AnimatedVisibility(
    visible = visible,
    enter = fadeIn() + slideInVertically(),
    exit = fadeOut() + slideOutVertically(),
    modifier = modifier,
    content = content,
)
