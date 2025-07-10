package com.tgyuu.designsystem.foundation

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

val PrimaryDefault = Color(0xFF0F4C75)
private val PrimaryMiddle = Color(0xFF3282B8)
val PrimaryLight = Color(0xFFBBE1FA)
val LightBackground = Color(0xFFFFFFFF)
val DarkBackground = Color(0xFF262729)
private val Black = Color(0xFF1B1A2A)
private val Dark1 = Color(0xFF484B4D)
private val Dark2 = Color(0xFF6C7073)
private val Dark3 = Color(0xFF909599)
private val Light1 = Color(0xFFCBD1D9)
private val Light2 = Color(0xFFE8EBF0)
private val Light3 = Color(0xFFF4F6FA)
private val White = Color(0xFFFFFFFF)
private val Error = Color(0xFFFF3059)
private val Success = Color(0xFF5DB441)

val normalLightColorScheme = EbbingColors(
    background = LightBackground,
    primaryDefault = PrimaryDefault,
    primaryMiddle = PrimaryMiddle,
    primaryLight = PrimaryLight,
    black = Black,
    dark1 = Dark1,
    dark2 = Dark2,
    dark3 = Dark3,
    light1 = Light1,
    light2 = Light2,
    light3 = Light3,
    white = White,
    error = Error,
    success = Success,
)
val normalDarkColorScheme = EbbingColors(
    background = DarkBackground,
    primaryDefault = PrimaryLight,
    primaryMiddle = PrimaryMiddle,
    primaryLight = PrimaryDefault,
    black = White,
    white = Black,
    dark1 = Light1,
    dark2 = Light2,
    dark3 = Dark3,
    light1 = Light1,
    light2 = Light2,
    light3 = Dark2,
    error = Error,
    success = Success,
)

val forestLightColorScheme = EbbingColors(
    background = Color(0xFFE8F5E9),
    primaryDefault = Color(0xFF2E7D32),
    primaryMiddle = Color(0xFF388E3C),
    primaryLight = Color(0xFF81C784),
    black = Black,
    white = White,
    dark1 = Dark1,
    dark2 = Color(0xFF4CAF50),
    dark3 = Color(0xFF66BB6A),
    light1 = Color(0xFFC3E6C5),
    light2 = Color(0xFFAEDBB0),
    light3 = Color(0xFFD8EDD9),
    error = Error,
    success = Success,
)
val forestDarkColorScheme = EbbingColors(
    primaryDefault = Color(0xFF81C784),
    primaryMiddle = Color(0xFF4CAF50),
    primaryLight = Color(0xFF2E7D32),
    background = Color(0xFF1B5E20),
    black = White,
    white = Black,
    dark1 = Light1,
    dark2 = Color(0xFF4CAF50),
    dark3 = Color(0xFF388E3C),
    light1 = Color(0xFF2E7D32),
    light2 = Color(0xFF1B5E20),
    light3 = Color(0xFF0D3B12),
    error = Error,
    success = Success,
)

val sunsetLightColorScheme = EbbingColors(
    primaryDefault = Color(0xFFF4511E),
    primaryMiddle = Color(0xFFFF8A65),
    primaryLight = Color(0xFFFFCCBC),
    background = Color(0xFFFFF3E0),
    black = Black,
    white = White,
    dark1 = Dark1,
    dark2 = Color(0xFFE64A19),
    dark3 = Color(0xFFFB8C00),
    light1 = Color(0xFFFFAB91),
    light2 = Color(0xFFFFD7B0),
    light3 = Color(0xFFFFE0B2),
    error = Error,
    success = Success,
)
val sunsetDarkColorScheme = EbbingColors(
    primaryDefault = Color(0xFFFFAB91),
    primaryMiddle = Color(0xFFFF8A65),
    primaryLight = Color(0xFFF4511E),
    background = Color(0xFF4E342E),
    black = White,
    white = White,
    dark1 = Light1,
    dark2 = Color(0xFFF4511E),
    dark3 = Color(0xFFD84315),
    light1 = Color(0xFFBF360C),
    light2 = Color(0xFF4E342E),
    light3 = Color(0xFF2E1F1C),
    error = Error,
    success = Success,
)

val marineLightColorScheme = normalLightColorScheme.copy(
    background = Color(0xFFEAF3F8),
    light1 = Color(0xFFD3E7F2),
    light2 = Color(0xFFCAE4F3),
    light3 = Color(0xFFC1E1F5),
    primaryLight = Color(0xFFD3E7F2),
    primaryMiddle = Color(0xFFA7CDEF),
    primaryDefault = Color(0xFF5EB4E1)
)
val marineDarkColorScheme = normalDarkColorScheme.copy(
    background = Color(0xFF263444),
    primaryDefault = Color(0xFFA4C2E1),
    primaryMiddle = Color(0xFF5B7A9C),
    primaryLight = Color(0xFF2E4B64),
    light1 = Color(0xFFA4C2E1),
    light2 = Color(0xFF5B7A9C),
    light3 = Color(0xFF2E4B64),
)

val lilacLightColorScheme = normalLightColorScheme.copy(
    background     = Color(0xFFF2F0FF),  // 1. 약간 진해진 연보라
    light1         = Color(0xFFE9E3FF),  // 2. 연보라
    light2         = Color(0xFFDDD0FF),  // 3. 미디엄 라벤더
    light3         = Color(0xFFEBE6FF),  // 4. 이전보다 더 연하게 조정
    primaryLight   = Color(0xFFE9E3FF),
    primaryMiddle  = Color(0xFFDDD0FF),
    primaryDefault = Color(0xFFEBE6FF),
)
val lilacDarkColorScheme = normalDarkColorScheme.copy(
    background     = Color(0xFF1F1D2E),  // 1. 훨씬 진해진 다크 네이비퍼플
    light1         = Color(0xFF7F6CD1),  // 2. 어두운 연보라
    light2         = Color(0xFF5D4BA3),  // 3. 어두운 라벤더
    light3         = Color(0xFF3B2A75),  // 4. 가장 어두운 퍼플
    primaryLight   = Color(0xFF7F6CD1),
    primaryMiddle  = Color(0xFF5D4BA3),
    primaryDefault = Color(0xFF3B2A75),
)

@Immutable
data class EbbingColors(
    val background: Color,
    val primaryDefault: Color,
    val primaryMiddle: Color,
    val primaryLight: Color,
    val black: Color,
    val dark1: Color,
    val dark2: Color,
    val dark3: Color,
    val light1: Color,
    val light2: Color,
    val light3: Color,
    val white: Color,
    val error: Color,
    val success: Color,
)
