package com.tgyuu.designsystem.foundation

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

// =============================================================
// Color_Base / Greyscale
// =============================================================
private val Greyscale000 = Color(0xFF070808)
private val Greyscale100 = Color(0xFF131416)
private val Greyscale200 = Color(0xFF4B4F5D)
private val Greyscale300 = Color(0xFF5B5F72)
private val Greyscale400 = Color(0xFF788198)
private val Greyscale500 = Color(0xFF8994A8)
private val Greyscale600 = Color(0xFFA1AAB8)
private val Greyscale700 = Color(0xFFBCC6D4)
private val Greyscale800 = Color(0xFFE8EBF0)
private val Greyscale900 = Color(0xFFF4F6FA)
private val Greyscale950 = Color(0xFFFFFFFF)

// =============================================================
// Color_Base / Bluescale
// =============================================================
private val Bluescale000 = Color(0xFF091F31)
private val Bluescale100 = Color(0xFF194467)
private val Bluescale200 = Color(0xFF0F4C75)
private val Bluescale300 = Color(0xFF0E5F96)
private val Bluescale400 = Color(0xFF1077B9)
private val Bluescale500 = Color(0xFF1D95DA)
private val Bluescale600 = Color(0xFF82C0E2)
private val Bluescale700 = Color(0xFFB1D7EE)
private val Bluescale800 = Color(0xFFCBE5F5)
private val Bluescale900 = Color(0xFFF1F8FE)

// =============================================================
// Color_Base / Greenscale (Forest)
// =============================================================
private val Greenscale000 = Color(0xFF0D120E)
private val Greenscale100 = Color(0xFF1A4520)
private val Greenscale200 = Color(0xFF2D6830)
private val Greenscale300 = Color(0xFF2E8439)
private val Greenscale400 = Color(0xFF388E3C)
private val Greenscale500 = Color(0xFF46A14B)
private val Greenscale600 = Color(0xFF74C278)
private val Greenscale700 = Color(0xFF9DD89F)
private val Greenscale800 = Color(0xFFC8E6C9)
private val Greenscale900 = Color(0xFFF0F6F0)
private val Greenscale950 = Color(0xFFFBFEFB)

// =============================================================
// Color_Base / Sunsetscale
// =============================================================
private val Sunsetscale000 = Color(0xFFBF360C)
private val Sunsetscale100 = Color(0xFFD84315)
private val Sunsetscale200 = Color(0xFFE64A19)
private val Sunsetscale300 = Color(0xFFF4511E)
private val Sunsetscale400 = Color(0xFFFF5722)
private val Sunsetscale500 = Color(0xFFFF7043)
private val Sunsetscale600 = Color(0xFFFF8A65)
private val Sunsetscale700 = Color(0xFFFFAB91)
private val Sunsetscale800 = Color(0xFFFFCCBC)
private val Sunsetscale900 = Color(0xFFFFF3E0)

// =============================================================
// Color_Base / Marinescale
// =============================================================
private val Marinescale000 = Color(0xFF2E4B64)
private val Marinescale100 = Color(0xFF5B7A9C)
private val Marinescale200 = Color(0xFFA4C2E1)
private val Marinescale700 = Color(0xFFD3E7F2)
private val Marinescale800 = Color(0xFFCAE4F3)
private val Marinescale900 = Color(0xFFC1E1F5)

// =============================================================
// Color_Base / Lilacscale
// =============================================================
private val Lilacscale000 = Color(0xFF3B2A75)
private val Lilacscale100 = Color(0xFF5D4BA3)
private val Lilacscale200 = Color(0xFF7F6CD1)
private val Lilacscale700 = Color(0xFFE9E3FF)
private val Lilacscale800 = Color(0xFFDDD0FF)
private val Lilacscale900 = Color(0xFFEBE6FF)

// =============================================================
// Color_Semantic / Status
// =============================================================
private val StatusError   = Color(0xFFFF3059)
private val StatusSuccess = Color(0xFF5DB441)

// =============================================================
// Color_Semantic Schemes
// =============================================================

val normalLightColorScheme = EbbingColors(
    background       = Greyscale950,
    primaryNormal    = Bluescale200,
    primaryDeep      = Bluescale500,
    primaryContainer = Bluescale800,
    statusError      = StatusError,
    statusSuccess    = StatusSuccess,
    textOnBackground = Greyscale000,
    textSub          = Greyscale200,
    textDisabled     = Greyscale400,
    textOnPrimary    = Greyscale950,
    textPrimary      = Bluescale200,
    textError        = StatusError,
    fillNormal       = Greyscale900,
    fillStrong       = Greyscale800,
    fillDisabled     = Greyscale700,
    fillPrimary      = Bluescale200,
    fillFocused      = Greyscale000,
)

val normalDarkColorScheme = EbbingColors(
    background       = Greyscale000,
    primaryNormal    = Bluescale200,
    primaryDeep      = Bluescale500,
    primaryContainer = Bluescale200,
    statusError      = StatusError,
    statusSuccess    = StatusSuccess,
    textOnBackground = Greyscale950,
    textSub          = Greyscale700,
    textDisabled     = Greyscale500,
    textOnPrimary    = Greyscale950,
    textPrimary      = Bluescale500,
    textError        = StatusError,
    fillNormal       = Greyscale100,
    fillStrong       = Greyscale200,
    fillDisabled     = Greyscale300,
    fillPrimary      = Bluescale200,
    fillFocused      = Bluescale200,
)

