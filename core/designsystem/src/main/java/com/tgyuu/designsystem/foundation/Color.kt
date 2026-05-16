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
private val Greyscale600 = Color(0xFFA1AABB)
private val Greyscale700 = Color(0xFFBCC6D4)
private val Greyscale800 = Color(0xFFE8EBF0)
private val Greyscale900 = Color(0xFFF4F6FA)
private val Greyscale950 = Color(0xFFFFFFFF)

// =============================================================
// Color_Base / Bluescale
// =============================================================
private val Bluescale000 = Color(0xFF091F31)
private val Bluescale100 = Color(0xFF134467)
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
private val Greenscale100 = Color(0xFF1E251E)
private val Greenscale200 = Color(0xFF344435)
private val Greenscale300 = Color(0xFF2D6830)
private val Greenscale400 = Color(0xFF358439)
private val Greenscale500 = Color(0xFF46A14B)
private val Greenscale600 = Color(0xFF81C784)
private val Greenscale700 = Color(0xFF9DD89F)
private val Greenscale800 = Color(0xFFC8EAC9)
private val Greenscale900 = Color(0xFFF0F6F0)
private val Greenscale950 = Color(0xFFFBFEFB)

// =============================================================
// Color_Base / Orangescale (Sunset)
// =============================================================
private val Orangescale000 = Color(0xFF181313)
private val Orangescale100 = Color(0xFF211918)
private val Orangescale200 = Color(0xFF6F3833)
private val Orangescale300 = Color(0xFF944136)
private val Orangescale400 = Color(0xFFE53611)
private val Orangescale500 = Color(0xFFF4511E)
private val Orangescale600 = Color(0xFFF77340)
private val Orangescale700 = Color(0xFFFAA477)
private val Orangescale800 = Color(0xFFF7D5C1)
private val Orangescale900 = Color(0xFFFCEFE6)
private val Orangescale950 = Color(0xFFFFFCFA)

// =============================================================
// Color_Base / Purplescale (Lilac)
// =============================================================
private val Purplescale000 = Color(0xFF151418)
private val Purplescale100 = Color(0xFF22212A)
private val Purplescale200 = Color(0xFF474358)
private val Purplescale300 = Color(0xFF5E5779)
private val Purplescale400 = Color(0xFF7F6CD1)
private val Purplescale500 = Color(0xFF8479DB)
private val Purplescale600 = Color(0xFF9693E6)
private val Purplescale700 = Color(0xFFB2B5EF)
private val Purplescale800 = Color(0xFFCFD2F6)
private val Purplescale900 = Color(0xFFEEEFFC)
private val Purplescale950 = Color(0xFFFBFBFE)

// =============================================================
// Color_Semantic / Status
// =============================================================
private val StatusError   = Color(0xFFFF3059)
private val StatusSuccess = Color(0xFF5DB441)

// =============================================================
// Color_Semantic Schemes
// =============================================================

// --- Normal (Greyscale neutral + Bluescale primary) ---

val normalLightColorScheme = EbbingColors(
    background       = Greyscale950,
    primaryNormal    = Bluescale200,
    primaryDeep      = Bluescale100,
    statusError      = StatusError,
    statusSuccess    = StatusSuccess,
    textOnBackground = Greyscale000,
    textSub          = Greyscale300,
    textDisabled     = Greyscale500,
    textOnPrimary    = Greyscale950,
    textPrimary      = Bluescale200,
    textError        = StatusError,
    fillNormal       = Greyscale950,
    fillTextfield    = Greyscale900,
    fillSelected     = Bluescale900,
    fillDisabled     = Greyscale600,
    fillPrimary      = Bluescale200,
    fillFocused      = Greyscale100,
    fillError        = StatusError,
    strokePrimary    = Bluescale200,
    strokeNormal     = Greyscale800,
    strokeSecondary  = Greyscale900,
    strokeOutline    = Greyscale800,
    strokeOnPrimary  = Greyscale950,
    strokeIcon       = Greyscale300,
    strokeDisabled   = Greyscale500,
    materialDimmer   = Color(0xB3070808),
)

val normalDarkColorScheme = EbbingColors(
    background       = Greyscale000,
    primaryNormal    = Bluescale600,
    primaryDeep      = Bluescale700,
    statusError      = StatusError,
    statusSuccess    = StatusSuccess,
    textOnBackground = Greyscale950,
    textSub          = Greyscale700,
    textDisabled     = Greyscale500,
    textOnPrimary    = Greyscale000,
    textPrimary      = Bluescale600,
    textError        = StatusError,
    fillNormal       = Greyscale000,
    fillTextfield    = Greyscale100,
    fillSelected     = Bluescale100,
    fillDisabled     = Greyscale200,
    fillPrimary      = Bluescale600,
    fillFocused      = Bluescale900,
    fillError        = StatusError,
    strokePrimary    = Bluescale600,
    strokeNormal     = Greyscale200,
    strokeSecondary  = Greyscale100,
    strokeOutline    = Greyscale200,
    strokeOnPrimary  = Greyscale000,
    strokeIcon       = Greyscale700,
    strokeDisabled   = Greyscale500,
    materialDimmer   = Color(0xB3070808),
)

