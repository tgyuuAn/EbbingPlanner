package com.tgyuu.ebbingplanner.ui.widget.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.glance.ColorFilter
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.Action
import androidx.glance.action.clickable
import androidx.glance.color.ColorProvider
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.ContentScale
import androidx.glance.layout.size
import com.tgyuu.ebbingplanner.R

@Composable
fun EbbingWidgetCheck(
    checked: Boolean,
    colorValue: Int,
    onCheckedChange: Action,
    modifier: GlanceModifier = GlanceModifier,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(20.dp)
            .clickable(onCheckedChange),
    ) {
        if (checked) {
            Image(
                provider = ImageProvider(com.tgyuu.designsystem.R.drawable.ic_widget_check),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                colorFilter = ColorFilter.tint(ColorProvider(Color(colorValue), Color(colorValue))),
            )
        } else {
            Image(
                provider = ImageProvider(R.drawable.shape_widget_unchecked),
                contentDescription = null,
                colorFilter = ColorFilter.tint(ColorProvider(Color(colorValue), Color(colorValue))),
                modifier = GlanceModifier.size(20.dp),
            )
        }
    }
}
