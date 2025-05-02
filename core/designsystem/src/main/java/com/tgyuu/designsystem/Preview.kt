package com.tgyuu.designsystem

import android.content.res.Configuration
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import com.tgyuu.designsystem.foundation.EbbingTheme

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
annotation class EbbingPreview

@Composable
fun BasePreview(content: @Composable () -> Unit = {}) {
    EbbingTheme {
        Surface(color = EbbingTheme.colors.background) {
            content()
        }
    }
}
