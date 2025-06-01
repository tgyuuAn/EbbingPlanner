package com.tgyuu.ebbingplanner.ui.widget.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.clickable
import androidx.glance.color.ColorProvider
import androidx.glance.layout.Box
import androidx.glance.layout.ContentScale
import androidx.glance.layout.size

@Composable
fun EbbingWidgetCheck(
    checked: Boolean,
    colorValue: Int,
    onCheckedChange: (Boolean) -> Unit,
    modifier: GlanceModifier = GlanceModifier,
) {
    Box(
        modifier = modifier
            .size(20.dp)
            .clickable { onCheckedChange(!checked) },
    ) {
        Image(
            provider = ImageProvider(com.tgyuu.designsystem.R.drawable.ic_widget_check),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            colorFilter = androidx.glance.ColorFilter.tint(
                ColorProvider(Color(colorValue), Color(colorValue))
            ),
        )
    }
}
