package com.tgyuu.designsystem.foundation

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf

private val LocalColors = staticCompositionLocalOf {
    EbbingColors()
}
private val LocalTypography = staticCompositionLocalOf {
    EbbingTypography()
}

@Composable
fun EbbingTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalColors provides EbbingColors().copy(
            background = if (darkTheme) DarkBackground else LightBackground
        )
    ) {
        CompositionLocalProvider(content = content)
    }
}

object EbbingTheme {
    val colors: EbbingColors
        @Composable
        get() = LocalColors.current
    val typography: EbbingTypography
        @Composable
        get() = LocalTypography.current
}

