package com.tgyuu.designsystem.component.calendar

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.number

// kotlinx-datetime 0.7부터 DayOfWeek가 java.time typealias가 아니므로 변환 후 표시명 조회
fun DayOfWeek.toShortLabel(): String =
    java.time.DayOfWeek.of(isoDayNumber)
        .getDisplayName(java.time.format.TextStyle.SHORT, java.util.Locale.getDefault())

fun yearMonthDiff(from: LocalDate, to: LocalDate): Int {
    return (to.year - from.year) * 12 + (to.monthNumber - from.monthNumber)
}

val EbbingDayOfWeek = listOf(
    DayOfWeek.SUNDAY, DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
    DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY,
)

private val EbbingDayOfWeekSunday = EbbingDayOfWeek

private val EbbingDayOfWeekMonday = listOf(
    DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY,
    DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY,
)

fun getEbbingDayOfWeek(startFromMonday: Boolean): List<DayOfWeek> =
    if (startFromMonday) EbbingDayOfWeekMonday else EbbingDayOfWeekSunday

internal const val COLOR_ANIM_THRESHOLD = 5L * 1024 * 1024
