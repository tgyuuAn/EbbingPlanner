package com.tgyuu.designsystem.component.calendar

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.number

fun DayOfWeek.toShortLabel(): String =
    getDisplayName(java.time.format.TextStyle.SHORT, java.util.Locale.getDefault())

fun yearMonthDiff(from: LocalDate, to: LocalDate): Int {
    return (to.year - from.year) * 12 + (to.monthNumber - from.monthNumber)
}

internal const val COLOR_ANIM_THRESHOLD = 5L * 1024 * 1024
