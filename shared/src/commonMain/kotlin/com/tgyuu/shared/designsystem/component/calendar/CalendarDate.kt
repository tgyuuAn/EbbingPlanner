package com.tgyuu.shared.designsystem.component.calendar

import androidx.compose.runtime.Composable
import ebbingplanner.shared.generated.resources.Res
import ebbingplanner.shared.generated.resources.day_fri
import ebbingplanner.shared.generated.resources.day_mon
import ebbingplanner.shared.generated.resources.day_sat
import ebbingplanner.shared.generated.resources.day_sun
import ebbingplanner.shared.generated.resources.day_thu
import ebbingplanner.shared.generated.resources.day_tue
import ebbingplanner.shared.generated.resources.day_wed
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

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

fun getWeekStart(date: LocalDate, startFromMonday: Boolean = false): LocalDate {
    val iso = date.dayOfWeek.ordinal + 1 // MONDAY=1, ..., SUNDAY=7
    val daysFromStart = if (startFromMonday) {
        iso - 1
    } else {
        iso % 7
    }
    return date.minus(daysFromStart, DateTimeUnit.DAY)
}

fun weeksBetween(from: LocalDate, to: LocalDate, startFromMonday: Boolean = false): Int {
    val fromWeekStart = getWeekStart(from, startFromMonday)
    val toWeekStart = getWeekStart(to, startFromMonday)
    return ((toWeekStart.toEpochDays() - fromWeekStart.toEpochDays()) / 7).toInt()
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

private fun DayOfWeek.shortLabelRes(): StringResource = when (this) {
    DayOfWeek.SUNDAY -> Res.string.day_sun
    DayOfWeek.MONDAY -> Res.string.day_mon
    DayOfWeek.TUESDAY -> Res.string.day_tue
    DayOfWeek.WEDNESDAY -> Res.string.day_wed
    DayOfWeek.THURSDAY -> Res.string.day_thu
    DayOfWeek.FRIDAY -> Res.string.day_fri
    DayOfWeek.SATURDAY -> Res.string.day_sat
    else -> Res.string.day_sun
}

@Composable
fun DayOfWeek.toLocalizedShort(): String = stringResource(shortLabelRes())

@Composable
fun rememberDayOfWeekShortLabels(): Map<DayOfWeek, String> = mapOf(
    DayOfWeek.SUNDAY to stringResource(Res.string.day_sun),
    DayOfWeek.MONDAY to stringResource(Res.string.day_mon),
    DayOfWeek.TUESDAY to stringResource(Res.string.day_tue),
    DayOfWeek.WEDNESDAY to stringResource(Res.string.day_wed),
    DayOfWeek.THURSDAY to stringResource(Res.string.day_thu),
    DayOfWeek.FRIDAY to stringResource(Res.string.day_fri),
    DayOfWeek.SATURDAY to stringResource(Res.string.day_sat),
)
