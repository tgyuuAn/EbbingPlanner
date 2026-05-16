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
    val fillTextfield: ColorProvider,
    val fillSelected: ColorProvider,
    val fillDisabled: ColorProvider,
    val fillPrimary: ColorProvider,
    val fillFocused: ColorProvider,
    val fillError: ColorProvider,
    // Stroke
    val strokePrimary: ColorProvider,
    val strokeNormal: ColorProvider,
    val strokeSecondary: ColorProvider,
    val strokeOutline: ColorProvider,
    val strokeOnPrimary: ColorProvider,
    val strokeIcon: ColorProvider,
    val strokeDisabled: ColorProvider,
    // Material
    val materialDimmer: ColorProvider,
)

val LocalEbbingWidgetColors = staticCompositionLocalOf<EbbingWidgetColors> {
    error("LocalEbbingWidgetColors not provided")
}
