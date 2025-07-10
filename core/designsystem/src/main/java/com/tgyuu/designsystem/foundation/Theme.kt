package com.tgyuu.designsystem.foundation

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import com.tgyuu.domain.model.Theme

private val LocalColors = staticCompositionLocalOf {
    normalLightColorScheme
}
private val LocalTypography = staticCompositionLocalOf {
    EbbingTypography()
}

@Composable
fun EbbingTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    customTheme: Theme = Theme.NORMAL,
    content: @Composable () -> Unit,
) {
    val colors = when (customTheme) {
        Theme.NORMAL -> if (darkTheme) normalDarkColorScheme else normalLightColorScheme
        Theme.DARK -> normalDarkColorScheme

        Theme.FOREST -> if (darkTheme) forestDarkColorScheme else forestLightColorScheme
        Theme.FOREST_DARK -> forestDarkColorScheme

        Theme.SUNSET -> if (darkTheme) sunsetDarkColorScheme else sunsetLightColorScheme
        Theme.SUNSET_DARK -> sunsetDarkColorScheme

        Theme.PASTEL -> if (darkTheme) pastelDarkColorScheme else pastelLightColorScheme
        Theme.PASTEL_DARK -> pastelDarkColorScheme
    }

    CompositionLocalProvider(
        LocalColors provides colors,
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

