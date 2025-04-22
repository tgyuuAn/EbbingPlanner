package com.tgyuu.designsystem.component

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.tgyuu.designsystem.foundation.EbbingTheme

@Preview
@Composable
fun BasePreview(content: @Composable () -> Unit = {}) {
    EbbingTheme {
        Surface(color = EbbingTheme.colors.background) {
            content()
        }
    }
}
