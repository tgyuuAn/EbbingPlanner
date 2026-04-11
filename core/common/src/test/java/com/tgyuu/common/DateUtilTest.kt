package com.tgyuu.common

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeParseException

class LocalDateFormatTest {

    @Test
    fun `LocalDate를 문자열로 변환할 수 있다`() {
        // given
        val date = LocalDate.of(2025, 4, 22)

        // when
        val result = date.toFormattedString()

        // then
        assertEquals("2025-04-22", result)
    }

    @Test
    fun `yyyy-MM-dd 형식의 문자열을 LocalDate로 변환할 수 있다`() {
        // given
        val input = "2023-11-15"

        // when
        val result = input.toLocalDateOrThrow()

        // then
        assertEquals(LocalDate.of(2023, 11, 15), result)
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "2023/11/15",     // 슬래시 사용
            "15-11-2023",     // 잘못된 순서
            "2023-2-5",       // 잘못된 포맷
            "20231115",       // 구분자 없음
            "invalid-date",   // 텍스트
            "",               // 빈 문자열
            "2023-13-01",     // 존재하지 않는 월
            "2023-00-10",     // 0월
            "2023-12-32"      // 존재하지 않는 일
        ]
    )
    fun `잘못된 형식의 문자열을 LocalDate로 변환하려고 하면 IllegalArgumentException이 발생한다`(input: String) {
        // expect
        assertThrows<IllegalArgumentException> {
            input.toLocalDateOrThrow()
        }
    }

    @Test
    fun `LocalDateTime을 문자열로 변환할 수 있다`() {
        // given
        val dateTime = LocalDateTime.of(2025, 4, 22, 15, 30, 45)

        // when
        val result = dateTime.toFormattedString()

        // then
        assertEquals("2025-04-22 15:30:45", result)
    }

    @Test
    fun `yyyy-MM-dd HH-mm-ss 형식의 문자열을 LocalDateTime으로 변환할 수 있다`() {
        // given
        val input = "2023-11-15 09:05:00"

        // when
        val result = input.toLocalDateTimeOrThrow()

        // then
        assertEquals(LocalDateTime.of(2023, 11, 15, 9, 5, 0), result)
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "2023/11/15 10:00:00", // 잘못된 구분자
            "15-11-2023 10:00:00", // 잘못된 순서
            "2023-2-5 1:2:3",      // 잘못된 포맷
            "20231115T100000",     // ISO 8601 형식 아님
            "invalid-datetime",    // 텍스트
            "",                    // 빈 문자열
            "2023-13-01 00:00:00", // 존재하지 않는 월
            "2023-00-10 00:00:00", // 0월
            "2023-12-32 00:00:00", // 존재하지 않는 일
            "2023-12-31 24:01:00", // 24시는 허용 안 됨
            "2023-12-31 23:60:00"  // 60분은 허용 안 됨
        ]
    )
    fun `잘못된 형식의 문자열을 LocalDateTime으로 변환하려고 하면 DateTimeParseException이 발생한다`(
        input: String
    ) {
        // expect
        assertThrows<DateTimeParseException> {
            input.toLocalDateTimeOrThrow()
        }
    }

    @Test
    fun `같은 날짜를 비교하면 오늘을 반환한다`() {
        // given
        val reference = LocalDate.of(2025, 4, 23)

        // when
        val result = reference.toRelativeDayDescription(referenceDate = reference)

        // then
        assertEquals("오늘", result)
    }

    @Test
    fun `미래 날짜를 비교하면 N일 후를 반환한다`() {
        // given
        val reference = LocalDate.of(2025, 4, 20)
        val futureDate = LocalDate.of(2025, 4, 25)

        // when
        val result = futureDate.toRelativeDayDescription(referenceDate = reference)

        // then
        assertEquals("5일 후", result)
    }

    @Test
    fun `과거 날짜를 비교하면 N일 전을 반환한다`() {
        // given
        val reference = LocalDate.of(2025, 4, 25)
        val pastDate = LocalDate.of(2025, 4, 20)

        // when
        val result = pastDate.toRelativeDayDescription(referenceDate = reference)

        // then
        assertEquals("5일 전", result)
    }

    @Test
    fun `휴일이 없는 경우 그대로 반복 주기를 적용한다`() {
        // given
        val baseDate = LocalDate.of(2025, 7, 20) // 일요일
        val intervals = listOf(0, 1, 2)
        val restDays = emptySet<DayOfWeek>()

        // when
        val result = generateValidSchedules(baseDate, intervals, restDays)

        // then
        assertEquals(
            listOf(
                LocalDate.of(2025, 7, 20),
                LocalDate.of(2025, 7, 21),
                LocalDate.of(2025, 7, 22),
            ),
            result
        )
    }

    @Test
    fun `휴일이면 반복 주기를 계산할 때 다음 평일로 이동한다`() {
        // given
        val baseDate = LocalDate.of(2025, 7, 20) // 일요일
        val intervals = listOf(0, 1, 2)
        val restDays = setOf(DayOfWeek.SUNDAY)

        // when
        val result = generateValidSchedules(baseDate, intervals, restDays)

        // then
        assertEquals(
            listOf(
                LocalDate.of(2025, 7, 21), // 7/20은 일요일이라서 → 7/21
                LocalDate.of(2025, 7, 22),
                LocalDate.of(2025, 7, 23),
            ),
            result
        )
    }

    @Test
    fun `중복된 날짜가 있을 경우, 반복 주기를 구할 때 다음 날짜를 선택한다`() {
        // given
        val baseDate = LocalDate.of(2025, 7, 20) // 일요일
        val intervals = listOf(0, 0, 0)
        val restDays = emptySet<DayOfWeek>()

        // when
        val result = generateValidSchedules(baseDate, intervals, restDays)

        // then
        assertEquals(
            listOf(
                LocalDate.of(2025, 7, 20),
                LocalDate.of(2025, 7, 21),
                LocalDate.of(2025, 7, 22)
            ),
            result
        )
    }

    // === 매일하기 (generateDailySchedules) 테스트 ===

    @Test
    fun `매일하기는 연속된 날짜를 생성한다`() {
        // given
        val baseDate = LocalDate.of(2025, 7, 20)
        val intervals = listOf(0, 1, 2, 3, 4)
        val restDays = emptySet<DayOfWeek>()

        // when
        val result = generateDailySchedules(baseDate, intervals, restDays)

        // then
        assertEquals(
            listOf(
                LocalDate.of(2025, 7, 20),
                LocalDate.of(2025, 7, 21),
                LocalDate.of(2025, 7, 22),
                LocalDate.of(2025, 7, 23),
                LocalDate.of(2025, 7, 24),
            ),
            result
        )
    }

    @Test
    fun `매일하기에서 쉬는날은 미루지 않고 제거한다`() {
        // given
        val baseDate = LocalDate.of(2025, 7, 20) // 일요일
        val intervals = listOf(0, 1, 2, 3, 4, 5, 6)
        val restDays = setOf(DayOfWeek.SUNDAY)

        // when
        val result = generateDailySchedules(baseDate, intervals, restDays)

        // then - 일요일인 7/20, 7/27은 제거됨
        assertEquals(
            listOf(
                LocalDate.of(2025, 7, 21), // 월
                LocalDate.of(2025, 7, 22), // 화
                LocalDate.of(2025, 7, 23), // 수
                LocalDate.of(2025, 7, 24), // 목
                LocalDate.of(2025, 7, 25), // 금
                LocalDate.of(2025, 7, 26), // 토
            ),
            result
        )
    }

    @Test
    fun `매일하기에서 토요일과 일요일을 쉬는날로 설정하면 주말이 모두 제거된다`() {
        // given
        val baseDate = LocalDate.of(2025, 7, 21) // 월요일
        val intervals = (0..13).toList() // 2주
        val restDays = setOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)

        // when
        val result = generateDailySchedules(baseDate, intervals, restDays)

        // then - 14일 중 주말 4일 제거 = 10일
        assertEquals(10, result.size)
        result.forEach { date ->
            assert(date.dayOfWeek != DayOfWeek.SATURDAY && date.dayOfWeek != DayOfWeek.SUNDAY) {
                "$date 은 주말인데 포함되어 있습니다"
            }
        }
    }

    @Test
    fun `매일하기에서 쉬는날이 없으면 모든 날짜가 포함된다`() {
        // given
        val baseDate = LocalDate.of(2025, 7, 20)
        val intervals = (0..29).toList() // 30일
        val restDays = emptySet<DayOfWeek>()

        // when
        val result = generateDailySchedules(baseDate, intervals, restDays)

        // then
        assertEquals(30, result.size)
        assertEquals(LocalDate.of(2025, 7, 20), result.first())
        assertEquals(LocalDate.of(2025, 8, 18), result.last())
    }

    @Test
    fun `매일하기에서 당일만 선택하면 하루만 생성된다`() {
        // given
        val baseDate = LocalDate.of(2025, 7, 20)
        val intervals = listOf(0)
        val restDays = emptySet<DayOfWeek>()

        // when
        val result = generateDailySchedules(baseDate, intervals, restDays)

        // then
        assertEquals(listOf(LocalDate.of(2025, 7, 20)), result)
    }

    @Test
    fun `매일하기에서 당일이 쉬는날이면 빈 리스트를 반환한다`() {
        // given
        val baseDate = LocalDate.of(2025, 7, 20) // 일요일
        val intervals = listOf(0)
        val restDays = setOf(DayOfWeek.SUNDAY)

        // when
        val result = generateDailySchedules(baseDate, intervals, restDays)

        // then
        assertEquals(emptyList<LocalDate>(), result)
    }

    // === 기존 generateValidSchedules 테스트 ===

    @Test
    fun `반복 주기를 구할 때 중복 방지와 휴일 회피가 동시에 작동한다`() {
        // given
        val baseDate = LocalDate.of(2025, 7, 19) // 토요일
        val intervals = listOf(0, 0, 0)
        val restDays = setOf(DayOfWeek.SUNDAY)

        // when
        val result = generateValidSchedules(baseDate, intervals, restDays)

        // then
        assertEquals(
            listOf(
                LocalDate.of(2025, 7, 19), // 7/19 (토)
                LocalDate.of(2025, 7, 21), // 7/20 (일) → skip → 7/21
                LocalDate.of(2025, 7, 22)  // 중복 회피 → 7/22
            ),
            result
        )
    }
}
