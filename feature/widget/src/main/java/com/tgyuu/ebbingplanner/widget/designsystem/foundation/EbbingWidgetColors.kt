package com.tgyuu.ebbingplanner.widget.designsystem.foundation

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.glance.color.ColorProvider
import androidx.glance.unit.ColorProvider

data class EbbingWidgetColors(
    // Background
    val background: ColorProvider,
    // Primary
    val primaryNormal: ColorProvider,
    val primaryDeep: ColorProvider,
    val primaryContainer: ColorProvider,
    // Status
    val statusError: ColorProvider,
    val statusSuccess: ColorProvider,
    // Text
    val textOnBackground: ColorProvider,
    val textSub: ColorProvider,
    val textDisabled: ColorProvider,
    val textOnPrimary: ColorProvider,
    val textPrimary: ColorProvider,
    val textError: ColorProvider,
    // Fill
    val fillNormal: ColorProvider,
    val fillStrong: ColorProvider,
    val fillDisabled: ColorProvider,
    val fillPrimary: ColorProvider,
)

val LocalEbbingWidgetColors = staticCompositionLocalOf<EbbingWidgetColors> {
    error("LocalEbbingWidgetColors not provided")
}
