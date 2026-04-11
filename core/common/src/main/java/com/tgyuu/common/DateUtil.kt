package com.tgyuu.common

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.temporal.ChronoUnit

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

/**
 * 기준일(referenceDate, 기본값: 오늘)로부터 이 날짜(this)가
 * 같으면 "오늘", 미래면 "N일 후", 과거면 "N일 전"을 반환
 */
fun LocalDate.toRelativeDayDescription(referenceDate: LocalDate = LocalDate.now()): String {
    val diff = daysBetween(referenceDate, this)
    return when {
        diff == 0L -> "오늘"
        diff > 0L -> "${diff}일 후"
        else -> "${-diff}일 전"
    }
}

private fun daysBetween(start: LocalDate, end: LocalDate): Long {
    return ChronoUnit.DAYS.between(start, end)
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
