package com.tgyuu.shared.designsystem.foundation

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import com.tgyuu.shared.domain.model.Theme

val LocalColors = staticCompositionLocalOf {
    normalLightColorScheme
}
val LocalTypography = staticCompositionLocalOf {
    EbbingTypography()
}

/** 테마+다크모드에 해당하는 색상 스킴 (Android Theme.primaryNormalColor 대응 접근용) */
fun colorSchemeFor(theme: Theme, darkTheme: Boolean): EbbingColors = when (theme) {
    Theme.NORMAL -> if (darkTheme) normalDarkColorScheme else normalLightColorScheme
    Theme.FOREST -> if (darkTheme) forestDarkColorScheme else forestLightColorScheme
    Theme.SUNSET -> if (darkTheme) sunsetDarkColorScheme else sunsetLightColorScheme
    Theme.MARINE -> if (darkTheme) marineDarkColorScheme else marineLightColorScheme
    Theme.LILAC -> if (darkTheme) lilacDarkColorScheme else lilacLightColorScheme
}

@Composable
fun EbbingTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    theme: Theme = Theme.NORMAL,
    content: @Composable () -> Unit,
) {
    val colors = colorSchemeFor(theme, darkTheme)

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
