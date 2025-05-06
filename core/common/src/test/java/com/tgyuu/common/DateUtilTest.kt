package com.tgyuu.common

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.time.LocalDate
import java.time.LocalDateTime

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
    fun `잘못된 형식의 문자열을 LocalDateTime으로 변환하려고 하면 IllegalArgumentException이 발생한다`(
        input: String
    ) {
        // expect
        assertThrows<IllegalArgumentException> {
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
}
