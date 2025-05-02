package com.tgyuu.designsystem.component

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateIntOffsetAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.tgyuu.common.ui.throttledClickable
import com.tgyuu.designsystem.BasePreview
import com.tgyuu.designsystem.EbbingPreview
import com.tgyuu.designsystem.foundation.EbbingTheme

@Composable
fun EbbingToggle(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val screenWidthPx = with(density) { 14.dp.toPx().toInt() }
    val targetOffset = if (checked) {
        IntOffset(screenWidthPx, 0)
    } else {
        IntOffset.Zero
    }

    val thumbXOffset by animateIntOffsetAsState(
        targetValue = targetOffset,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "Thumb Animation",
    )

    Box(
        contentAlignment = Alignment.CenterStart,
        modifier = modifier
            .size(width = 34.dp, height = 20.dp)
            .clip(RoundedCornerShape(999.dp))
            .background(if (checked) EbbingTheme.colors.primaryDefault else EbbingTheme.colors.light1)
            .throttledClickable(1000L) { onCheckedChange(!checked) }
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = 2.dp)
                .graphicsLayer { translationX = thumbXOffset.x.toFloat() }
                .size(SwitchDefaults.IconSize)
                .clip(CircleShape)
                .background(EbbingTheme.colors.background),
        )
    }
}

@EbbingPreview
@Composable
fun PreviewToggle() {
    BasePreview {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(10.dp)
        ) {
            EbbingToggle(
                checked = true,
                onCheckedChange = {},
            )

            EbbingToggle(
                checked = false,
                onCheckedChange = {},
            )
        }
    }
}
