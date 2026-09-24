package com.tgyuu.home.graph.addtodo.contract

import kotlinx.collections.immutable.persistentSetOf
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlinx.datetime.DayOfWeek

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
}
