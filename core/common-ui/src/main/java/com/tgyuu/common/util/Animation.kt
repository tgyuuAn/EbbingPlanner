package com.tgyuu.common.util

import android.content.ComponentCallbacks2
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

private const val BOTTOM_BAR_ANIMATION_DURATION = 700

@Composable
fun EbbingBottomBarAnimation(
    visible: Boolean,
    modifier: Modifier = Modifier,
    contents: @Composable AnimatedVisibilityScope.() -> Unit,
) = AnimatedVisibility(
    visible = visible,
    enter = fadeIn(tween(BOTTOM_BAR_ANIMATION_DURATION)) +
            slideInVertically(tween(BOTTOM_BAR_ANIMATION_DURATION)),
    exit = fadeOut(tween(BOTTOM_BAR_ANIMATION_DURATION)) +
            slideOutVertically(tween(BOTTOM_BAR_ANIMATION_DURATION)),
    content = contents,
    modifier = modifier,
)

@Composable
fun EbbingBottomBarEnterAnimation(
    visible: Boolean,
    modifier: Modifier = Modifier,
    contents: @Composable AnimatedVisibilityScope.() -> Unit,
) = AnimatedVisibility(
    visible = visible,
    enter = fadeIn(tween(BOTTOM_BAR_ANIMATION_DURATION)) +
            slideInVertically(tween(BOTTOM_BAR_ANIMATION_DURATION)) { it },
    exit = fadeOut(tween(0)) + shrinkVertically(tween(0)),
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
        fadeIn(tween(BOTTOM_BAR_ANIMATION_DURATION)) togetherWith
                fadeOut(tween(BOTTOM_BAR_ANIMATION_DURATION))
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
    enter = fadeIn() + slideInVertically(),
    exit = fadeOut() + slideOutVertically(),
    content = content,
    modifier = modifier,
)

val LocalAnimationsEnabled = staticCompositionLocalOf { true }

object MemoryAnimationController {
    var animationsEnabled by mutableStateOf(true)

    fun onTrimMemory(level: Int) {
        animationsEnabled = level < ComponentCallbacks2.TRIM_MEMORY_BACKGROUND
    }
}

@Composable
fun ebbingAnimateColorAsState(
    targetValue: Color,
    label: String = "ColorAnimation",
    finishedListener: ((Color) -> Unit)? = null,
): Color {
    val animationEnabled = LocalAnimationsEnabled.current

    return if (animationEnabled) {
        animateColorAsState(
            targetValue = targetValue,
            label = label,
            finishedListener = finishedListener,
        ).value
    } else {
        targetValue
    }
}
