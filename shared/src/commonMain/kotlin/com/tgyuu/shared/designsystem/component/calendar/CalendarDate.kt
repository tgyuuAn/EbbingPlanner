package com.tgyuu.shared.designsystem.component.calendar

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.plus

val EbbingDayOfWeek = listOf(
    DayOfWeek.SUNDAY,
    DayOfWeek.MONDAY,
    DayOfWeek.TUESDAY,
    DayOfWeek.WEDNESDAY,
    DayOfWeek.THURSDAY,
    DayOfWeek.FRIDAY,
    DayOfWeek.SATURDAY,
)

data class CalendarDate(
    val date: LocalDate,
    val isCurrentMonth: Boolean,
) {
    val dayOfMonth: Int = date.dayOfMonth
}

fun getCalendarDates(date: LocalDate): List<CalendarDate> {
    val previous = getPreviousMonthDatesToShow(date)
    val current = getCurrentMonthDatesToShow(date)
    val next = getNextMonthDatesToShow(date)

    return previous + current + next
}

private fun getPreviousMonthDatesToShow(date: LocalDate): List<CalendarDate> {
    val firstDayOfMonth = LocalDate(date.year, date.monthNumber, 1)
    val previousMonth = LocalDate(firstDayOfMonth.year, firstDayOfMonth.monthNumber, 1)
        .minus(1, DateTimeUnit.MONTH)
    val lastDayOfPreviousMonth = previousMonth.totalDaysInMonth()
    val count = getPreviousMonthDayOfWeeksToShow(date).size

    return ((lastDayOfPreviousMonth - count + 1)..lastDayOfPreviousMonth).map {
        CalendarDate(
            LocalDate(previousMonth.year, previousMonth.monthNumber, it),
            isCurrentMonth = false
        )
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

private fun getNextMonthDatesToShow(date: LocalDate): List<CalendarDate> {
    val totalDayCountUntilNextMonth =
        getPreviousMonthDatesToShow(date).size + getCurrentMonthDatesToShow(date).size
    val remainCount = 42 - totalDayCountUntilNextMonth

    val nextMonth = LocalDate(date.year, date.monthNumber, 1).plus(1, DateTimeUnit.MONTH)

    return (1..remainCount).map {
        CalendarDate(
            date = LocalDate(nextMonth.year, nextMonth.monthNumber, it),
            isCurrentMonth = false
        )
    }
}

private fun getPreviousMonthDayOfWeeksToShow(date: LocalDate): List<DayOfWeek> {
    val firstDayOfWeek = getFirstDayOfWeek(date)
    // Convert DayOfWeek to Sunday-based index (Sunday = 0)
    val sundayBasedIndex = when (firstDayOfWeek) {
        DayOfWeek.SUNDAY -> 0
        DayOfWeek.MONDAY -> 1
        DayOfWeek.TUESDAY -> 2
        DayOfWeek.WEDNESDAY -> 3
        DayOfWeek.THURSDAY -> 4
        DayOfWeek.FRIDAY -> 5
        DayOfWeek.SATURDAY -> 6
        else -> 0
    }

    return EbbingDayOfWeek.take(sundayBasedIndex)
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
