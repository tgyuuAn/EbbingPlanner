@file:OptIn(ExperimentalTime::class)

package com.tgyuu.common

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.math.absoluteValue
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * 현재 시스템 날짜/시간 가져오기
 */
fun LocalDate.Companion.now(): LocalDate =
    Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

fun LocalDateTime.Companion.now(): LocalDateTime =
    Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

fun LocalDate.copy(
    year: Int = this.year,
    month: Int = this.month.number,
    day: Int = this.day,
): LocalDate = LocalDate(year, month, day)

fun LocalDateTime.copy(
    year: Int = this.year,
    month: Int = this.month.number,
    day: Int = this.day,
    hour: Int = this.hour,
    minute: Int = this.minute,
    second: Int = this.second,
    nanoSecond: Int = this.nanosecond,
): LocalDateTime = LocalDateTime(year, month, day, hour, minute, second, nanoSecond)


/**
 * yyyy-MM-dd 형식 문자열로 변환
 */
fun LocalDate.toFormattedString(): String {
    return "${year.toString().padStart(4, '0')}-${
        month.number.toString().padStart(2, '0')
    }-${day.toString().padStart(2, '0')}"
}

/**
 * 문자열 -> LocalDate 변환
 */
fun String.toLocalDateOrThrow(): LocalDate {
    return try {
        LocalDate.parse(this)
    } catch (e: Exception) {
        throw IllegalArgumentException("날짜 형식이 올바르지 않습니다: $this", e)
    }
}

/**
 * yyyy-MM-dd HH:mm:ss 형식 문자열로 변환
 */
fun LocalDateTime.toFormattedString(): String {
    return "${year.toString().padStart(4, '0')}-${
        month.number.toString().padStart(2, '0')
    }-${day.toString().padStart(2, '0')} " +
            "${hour.toString().padStart(2, '0')}:${
                minute.toString().padStart(2, '0')
            }:${second.toString().padStart(2, '0')}"
}

/**
 * 문자열 -> LocalDateTime 변환
 */
fun String.toLocalDateTimeOrThrow(): LocalDateTime {
    return try {
        LocalDateTime.parse(this)
    } catch (e: Exception) {
        throw IllegalArgumentException("날짜 시간 형식이 올바르지 않습니다: $this", e)
    }
}

/**
 * 기준일(referenceDate, 기본값: 오늘)로부터 이 날짜(this)가
 * 같으면 "오늘", 미래면 "N일 후", 과거면 "N일 전"을 반환
 */
fun LocalDate.toRelativeDayDescription(referenceDate: LocalDate = LocalDate.now()): String {
    val diff = this.daysUntil(referenceDate)
    return when {
        diff == 0 -> "오늘"
        diff > 0 -> "${diff}일 후"
        else -> "${diff.absoluteValue}일 전"
    }
}

/**
 * 두 날짜 사이 일수 계산
 */
fun LocalDate.daysUntil(other: LocalDate): Int {
    return (other.toEpochDays() - this.toEpochDays()).toInt()
}

/**
 * 스케줄 생성
 */
fun generateValidSchedules(
    baseDate: LocalDate,
    intervals: List<Int>,
    restDays: Set<DayOfWeek>
): List<LocalDate> {
    val usedDates = mutableSetOf<LocalDate>()
    return intervals.map { interval ->
        var candidate = baseDate.plus(interval, DateTimeUnit.DAY).nextValidDate(restDays)
        while (candidate in usedDates) {
            candidate = candidate.plus(1, DateTimeUnit.DAY).nextValidDate(restDays)
        }
        usedDates += candidate
        candidate
    }
}

/**
 * 휴일 제외 다음 유효한 날짜 계산
 */
private fun LocalDate.nextValidDate(restDays: Set<DayOfWeek>): LocalDate {
    var date = this
    while (date.dayOfWeek in restDays) {
        date = date.plus(1, DateTimeUnit.DAY)
    }
    return date
}
