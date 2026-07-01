package com.tgyuu.home.graph.addtodo.contract

import com.tgyuu.designsystem.model.RepeatCycleUiModel
import com.tgyuu.domain.model.RepeatCycle
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toImmutableList
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.DayOfWeek
import java.time.LocalDate

class EditDateStateTest {
    @Test
    fun `제목을 입력하였을 경우 작성상태이다`(){
        // given
        val addTodoState = AddTodoState(title = "무언가")

        // when
        val actual = addTodoState.isModified

        // then
        val expected = true
        assertEquals(expected, actual)
    }

    @Test
    fun `상단 고정을 선택하였을 경우 작성상태이다`(){
        // given
        val addTodoState = AddTodoState(isPinned = true)

        // when
        val actual = addTodoState.isModified

        // then
        val expected = true
        assertEquals(expected, actual)
    }

    @Test
    fun `휴일을 선택하였을 경우 작성상태이다`(){
        // given
        val addTodoState = AddTodoState(restDays = persistentSetOf(DayOfWeek.SATURDAY))

        // when
        val actual = addTodoState.isModified

        // then
        val expected = true
        assertEquals(expected, actual)
    }


    @Test
    fun `제목, 우선순위, 휴일을 입력하지 않았을 경우 작성상태가 아니다`(){
        // given
        val addTodoState = AddTodoState()

        // when
        val actual = addTodoState.isModified

        // then
        val expected = false
        assertEquals(expected, actual)
    }

    // === 매일하기 스케줄 생성 테스트 ===

    @Test
    fun `매일하기 선택 시 연속된 일정이 생성된다`() {
        // given
        val baseDate = LocalDate.of(2025, 7, 21) // 월요일
        val intervals = (0..4).toList()
        val state = AddTodoState(
            selectedDate = baseDate,
            repeatCycle = RepeatCycleUiModel(
                id = RepeatCycle.DAILY_REPEAT_ID,
                intervals = intervals.toImmutableList(),
                displayName = "매일하기 (5일)",
            ),
        )

        // when
        val schedules = state.schedules

        // then
        assertEquals(5, schedules.size)
        assertEquals(LocalDate.of(2025, 7, 21), schedules.first())
        assertEquals(LocalDate.of(2025, 7, 25), schedules.last())
    }

    @Test
    fun `매일하기에서 쉬는날은 미루지 않고 제거된다`() {
        // given
        val baseDate = LocalDate.of(2025, 7, 21) // 월요일
        val intervals = (0..6).toList() // 월~일 7일
        val state = AddTodoState(
            selectedDate = baseDate,
            repeatCycle = RepeatCycleUiModel(
                id = RepeatCycle.DAILY_REPEAT_ID,
                intervals = intervals.toImmutableList(),
                displayName = "매일하기 (7일)",
            ),
            restDays = persistentSetOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY),
        )

        // when
        val schedules = state.schedules

        // then - 7일 중 주말 2일 제거 = 5일
        assertEquals(5, schedules.size)
        schedules.forEach { date ->
            assertTrue(
                date.dayOfWeek != DayOfWeek.SATURDAY && date.dayOfWeek != DayOfWeek.SUNDAY,
                "$date 은 주말인데 포함되어 있습니다"
            )
        }
    }

    @Test
    fun `일반 반복주기에서 쉬는날은 다음 평일로 미뤄진다`() {
        // given
        val baseDate = LocalDate.of(2025, 7, 20) // 일요일
        val intervals = listOf(0, 1, 2)
        val state = AddTodoState(
            selectedDate = baseDate,
            repeatCycle = RepeatCycleUiModel(
                id = -2,
                intervals = intervals.toImmutableList(),
                displayName = "당일, 1일, 2일",
            ),
            restDays = persistentSetOf(DayOfWeek.SUNDAY),
        )

        // when
        val schedules = state.schedules

        // then - 일요일이 미뤄져서 3개 모두 포함
        assertEquals(3, schedules.size)
        assertEquals(LocalDate.of(2025, 7, 21), schedules[0]) // 일→월로 미뤄짐
    }
}
