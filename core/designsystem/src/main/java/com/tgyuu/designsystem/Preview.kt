package com.tgyuu.designsystem

import android.content.res.Configuration
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.background
import androidx.glance.layout.Box
import androidx.glance.preview.ExperimentalGlancePreviewApi
import com.tgyuu.designsystem.foundation.EbbingTheme
import com.tgyuu.designsystem.foundation.PrimaryDefault

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@PreviewScreenSizes
annotation class EbbingPreview

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview(widthDp = 150, heightDp = 150)
annotation class EbbingWidgetPreview

@Composable
fun BasePreview(content: @Composable () -> Unit = {}) {
    EbbingTheme {
        Surface(color = EbbingTheme.colors.background) {
            content()
        }
    }
}

@Composable
fun BaseWidgetPreview(content: @Composable () -> Unit = {}) {
    GlanceTheme {
        Box(modifier = GlanceModifier.background(PrimaryDefault)) {
            content()
        }
    }
}
