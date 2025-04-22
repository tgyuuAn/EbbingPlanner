package com.tgyuu.common.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

private const val BOTTOM_BAR_ANIMATION_DURATION = 700

fun ebbingExitTransitionAnimation() = fadeOut(tween(BOTTOM_BAR_ANIMATION_DURATION))
fun ebbingEnterTransitionAnimation() = fadeIn(tween(BOTTOM_BAR_ANIMATION_DURATION))

@Composable
fun EbbingBottomBarAnimation(
    visible: Boolean,
    modifier: Modifier = Modifier,
    contents: @Composable AnimatedVisibilityScope.() -> Unit,
) = AnimatedVisibility(
    visible = visible,
    enter = fadeIn(tween(BOTTOM_BAR_ANIMATION_DURATION)) + slideInVertically(
        tween(
            BOTTOM_BAR_ANIMATION_DURATION
        )
    ),
    exit = fadeOut(tween(BOTTOM_BAR_ANIMATION_DURATION)) + slideOutVertically(
        tween(
            BOTTOM_BAR_ANIMATION_DURATION
        )
    ),
    content = contents,
    modifier = modifier,
)

@Composable
fun <S> EbbingPageTransitionAnimation(
    targetState: S,
    modifier: Modifier = Modifier,
    content: @Composable() AnimatedContentScope.(targetState: S) -> Unit
) = AnimatedContent(
    targetState = targetState,
    transitionSpec = {
        fadeIn(tween(BOTTOM_BAR_ANIMATION_DURATION)) togetherWith fadeOut(
            tween(BOTTOM_BAR_ANIMATION_DURATION)
        )
    },
    content = content,
    modifier = modifier,
)

@Composable
fun EbbingVisibleAnimation(
    visible: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable AnimatedVisibilityScope.() -> Unit,
) = AnimatedVisibility(
    visible = visible,
    enter = fadeIn() + expandVertically(),
    exit = fadeOut() + shrinkVertically(),
    content = content,
    modifier = modifier,
)
