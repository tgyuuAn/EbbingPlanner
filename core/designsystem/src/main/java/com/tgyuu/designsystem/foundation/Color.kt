package com.tgyuu.designsystem.foundation

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

private val PrimaryDefault = Color(0xFF0F4C75)
private val PrimaryMiddle = Color(0xFF3282B8)
private val PrimaryLight = Color(0xFFBBE1FA)

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

@Immutable
data class EbbingColors(
    val background: Color = LightBackground,
    val primaryDefault: Color = PrimaryDefault,
    val primaryMiddle: Color = PrimaryMiddle,
    val primaryLight: Color = PrimaryLight,
    val black: Color = Black,
    val dark1: Color = Dark1,
    val dark2: Color = Dark2,
    val dark3: Color = Dark3,
    val light1: Color = Light1,
    val light2: Color = Light2,
    val light3: Color = Light3,
    val white: Color = White,
    val error: Color = Error,
)

internal val darkModeColorScheme = EbbingColors(
    primaryDefault = PrimaryLight,
    primaryLight = PrimaryDefault,
    background = DarkBackground,
    black = White,
    white = Black,
    light3 = Dark2,
    dark1 = Light1,
    dark2 = Light2,
)
internal val lightModeColorScheme = EbbingColors()
