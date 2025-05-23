package com.tgyuu.designsystem.foundation

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density

private val LocalColors = staticCompositionLocalOf {
    lightModeColorScheme
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
        LocalColors provides if (darkTheme) darkModeColorScheme else lightModeColorScheme,
        LocalDensity provides Density(LocalDensity.current.density, 1f)
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

