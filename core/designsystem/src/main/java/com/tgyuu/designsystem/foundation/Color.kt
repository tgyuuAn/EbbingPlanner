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


internal val normalLightColorScheme = EbbingColors(
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
internal val normalDarkColorScheme = EbbingColors(
    background = DarkBackground,
    primaryDefault = PrimaryLight,
    primaryMiddle = PrimaryMiddle,
    primaryLight = PrimaryDefault,
    black = White,
    dark1 = Light1,
    dark2 = Light2,
    dark3 = Dark3,
    light1 = Light1,
    light2 = Light2,
    light3 = Dark2,
    white = Black,
    error = Error,
    success = Success,
)

internal val forestLightColorScheme = EbbingColors(
    primaryDefault = Color(0xFF2E7D32),
    primaryMiddle = Color(0xFF388E3C),
    primaryLight = Color(0xFF81C784),
    background = Color(0xFFE8F5E9),
    black = Color(0xFF1B5E20),
    white = Color(0xFFFFFFFF),
    dark1 = Color(0xFF4CAF50),
    dark2 = Color(0xFF66BB6A),
    dark3 = Color(0xFF81C784),
    light1 = Color(0xFFA5D6A7),
    light2 = Color(0xFFC8E6C9),
    light3 = Color(0xFFE8F5E9),
    error = Color(0xFFD32F2F),
    success = Color(0xFF388E3C),
)
internal val forestDarkColorScheme = EbbingColors(
    primaryDefault = Color(0xFF81C784),
    primaryMiddle = Color(0xFF4CAF50),
    primaryLight = Color(0xFF2E7D32),
    background = Color(0xFF1B5E20),
    black = Color(0xFFE8F5E9),
    white = Color(0xFF1B5E20),
    dark1 = Color(0xFF66BB6A),
    dark2 = Color(0xFF4CAF50),
    dark3 = Color(0xFF388E3C),
    light1 = Color(0xFF2E7D32),
    light2 = Color(0xFF1B5E20),
    light3 = Color(0xFF0D3B12),
    error = Color(0xFFEF5350),
    success = Color(0xFF66BB6A),
)

internal val sunsetLightColorScheme = EbbingColors(
    primaryDefault = Color(0xFFF4511E),
    primaryMiddle = Color(0xFFFF8A65),
    primaryLight = Color(0xFFFFCCBC),
    background = Color(0xFFFFF3E0),
    black = Color(0xFF4E342E),
    white = Color(0xFFFFFFFF),
    dark1 = Color(0xFFD84315),
    dark2 = Color(0xFFE64A19),
    dark3 = Color(0xFFFB8C00),
    light1 = Color(0xFFFFAB91),
    light2 = Color(0xFFFFD7B0),
    light3 = Color(0xFFFFF3E0),
    error = Color(0xFFC62828),
    success = Color(0xFF7CB342),
)
internal val sunsetDarkColorScheme = EbbingColors(
    primaryDefault = Color(0xFFFFAB91),
    primaryMiddle = Color(0xFFFF8A65),
    primaryLight = Color(0xFFF4511E),
    background = Color(0xFF4E342E),
    black = Color(0xFFFFF3E0),
    white = Color(0xFF4E342E),
    dark1 = Color(0xFFFF7043),
    dark2 = Color(0xFFF4511E),
    dark3 = Color(0xFFD84315),
    light1 = Color(0xFFBF360C),
    light2 = Color(0xFF4E342E),
    light3 = Color(0xFF2E1F1C),
    error = Color(0xFFE53935),
    success = Color(0xFF8E24AA),
)

internal val pastelLightColorScheme = EbbingColors(
    primaryDefault = Color(0xFF80DEEA),
    primaryMiddle = Color(0xFF4DD0E1),
    primaryLight = Color(0xFFB2EBF2),
    background = Color(0xFFE0F7FA),
    black = Color(0xFF006064),
    white = Color(0xFFFFFFFF),
    dark1 = Color(0xFF26C6DA),
    dark2 = Color(0xFF00ACC1),
    dark3 = Color(0xFF00838F),
    light1 = Color(0xFF4DD0E1),
    light2 = Color(0xFF80DEEA),
    light3 = Color(0xFFB2EBF2),
    error = Color(0xFFE57373),
    success = Color(0xFF81C784),
)
internal val pastelDarkColorScheme = EbbingColors(
    primaryDefault = Color(0xFFB2EBF2),
    primaryMiddle = Color(0xFF4DD0E1),
    primaryLight = Color(0xFF00838F),
    background = Color(0xFF006064),
    black = Color(0xFFE0F7FA),
    white = Color(0xFF006064),
    dark1 = Color(0xFF26C6DA),
    dark2 = Color(0xFF00ACC1),
    dark3 = Color(0xFF00838F),
    light1 = Color(0xFF4DD0E1),
    light2 = Color(0xFF80DEEA),
    light3 = Color(0xFFB2EBF2),
    error = Color(0xFFE53935),
    success = Color(0xFF66BB6A),
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