// --- Forest (Greenscale for both neutral + primary) ---

val forestLightColorScheme = EbbingColors(
    background       = Greenscale950,
    primaryNormal    = Greenscale500,
    primaryDeep      = Greenscale400,
    statusError      = StatusError,
    statusSuccess    = StatusSuccess,
    textOnBackground = Greenscale000,
    textSub          = Greenscale300,
    textDisabled     = Greyscale500,
    textOnPrimary    = Greenscale950,
    textPrimary      = Greenscale500,
    textError        = StatusError,
    fillNormal       = Greenscale950,
    fillTextfield    = Greenscale900,
    fillSelected     = Greenscale900,
    fillDisabled     = Greenscale700,
    fillPrimary      = Greenscale500,
    fillFocused      = Greenscale100,
    fillError        = StatusError,
    strokePrimary    = Greenscale500,
    strokeNormal     = Greenscale800,
    strokeSecondary  = Greenscale900,
    strokeOutline    = Greenscale800,
    strokeOnPrimary  = Greenscale900,
    strokeIcon       = Greenscale300,
    strokeDisabled   = Greyscale500,
    materialDimmer   = Color(0xB3070808),
)

val forestDarkColorScheme = EbbingColors(
    background       = Greenscale000,
    primaryNormal    = Greenscale600,
    primaryDeep      = Greenscale700,
    statusError      = StatusError,
    statusSuccess    = StatusSuccess,
    textOnBackground = Greenscale950,
    textSub          = Greenscale800,
    textDisabled     = Greyscale500,
    textOnPrimary    = Greenscale000,
    textPrimary      = Greenscale600,
    textError        = StatusError,
    fillNormal       = Greenscale000,
    fillTextfield    = Greenscale100,
    fillSelected     = Greenscale200,
    fillDisabled     = Greenscale300,
    fillPrimary      = Greenscale600,
    fillFocused      = Greenscale900,
    fillError        = StatusError,
    strokePrimary    = Greenscale600,
    strokeNormal     = Greenscale200,
    strokeSecondary  = Greenscale100,
    strokeOutline    = Greenscale200,
    strokeOnPrimary  = Greenscale100,
    strokeIcon       = Greenscale700,
    strokeDisabled   = Greyscale500,
    materialDimmer   = Color(0xB3070808),
)

// --- Sunset (Orangescale for both neutral + primary) ---

val sunsetLightColorScheme = EbbingColors(
    background       = Orangescale950,
    primaryNormal    = Orangescale500,
    primaryDeep      = Orangescale400,
    statusError      = StatusError,
    statusSuccess    = StatusSuccess,
    textOnBackground = Orangescale000,
    textSub          = Orangescale300,
    textDisabled     = Greyscale500,
    textOnPrimary    = Orangescale950,
    textPrimary      = Orangescale500,
    textError        = StatusError,
    fillNormal       = Orangescale950,
    fillTextfield    = Orangescale900,
    fillSelected     = Orangescale900,
    fillDisabled     = Orangescale700,
    fillPrimary      = Orangescale500,
    fillFocused      = Orangescale100,
    fillError        = StatusError,
    strokePrimary    = Orangescale500,
    strokeNormal     = Orangescale800,
    strokeSecondary  = Orangescale900,
    strokeOutline    = Orangescale800,
    strokeOnPrimary  = Orangescale900,
    strokeIcon       = Orangescale300,
    strokeDisabled   = Greyscale500,
    materialDimmer   = Color(0xB3070808),
)

val sunsetDarkColorScheme = EbbingColors(
    background       = Orangescale000,
    primaryNormal    = Orangescale600,
    primaryDeep      = Orangescale700,
    statusError      = StatusError,
    statusSuccess    = StatusSuccess,
    textOnBackground = Orangescale950,
    textSub          = Orangescale800,
    textDisabled     = Greyscale500,
    textOnPrimary    = Orangescale000,
    textPrimary      = Orangescale600,
    textError        = StatusError,
    fillNormal       = Orangescale000,
    fillTextfield    = Orangescale100,
    fillSelected     = Orangescale200,
    fillDisabled     = Orangescale300,
    fillPrimary      = Orangescale600,
    fillFocused      = Orangescale900,
    fillError        = StatusError,
    strokePrimary    = Orangescale600,
    strokeNormal     = Orangescale200,
    strokeSecondary  = Orangescale100,
    strokeOutline    = Orangescale200,
    strokeOnPrimary  = Orangescale100,
    strokeIcon       = Orangescale700,
    strokeDisabled   = Greyscale500,
    materialDimmer   = Color(0xB3070808),
)

// --- Marine (Bluescale for both neutral + primary) ---

