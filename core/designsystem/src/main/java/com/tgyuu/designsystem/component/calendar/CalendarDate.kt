package com.tgyuu.designsystem.component.calendar

import java.time.DayOfWeek
import java.time.DayOfWeek.FRIDAY
import java.time.DayOfWeek.MONDAY
import java.time.DayOfWeek.SATURDAY
import java.time.DayOfWeek.SUNDAY
import java.time.DayOfWeek.THURSDAY
import java.time.DayOfWeek.TUESDAY
import java.time.DayOfWeek.WEDNESDAY
import java.time.LocalDate

val EbbingDayOfWeek = listOf(
    SUNDAY,
    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
    FRIDAY,
    SATURDAY,
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
    val firstDayOfMonth = date.withDayOfMonth(1)
    val previousMonth = firstDayOfMonth.minusMonths(1)
    val lastDayOfPreviousMonth = previousMonth.lengthOfMonth()

    val count = getPreviousMonthDayOfWeeksToShow(date).size

    return ((lastDayOfPreviousMonth - count + 1)..lastDayOfPreviousMonth).map {
        CalendarDate(previousMonth.withDayOfMonth(it), isCurrentMonth = false)
    }
}

private fun getCurrentMonthDatesToShow(date: LocalDate): List<CalendarDate> {
    val yearMonth = date.withDayOfMonth(1)
    val lastDay = yearMonth.lengthOfMonth()
    return (1..lastDay).map { day ->
        CalendarDate(yearMonth.withDayOfMonth(day), isCurrentMonth = true)
    }
}

private fun getNextMonthDatesToShow(date: LocalDate): List<CalendarDate> {
    val totalDayCountUntilNextMonth =
        getPreviousMonthDatesToShow(date).size + getCurrentMonthDatesToShow(date).size
    val remainCount = 42 - totalDayCountUntilNextMonth

    val nextMonth = date.withDayOfMonth(1).plusMonths(1)
    return (1..remainCount).map {
        CalendarDate(nextMonth.withDayOfMonth(it), isCurrentMonth = false)
    }
}

/**
 * 해당 월의 달력을 6줄 7칸 기준으로 그릴 때,
 * 1일 전에 보여야 할 이전 달의 요일 목록을 반환합니다.
 *
 */
private fun getPreviousMonthDayOfWeeksToShow(date: LocalDate): List<DayOfWeek> {
    val firstDayOfWeek = getFirstDayOfWeek(date)
    val count = (firstDayOfWeek.value % 7) // SUNDAY(7) % 7 = 0 → 일요일 기준

    return (0 until count).map {
        DayOfWeek.of((it + 1))
    }
}

/**
 * 해당 날짜의 달에 1일의 요일을 구합니다.
 */
private fun getFirstDayOfWeek(date: LocalDate): DayOfWeek {
    return date.withDayOfMonth(1).dayOfWeek
}
