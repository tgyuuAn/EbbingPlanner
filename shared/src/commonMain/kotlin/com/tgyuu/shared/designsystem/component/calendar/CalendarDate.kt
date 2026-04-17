package com.tgyuu.shared.designsystem.component.calendar

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.plus

val EbbingDayOfWeekSunday = listOf(
    DayOfWeek.SUNDAY,
    DayOfWeek.MONDAY,
    DayOfWeek.TUESDAY,
    DayOfWeek.WEDNESDAY,
    DayOfWeek.THURSDAY,
    DayOfWeek.FRIDAY,
    DayOfWeek.SATURDAY,
)

val EbbingDayOfWeekMonday = listOf(
    DayOfWeek.MONDAY,
    DayOfWeek.TUESDAY,
    DayOfWeek.WEDNESDAY,
    DayOfWeek.THURSDAY,
    DayOfWeek.FRIDAY,
    DayOfWeek.SATURDAY,
    DayOfWeek.SUNDAY,
)

// Keep for backward compatibility
val EbbingDayOfWeek = EbbingDayOfWeekSunday

data class CalendarDate(
    val date: LocalDate,
    val isCurrentMonth: Boolean,
) {
    val dayOfMonth: Int = date.dayOfMonth
}

fun getCalendarDates(date: LocalDate, startFromMonday: Boolean = false): List<CalendarDate> {
    val leadingCount = getLeadingEmptyCellCount(date, startFromMonday)
    val current = getCurrentMonthDatesToShow(date)
    val trailingCount = 42 - leadingCount - current.size

    val previous = getPreviousMonthDatesToShowByCount(date, leadingCount)
    val next = getNextMonthDatesToShowByCount(date, trailingCount)

    return previous + current + next
}

private fun getPreviousMonthDatesToShowByCount(date: LocalDate, count: Int): List<CalendarDate> {
    if (count == 0) return emptyList()
    val previousMonth = LocalDate(date.year, date.monthNumber, 1).minus(1, DateTimeUnit.MONTH)
    val lastDay = previousMonth.totalDaysInMonth()
    return ((lastDay - count + 1)..lastDay).map {
        CalendarDate(LocalDate(previousMonth.year, previousMonth.monthNumber, it), isCurrentMonth = false)
    }
}

private fun getCurrentMonthDatesToShow(date: LocalDate): List<CalendarDate> {
    val firstDayOfMonth = LocalDate(date.year, date.monthNumber, 1)
    val lastDay = firstDayOfMonth.totalDaysInMonth()

    return (1..lastDay).map { day ->
        CalendarDate(
            date = LocalDate(date.year, date.monthNumber, day),
            isCurrentMonth = true,
        )
    }
}

private fun getNextMonthDatesToShowByCount(date: LocalDate, count: Int): List<CalendarDate> {
    if (count <= 0) return emptyList()
    val nextMonth = LocalDate(date.year, date.monthNumber, 1).plus(1, DateTimeUnit.MONTH)
    return (1..count).map {
        CalendarDate(date = LocalDate(nextMonth.year, nextMonth.monthNumber, it), isCurrentMonth = false)
    }
}

fun getLeadingEmptyCellCount(date: LocalDate, startFromMonday: Boolean): Int {
    val firstDayOfWeek = LocalDate(date.year, date.monthNumber, 1).dayOfWeek
    return if (startFromMonday) {
        when (firstDayOfWeek) {
            DayOfWeek.MONDAY -> 0
            DayOfWeek.TUESDAY -> 1
            DayOfWeek.WEDNESDAY -> 2
            DayOfWeek.THURSDAY -> 3
            DayOfWeek.FRIDAY -> 4
            DayOfWeek.SATURDAY -> 5
            DayOfWeek.SUNDAY -> 6
            else -> 0
        }
    } else {
        when (firstDayOfWeek) {
            DayOfWeek.SUNDAY -> 0
            DayOfWeek.MONDAY -> 1
            DayOfWeek.TUESDAY -> 2
            DayOfWeek.WEDNESDAY -> 3
            DayOfWeek.THURSDAY -> 4
            DayOfWeek.FRIDAY -> 5
            DayOfWeek.SATURDAY -> 6
            else -> 0
        }
    }
}

private fun getFirstDayOfWeek(date: LocalDate): DayOfWeek {
    return LocalDate(date.year, date.monthNumber, 1).dayOfWeek
}

fun LocalDate.totalDaysInMonth(): Int {
    val nextMonth = this.plus(1, DateTimeUnit.MONTH).run { LocalDate(year, monthNumber, 1) }
    val lastDayOfMonth = nextMonth.minus(1, DateTimeUnit.DAY)
    return lastDayOfMonth.dayOfMonth
}

fun yearMonthDiff(from: LocalDate, to: LocalDate): Int {
    return (to.year - from.year) * 12 + (to.monthNumber - from.monthNumber)
}

fun DayOfWeek.toKorean(): String = when (this) {
    DayOfWeek.SUNDAY -> "일"
    DayOfWeek.MONDAY -> "월"
    DayOfWeek.TUESDAY -> "화"
    DayOfWeek.WEDNESDAY -> "수"
    DayOfWeek.THURSDAY -> "목"
    DayOfWeek.FRIDAY -> "금"
    DayOfWeek.SATURDAY -> "토"
    else -> ""
}
