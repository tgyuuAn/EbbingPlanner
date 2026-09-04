package com.tgyuu.designsystem.component.calendar

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.daysUntil
import kotlinx.datetime.minus
import kotlinx.datetime.number
import kotlinx.datetime.plus

data class CalendarDate(
    val date: LocalDate,
    val isCurrentMonth: Boolean,
) {
    val dayOfMonth: Int = date.dayOfMonth
}

fun getWeekStart(date: LocalDate, startFromMonday: Boolean = false): LocalDate {
    val iso = date.dayOfWeek.ordinal + 1 // MONDAY=1, ..., SUNDAY=7
    val daysFromStart = if (startFromMonday) {
        iso - 1 // MONDAY=0, ..., SUNDAY=6
    } else {
        iso % 7 // SUNDAY(7%7)=0, MONDAY=1, ..., SATURDAY=6
    }
    return date.minus(daysFromStart, DateTimeUnit.DAY)
}

fun weeksBetween(from: LocalDate, to: LocalDate, startFromMonday: Boolean = false): Int {
    val fromWeekStart = getWeekStart(from, startFromMonday)
    val toWeekStart = getWeekStart(to, startFromMonday)
    return fromWeekStart.daysUntil(toWeekStart) / 7
}

fun getWeekDates(date: LocalDate, startFromMonday: Boolean = false): List<CalendarDate> {
    val weekStart = getWeekStart(date, startFromMonday)
    return (0..6).map { offset ->
        val d = weekStart.plus(offset, DateTimeUnit.DAY)
        CalendarDate(d, isCurrentMonth = d.month == date.month)
    }
}

fun getCalendarDates(date: LocalDate, startFromMonday: Boolean = false): List<CalendarDate> {
    val leadingCount = getLeadingEmptyCellCount(date, startFromMonday)
    val current = getCurrentMonthDatesToShow(date)
    val trailingCount = 42 - leadingCount - current.size
    val previous = getPreviousMonthDatesToShowByCount(date, leadingCount)
    val next = getNextMonthDatesToShowByCount(date, trailingCount)
    return previous + current + next
}

private fun getLeadingEmptyCellCount(date: LocalDate, startFromMonday: Boolean): Int {
    val firstDay = LocalDate(date.year, date.monthNumber, 1).dayOfWeek
    return if (startFromMonday) {
        when (firstDay) {
            DayOfWeek.MONDAY -> 0; DayOfWeek.TUESDAY -> 1; DayOfWeek.WEDNESDAY -> 2
            DayOfWeek.THURSDAY -> 3; DayOfWeek.FRIDAY -> 4; DayOfWeek.SATURDAY -> 5
            else -> 6 // SUNDAY
        }
    } else {
        when (firstDay) {
            DayOfWeek.SUNDAY -> 0; DayOfWeek.MONDAY -> 1; DayOfWeek.TUESDAY -> 2
            DayOfWeek.WEDNESDAY -> 3; DayOfWeek.THURSDAY -> 4; DayOfWeek.FRIDAY -> 5
            else -> 6 // SATURDAY
        }
    }
}

private fun getPreviousMonthDatesToShowByCount(date: LocalDate, count: Int): List<CalendarDate> {
    if (count == 0) return emptyList()
    val previousMonth = LocalDate(date.year, date.monthNumber, 1).minus(1, DateTimeUnit.MONTH)
    val lastDay = previousMonth.totalDaysInMonth()
    return ((lastDay - count + 1)..lastDay).map {
        CalendarDate(LocalDate(previousMonth.year, previousMonth.monthNumber, it), isCurrentMonth = false)
    }
}

private fun getNextMonthDatesToShowByCount(date: LocalDate, count: Int): List<CalendarDate> {
    if (count <= 0) return emptyList()
    val nextMonth = LocalDate(date.year, date.monthNumber, 1).plus(1, DateTimeUnit.MONTH)
    return (1..count).map {
        CalendarDate(LocalDate(nextMonth.year, nextMonth.monthNumber, it), isCurrentMonth = false)
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

fun LocalDate.totalDaysInMonth(): Int {
    // 다음 달 1일
    val nextMonth = this.plus(1, DateTimeUnit.MONTH).run { LocalDate(year, monthNumber, 1) }
    // 다음 달 1일에서 하루 빼기 = 이번 달 마지막 날
    val lastDayOfMonth = nextMonth.minus(1, DateTimeUnit.DAY)
    return lastDayOfMonth.dayOfMonth
}