val marineLightColorScheme = EbbingColors(
    background       = Bluescale900,
    primaryNormal    = Bluescale500,
    primaryDeep      = Bluescale400,
    statusError      = StatusError,
    statusSuccess    = StatusSuccess,
    textOnBackground = Bluescale000,
    textSub          = Bluescale300,
    textDisabled     = Greyscale500,
    textOnPrimary    = Bluescale900,
    textPrimary      = Bluescale500,
    textError        = StatusError,
    fillNormal       = Bluescale900,
    fillTextfield    = Bluescale800,
    fillSelected     = Bluescale800,
    fillDisabled     = Bluescale700,
    fillPrimary      = Bluescale500,
    fillFocused      = Bluescale100,
    fillError        = StatusError,
    strokePrimary    = Bluescale500,
    strokeNormal     = Bluescale800,
    strokeSecondary  = Bluescale900,
    strokeOutline    = Bluescale800,
    strokeOnPrimary  = Bluescale900,
    strokeIcon       = Bluescale300,
    strokeDisabled   = Greyscale500,
    materialDimmer   = Color(0xB3070808),
)

val marineDarkColorScheme = EbbingColors(
    background       = Bluescale000,
    primaryNormal    = Bluescale600,
    primaryDeep      = Bluescale700,
    statusError      = StatusError,
    statusSuccess    = StatusSuccess,
    textOnBackground = Bluescale900,
    textSub          = Bluescale800,
    textDisabled     = Greyscale500,
    textOnPrimary    = Bluescale000,
    textPrimary      = Bluescale600,
    textError        = StatusError,
    fillNormal       = Bluescale000,
    fillTextfield    = Bluescale100,
    fillSelected     = Bluescale200,
    fillDisabled     = Bluescale300,
    fillPrimary      = Bluescale600,
    fillFocused      = Bluescale900,
    fillError        = StatusError,
    strokePrimary    = Bluescale600,
    strokeNormal     = Bluescale200,
    strokeSecondary  = Bluescale100,
    strokeOutline    = Bluescale200,
    strokeOnPrimary  = Bluescale100,
    strokeIcon       = Bluescale700,
    strokeDisabled   = Greyscale500,
    materialDimmer   = Color(0xB3070808),
)

// --- Lilac (Purplescale for both neutral + primary) ---

val lilacLightColorScheme = EbbingColors(
    background       = Purplescale950,
    primaryNormal    = Purplescale500,
    primaryDeep      = Purplescale400,
    statusError      = StatusError,
    statusSuccess    = StatusSuccess,
    textOnBackground = Purplescale000,
    textSub          = Purplescale300,
    textDisabled     = Greyscale500,
    textOnPrimary    = Purplescale950,
    textPrimary      = Purplescale500,
    textError        = StatusError,
    fillNormal       = Purplescale950,
    fillTextfield    = Purplescale900,
    fillSelected     = Purplescale900,
    fillDisabled     = Purplescale700,
    fillPrimary      = Purplescale500,
    fillFocused      = Purplescale100,
    fillError        = StatusError,
    strokePrimary    = Purplescale500,
    strokeNormal     = Purplescale800,
    strokeSecondary  = Purplescale900,
    strokeOutline    = Purplescale800,
    strokeOnPrimary  = Purplescale900,
    strokeIcon       = Purplescale300,
    strokeDisabled   = Greyscale500,
    materialDimmer   = Color(0xB3070808),
)

val lilacDarkColorScheme = EbbingColors(
    background       = Purplescale000,
    primaryNormal    = Purplescale600,
    primaryDeep      = Purplescale700,
    statusError      = StatusError,
    statusSuccess    = StatusSuccess,
    textOnBackground = Purplescale950,
    textSub          = Purplescale800,
    textDisabled     = Greyscale500,
    textOnPrimary    = Purplescale000,
    textPrimary      = Purplescale600,
    textError        = StatusError,
    fillNormal       = Purplescale000,
    fillTextfield    = Purplescale100,
    fillSelected     = Purplescale200,
    fillDisabled     = Purplescale300,
    fillPrimary      = Purplescale600,
    fillFocused      = Purplescale900,
    fillError        = StatusError,
    strokePrimary    = Purplescale600,
    strokeNormal     = Purplescale200,
    strokeSecondary  = Purplescale100,
    strokeOutline    = Purplescale200,
    strokeOnPrimary  = Purplescale100,
    strokeIcon       = Purplescale700,
    strokeDisabled   = Greyscale500,
    materialDimmer   = Color(0xB3070808),
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
    // Semantic / Status
    val statusError: Color,
    val statusSuccess: Color,
    // Semantic / Usage / Text
    val textOnBackground: Color,
    val textSub: Color,
    val textDisabled: Color,
    val textOnPrimary: Color,
    val textPrimary: Color,
    val textError: Color,
    // Semantic / Usage / Fill
    val fillNormal: Color,
    val fillTextfield: Color,
    val fillSelected: Color,
    val fillDisabled: Color,
    val fillPrimary: Color,
    val fillFocused: Color,
    val fillError: Color,
    // Semantic / Usage / Stroke
    val strokePrimary: Color,
    val strokeNormal: Color,
    val strokeSecondary: Color,
    val strokeOutline: Color,
    val strokeOnPrimary: Color,
    val strokeIcon: Color,
    val strokeDisabled: Color,
    // Semantic / Usage / Material
    val materialDimmer: Color,
)
