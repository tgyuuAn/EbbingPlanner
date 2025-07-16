package com.tgyuu.ebbingplanner.widget.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.background
import androidx.glance.color.ColorProvider
import androidx.glance.layout.Box
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview(widthDp = 150, heightDp = 150)
annotation class EbbingWidgetPreview

@Composable
fun BaseWidgetPreview(content: @Composable () -> Unit = {}) {
    GlanceTheme {
        Box(modifier = GlanceModifier.background(ColorProvider(Color.Black, Color.White))) {
            content()
        }
    }
}
