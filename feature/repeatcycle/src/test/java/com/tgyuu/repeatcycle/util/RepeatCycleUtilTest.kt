package com.tgyuu.repeatcycle.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class RepeatCycleUtilTest {

    @Test
    fun `정상적인 숫자 문자열은 중복 없이 정렬된 리스트로 변환된다`() {
        // given
        val input = "7, 1, 15, 0, 1, 15"

        // when
        val actual = parsingIntervals(input)


        // then
        val expected = listOf(0, 1, 7, 15)
        assertEquals(expected, actual.getOrNull())
    }

    @Test
    fun `공백이 포함되어도 정상적으로 파싱된다`() {
        // given
        val input = "  3 ,  10 ,5  , 1 , 5"

        // when
        val actual = parsingIntervals(input)

        // then
        val expected = listOf(1, 3, 5, 10)
        assertEquals(expected, actual.getOrNull())
    }

    @Test
    fun `1000 이상의 문자열은 무시한다`() {
        // given
        val input = " 1000, 1001, 1002, 1003"

        // when
        val actual = parsingIntervals(input)

        // then
        val expected = emptyList<Int>()
        assertEquals(expected, actual.getOrNull())
    }

    @Test
    fun `빈 문자열은 빈 리스트를 반환한다`() {
        // given
        val input = ""

        // when
        val actual = parsingIntervals(input)

        // then
        val expected = emptyList<Int>()
        assertEquals(expected, actual.getOrNull())
    }

    @Test
    fun `숫자가 아닌 값이 포함되면 실패한다`() {
        // given
        val input = "1, two, 3"

        // when
        val actual = parsingIntervals(input)

        // then
        assertTrue(actual.exceptionOrNull() is NumberFormatException)
    }

    @Test
    fun `쉼표만 있는 입력은 빈 리스트를 반환한다`() {
        // given
        val input = ", , ,"

        // when
        val actual = parsingIntervals(input)

        // then
        val expected = emptyList<Int>()
        assertEquals(expected, actual.getOrNull())
    }

    @Test
    fun `0만 있을 경우 당일만 반환한다`() {
        // given
        val input = listOf(0)

        // when
        val actual = input.toPreviewIntervals()

        // then
        assertEquals("당일만", actual)
    }

    @Test
    fun `0과 다른 숫자가 섞여 있으면 당일, n일 형식으로 반환한다`() {
        // given
        val input = listOf(0, 1, 3)

        // when
        val actual = input.toPreviewIntervals()

        // then
        assertEquals("당일, 1일, 3일", actual)
    }

    @Test
    fun `0이 없을 경우 모두 n일 형식으로 반환한다`() {
        // given
        val input = listOf(1, 2, 5)

        // when
        val actual = input.toPreviewIntervals()

        // then
        assertEquals("1일, 2일, 5일", actual)
    }

    @Test
    fun `빈 리스트는 경고 문구를 반환한다`() {
        // given
        val input = emptyList<Int>()

        // when
        val actual = input.toPreviewIntervals()

        // then
        assertEquals("올바른 형태로 작성해주세요.", actual)
    }
}