val forestLightColorScheme = normalLightColorScheme.copy(
    background       = Greenscale950,
    primaryNormal    = Greenscale500,
    primaryDeep      = Greenscale300,
    primaryContainer = Greenscale800,
    textOnBackground = Greenscale000,
    textSub          = Greenscale200,
    textDisabled     = Color(0x992D6830),
    textOnPrimary    = Greenscale950,
    textPrimary      = Greenscale300,
    fillNormal       = Greenscale900,
    fillStrong       = Greenscale800,
    fillDisabled     = Greenscale700,
    fillPrimary      = Greenscale500,
    fillFocused      = Greenscale000,
)

val forestDarkColorScheme = normalDarkColorScheme.copy(
    background       = Greenscale000,
    primaryNormal    = Greenscale600,
    primaryDeep      = Greenscale500,
    primaryContainer = Greenscale300,
    textOnBackground = Greenscale950,
    textSub          = Greenscale700,
    textDisabled     = Color(0x9974C278),
    textOnPrimary    = Greenscale000,
    textPrimary      = Greenscale600,
    fillNormal       = Color(0xFF182D1B),
    fillStrong       = Color(0xFF1A3D1E),
    fillDisabled     = Greenscale200,
    fillPrimary      = Greenscale600,
    fillFocused      = Greenscale300,
)

val sunsetLightColorScheme = normalLightColorScheme.copy(
    background       = Sunsetscale900,
    primaryNormal    = Sunsetscale400,
    primaryDeep      = Sunsetscale200,
    primaryContainer = Sunsetscale800,
    textPrimary      = Sunsetscale200,
    fillNormal       = Sunsetscale900,
    fillStrong       = Sunsetscale800,
    fillDisabled     = Sunsetscale700,
    fillPrimary      = Sunsetscale400,
    fillFocused      = Sunsetscale200,
)

val sunsetDarkColorScheme = normalDarkColorScheme.copy(
    background       = Sunsetscale000,
    primaryNormal    = Sunsetscale700,
    primaryDeep      = Sunsetscale500,
    primaryContainer = Sunsetscale200,
    textPrimary      = Sunsetscale700,
    fillNormal       = Sunsetscale100,
    fillStrong       = Sunsetscale200,
    fillDisabled     = Sunsetscale300,
    fillPrimary      = Sunsetscale700,
    fillFocused      = Sunsetscale200,
)

val marineLightColorScheme = normalLightColorScheme.copy(
    background       = Color(0xFFEAF3F8),
    primaryNormal    = Marinescale200,
    primaryDeep      = Marinescale000,
    primaryContainer = Marinescale700,
    textPrimary      = Marinescale000,
    fillNormal       = Marinescale900,
    fillStrong       = Marinescale800,
    fillDisabled     = Marinescale700,
    fillPrimary      = Marinescale200,
    fillFocused      = Marinescale000,
)

val marineDarkColorScheme = normalDarkColorScheme.copy(
    background       = Color(0xFF263444),
    primaryNormal    = Color(0xFF5EB4E1),
    primaryDeep      = Marinescale200,
    primaryContainer = Marinescale000,
    textPrimary      = Color(0xFF5EB4E1),
    fillNormal       = Marinescale000,
    fillStrong       = Marinescale100,
    fillDisabled     = Marinescale200,
    fillPrimary      = Color(0xFF5EB4E1),
    fillFocused      = Marinescale000,
)

val lilacLightColorScheme = normalLightColorScheme.copy(
    background       = Color(0xFFF2F0FF),
    primaryNormal    = Lilacscale200,
    primaryDeep      = Lilacscale000,
    primaryContainer = Lilacscale700,
    textPrimary      = Lilacscale000,
    fillNormal       = Lilacscale900,
    fillStrong       = Lilacscale800,
    fillDisabled     = Lilacscale700,
    fillPrimary      = Lilacscale200,
    fillFocused      = Lilacscale000,
)

val lilacDarkColorScheme = normalDarkColorScheme.copy(
    background       = Color(0xFF1F1D2E),
    primaryNormal    = Lilacscale900,
    primaryDeep      = Lilacscale800,
    primaryContainer = Lilacscale200,
    textPrimary      = Lilacscale900,
    fillNormal       = Lilacscale000,
    fillStrong       = Lilacscale100,
    fillDisabled     = Lilacscale200,
    fillPrimary      = Lilacscale900,
    fillFocused      = Lilacscale000,
)

// =============================================================
// Color_Semantic Data Class
// =============================================================

@Immutable
data class EbbingColors(
    // Semantic / Background
    val background: Color,
    // Semantic / Primary
    val primaryNormal: Color,
    val primaryDeep: Color,
    val primaryContainer: Color,
    // Semantic / Status
    val statusError: Color,
    val statusSuccess: Color,
    // Semantic / Text
    val textOnBackground: Color,
    val textSub: Color,
    val textDisabled: Color,
    val textOnPrimary: Color,
    val textPrimary: Color,
    val textError: Color,
    // Semantic / Fill
    val fillNormal: Color,
    val fillStrong: Color,
    val fillDisabled: Color,
    val fillPrimary: Color,
    val fillFocused: Color,
)
