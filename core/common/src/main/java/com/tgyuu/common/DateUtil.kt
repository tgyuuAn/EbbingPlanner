package com.tgyuu.common

import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

/**
 * 현재 시스템 날짜/시간 가져오기
 */
fun LocalDate.Companion.now(): LocalDate =
    Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

fun LocalDateTime.Companion.now(): LocalDateTime =
    Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

fun LocalDate.copy(
    year: Int = this.year,
    month: Int = this.monthNumber,
    dayOfMonth: Int = this.dayOfMonth,
): LocalDate = LocalDate(year, month, dayOfMonth)

fun LocalDateTime.copy(
    year: Int = this.year,
    month: Int = this.monthNumber,
    dayOfMonth: Int = this.dayOfMonth,
    hour: Int = this.hour,
    minute: Int = this.minute,
    second: Int = this.second,
    nanoSecond: Int = this.nanosecond,
): LocalDateTime = LocalDateTime(year, month, dayOfMonth, hour, minute, second, nanoSecond)


/**
 * yyyy-MM-dd 형식 문자열로 변환
 */
fun LocalDate.toFormattedString(): String {
    return "${year.toString().padStart(4, '0')}-${
        month.number.toString().padStart(2, '0')
    }-${dayOfMonth.toString().padStart(2, '0')}"
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
    }-${dayOfMonth.toString().padStart(2, '0')} " +
            "${hour.toString().padStart(2, '0')}:${
                minute.toString().padStart(2, '0')
            }:${second.toString().padStart(2, '0')}"
}

/**
 * 문자열 -> LocalDateTime 변환
 * "yyyy-MM-dd HH:mm:ss" 또는 ISO-8601 "yyyy-MM-ddTHH:mm:ss" 형식 지원
 */
fun String.toLocalDateTimeOrThrow(): LocalDateTime {
    return try {
        // DB에 저장된 형식("yyyy-MM-dd HH:mm:ss")을 ISO-8601 형식으로 변환
        val isoFormatted = this.replace(' ', 'T')
        LocalDateTime.parse(isoFormatted)
    } catch (e: Exception) {
        throw IllegalArgumentException("날짜 시간 형식이 올바르지 않습니다: $this", e)
    }
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
 * 매일하기 스케줄 생성
 *
 * 일반 반복주기(generateValidSchedules)와 달리, 쉬는 요일을 만나면
 * 다음 평일로 미루지 않고 해당 날짜를 "제거"한다.
 * 따라서 쉬는 요일이 많을수록 마지막 날짜는 그대로 유지되고 일정 개수가 줄어든다.
 */
fun generateDailySchedules(
    baseDate: LocalDate,
    intervals: List<Int>,
    restDays: Set<DayOfWeek>,
): List<LocalDate> =
    intervals
        .map { interval -> baseDate.plus(interval, DateTimeUnit.DAY) }
        .filter { it.dayOfWeek !in restDays }

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
