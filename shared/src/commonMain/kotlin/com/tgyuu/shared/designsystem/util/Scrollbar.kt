package com.tgyuu.shared.designsystem.util

import androidx.compose.animation.core.Spring.StiffnessMediumLow
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Android core/common-ui ModifierUtil.verticalScrollbar 포팅 (스크롤 중에만 우측 인디케이터 표시) */
@Composable
fun Modifier.verticalScrollbar(
    state: LazyListState,
    width: Dp = 6.dp,
    color: Color,
): Modifier {
    val targetAlpha = if (state.isScrollInProgress) .7f else 0f
    val duration = if (state.isScrollInProgress) 150 else 1000
    val alpha by animateFloatAsState(targetValue = targetAlpha, animationSpec = tween(durationMillis = duration))
    val firstIndex by animateFloatAsState(
        targetValue = state.layoutInfo.visibleItemsInfo.firstOrNull()?.index?.toFloat() ?: 0f,
        animationSpec = spring(stiffness = StiffnessMediumLow),
    )
    val lastIndex by animateFloatAsState(
        targetValue = state.layoutInfo.visibleItemsInfo.lastOrNull()?.index?.toFloat() ?: 0f,
        animationSpec = spring(stiffness = StiffnessMediumLow),
    )
    return drawWithContent {
        drawContent()
        val itemsCount = state.layoutInfo.totalItemsCount
        if (itemsCount > 0 && alpha > 0f) {
            val scrollbarTop = firstIndex / itemsCount * size.height
            val scrollBottom = (lastIndex + 1f) / itemsCount * size.height
            drawRoundRect(
                color = color,
                cornerRadius = CornerRadius(0.1f),
                topLeft = Offset(size.width - width.toPx(), scrollbarTop),
                size = Size(width.toPx(), scrollBottom - scrollbarTop),
                alpha = alpha,
            )
        }
    }
}

@Composable
fun Modifier.verticalScrollbar(
    state: LazyGridState,
    width: Dp = 6.dp,
    color: Color,
): Modifier {
    val targetAlpha = if (state.isScrollInProgress) .7f else 0f
    val duration = if (state.isScrollInProgress) 150 else 1000
    val alpha by animateFloatAsState(targetValue = targetAlpha, animationSpec = tween(durationMillis = duration))
    val firstIndex by animateFloatAsState(
        targetValue = state.layoutInfo.visibleItemsInfo.firstOrNull()?.index?.toFloat() ?: 0f,
        animationSpec = spring(stiffness = StiffnessMediumLow),
    )
    val lastIndex by animateFloatAsState(
        targetValue = state.layoutInfo.visibleItemsInfo.lastOrNull()?.index?.toFloat() ?: 0f,
        animationSpec = spring(stiffness = StiffnessMediumLow),
    )
    return drawWithContent {
        drawContent()
        val itemsCount = state.layoutInfo.totalItemsCount
        if (itemsCount > 0 && alpha > 0f) {
            val scrollbarTop = firstIndex / itemsCount * size.height
            val scrollBottom = (lastIndex + 1f) / itemsCount * size.height
            drawRoundRect(
                color = color,
                cornerRadius = CornerRadius(0.1f),
                topLeft = Offset(size.width - width.toPx(), scrollbarTop),
                size = Size(width.toPx(), scrollBottom - scrollbarTop),
                alpha = alpha,
            )
        }
    }
}
