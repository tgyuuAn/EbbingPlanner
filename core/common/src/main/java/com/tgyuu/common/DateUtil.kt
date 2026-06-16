package com.tgyuu.common

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
private val dateTimeFormatter: DateTimeFormatter =
    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

fun LocalDate.toFormattedString(): String {
    return this.format(dateFormatter)
}

fun String.toLocalDateOrThrow(): LocalDate {
    return try {
        LocalDate.parse(this, dateFormatter)
    } catch (e: DateTimeParseException) {
        throw IllegalArgumentException("날짜 형식이 올바르지 않습니다: $this", e)
    }
}

fun LocalDateTime.toFormattedString(): String {
    return this.format(dateTimeFormatter)
}

fun String.toLocalDateTimeOrThrow(): LocalDateTime {
    return try {
        LocalDateTime.parse(this, dateTimeFormatter)  // 공백 포맷
    } catch (e: DateTimeParseException) {
        LocalDateTime.parse(this, DateTimeFormatter.ISO_LOCAL_DATE_TIME)  // T 포맷
    }
}

fun generateValidSchedules(
    baseDate: LocalDate,
    intervals: List<Int>,
    restDays: Set<DayOfWeek>
): List<LocalDate> {
    val usedDates = mutableSetOf<LocalDate>()
    return intervals.map { interval ->
        var candidate = baseDate.plusDays(interval.toLong()).nextValidDate(restDays)

        while (candidate in usedDates) {
            candidate = candidate.plusDays(1).nextValidDate(restDays)
        }

        usedDates += candidate
        candidate
    }
}

fun generateDailySchedules(
    baseDate: LocalDate,
    intervals: List<Int>,
    restDays: Set<DayOfWeek>
): List<LocalDate> {
    return intervals
        .map { interval -> baseDate.plusDays(interval.toLong()) }
        .filter { date -> date.dayOfWeek !in restDays }
}

private fun LocalDate.nextValidDate(restDays: Set<DayOfWeek>): LocalDate {
    var date = this
    while (date.dayOfWeek in restDays) {
        date = date.plusDays(1)
    }
    return date
}
