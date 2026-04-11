package com.tgyuu.domain.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class RepeatCycleTest {

    @Test
    fun `매일하기 placeholder는 매일하기로 표시된다`() {
        // given
        val cycle = RepeatCycle(id = RepeatCycle.DAILY_REPEAT_ID, intervals = listOf(0))

        // when
        val result = cycle.toDisplayName()

        // then
        assertEquals("매일하기", result)
    }

    @Test
    fun `매일하기에 종료일이 설정되면 일수가 표시된다`() {
        // given
        val cycle = RepeatCycle(
            id = RepeatCycle.DAILY_REPEAT_ID,
            intervals = (0..6).toList(), // 7일
        )

        // when
        val result = cycle.toDisplayName()

        // then
        assertEquals("매일하기 (7일)", result)
    }

    @Test
    fun `매일하기 30일은 올바르게 표시된다`() {
        // given
        val cycle = RepeatCycle(
            id = RepeatCycle.DAILY_REPEAT_ID,
            intervals = (0..29).toList(),
        )

        // when
        val result = cycle.toDisplayName()

        // then
        assertEquals("매일하기 (30일)", result)
    }

    @Test
    fun `DefaultRepeatCycles에서 매일하기는 당일만 다음에 위치한다`() {
        // given & when
        val dailyIndex = DefaultRepeatCycles.indexOfFirst { it.id == RepeatCycle.DAILY_REPEAT_ID }
        val onlyTodayIndex = DefaultRepeatCycles.indexOfFirst { it.id == -1 }

        // then
        assertTrue(dailyIndex > onlyTodayIndex, "매일하기는 당일만 아래에 있어야 합니다")
    }

    @Test
    fun `당일만은 올바르게 표시된다`() {
        // given
        val cycle = RepeatCycle(id = -1, intervals = listOf(0))

        // when
        val result = cycle.toDisplayName()

        // then
        assertEquals("당일만", result)
    }

    @Test
    fun `일반 반복주기는 기존 형식으로 표시된다`() {
        // given
        val cycle = RepeatCycle(id = -2, intervals = listOf(0, 1, 7, 15))

        // when
        val result = cycle.toDisplayName()

        // then
        assertEquals("당일, 1일, 7일, 15일", result)
    }
}
