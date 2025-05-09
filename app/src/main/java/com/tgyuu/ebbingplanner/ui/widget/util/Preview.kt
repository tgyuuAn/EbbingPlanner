package com.tgyuu.ebbingplanner.ui.widget.util

import androidx.compose.runtime.Composable
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.background
import androidx.glance.color.ColorProvider
import androidx.glance.layout.Box
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview
import com.tgyuu.designsystem.foundation.PrimaryDefault
import com.tgyuu.designsystem.foundation.PrimaryLight

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview(widthDp = 150, heightDp = 150)
annotation class EbbingWidgetPreview

@Composable
fun BaseWidgetPreview(content: @Composable () -> Unit = {}) {
    GlanceTheme {
        Box(modifier = GlanceModifier.background(ColorProvider(PrimaryDefault, PrimaryLight))) {
            content()
        }
    }
}
