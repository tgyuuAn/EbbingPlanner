package com.tgyuu.designsystem.component.calendar

import java.time.DayOfWeek
import java.time.LocalDate

fun DayOfWeek.toShortLabel(): String =
    getDisplayName(java.time.format.TextStyle.SHORT, java.util.Locale.getDefault())

fun yearMonthDiff(from: LocalDate, to: LocalDate): Int {
    return (to.year - from.year) * 12 + (to.monthValue - from.monthValue)
}

internal const val COLOR_ANIM_THRESHOLD = 5L * 1024 * 1024
