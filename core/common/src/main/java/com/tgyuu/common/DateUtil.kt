package com.tgyuu.common

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.temporal.ChronoUnit

private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

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

fun daysBetween(start: LocalDate, end: LocalDate): Long {
    return ChronoUnit.DAYS.between(start, end)
}
