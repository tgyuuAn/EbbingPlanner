package com.tgyuu.designsystem.component.calendar

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.DayOfWeek.FRIDAY
import kotlinx.datetime.DayOfWeek.MONDAY
import kotlinx.datetime.DayOfWeek.SATURDAY
import kotlinx.datetime.DayOfWeek.SUNDAY
import kotlinx.datetime.DayOfWeek.THURSDAY
import kotlinx.datetime.DayOfWeek.TUESDAY
import kotlinx.datetime.DayOfWeek.WEDNESDAY
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.number
import kotlinx.datetime.plus

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

/**
 * 해당 월의 달력을 6줄 7칸 기준으로 그릴 때,
 * 1일 전에 보여야 할 이전 달의 요일 목록을 반환합니다.
 *
 */
private fun getPreviousMonthDayOfWeeksToShow(date: LocalDate): List<DayOfWeek> {
    val firstDayOfWeek = getFirstDayOfWeek(date)
    val count = (firstDayOfWeek.ordinal + 1) % 7 // 일요일 기준 0~6

    val allDays = DayOfWeek.entries // SUNDAY ~ SATURDAY 순서
    return (0 until count).map { allDays[it] }
}

/**
 * 해당 날짜의 달에 1일의 요일을 구합니다.
 */
private fun getFirstDayOfWeek(date: LocalDate): DayOfWeek {
    return LocalDate(date.year, date.monthNumber, 1).dayOfWeek
}


fun LocalDate.totalDaysInMonth(): Int {
    // 다음 달 1일
    val nextMonth = this.plus(1, DateTimeUnit.MONTH).run { LocalDate(year, monthNumber, 1) }
    // 다음 달 1일에서 하루 빼기 = 이번 달 마지막 날
    val lastDayOfMonth = nextMonth.minus(1, DateTimeUnit.DAY)
    return lastDayOfMonth.day
}
