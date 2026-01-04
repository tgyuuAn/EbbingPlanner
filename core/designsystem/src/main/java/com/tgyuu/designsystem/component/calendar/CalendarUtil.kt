package com.tgyuu.designsystem.component.calendar

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.number

fun DayOfWeek.toKorean(): String = when (this) {
    DayOfWeek.SUNDAY -> "일"
    DayOfWeek.MONDAY -> "월"
    DayOfWeek.TUESDAY -> "화"
    DayOfWeek.WEDNESDAY -> "수"
    DayOfWeek.THURSDAY -> "목"
    DayOfWeek.FRIDAY -> "금"
    DayOfWeek.SATURDAY -> "토"
}

fun yearMonthDiff(from: LocalDate, to: LocalDate): Int {
    return (to.year - from.year) * 12 + (to.monthNumber - from.monthNumber)
}

internal const val COLOR_ANIM_THRESHOLD = 5L * 1024 * 1024
