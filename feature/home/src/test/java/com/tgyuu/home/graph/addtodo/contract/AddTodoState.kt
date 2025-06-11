package com.tgyuu.home.graph.addtodo.contract

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.DayOfWeek

class AddTodoStateTest {
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
    fun `우선순위를 입력하였을 경우 작성상태이다`(){
        // given
        val addTodoState = AddTodoState(priority = "1")

        // when
        val actual = addTodoState.isModified

        // then
        val expected = true
        assertEquals(expected, actual)
    }

    @Test
    fun `휴일을 선택하였을 경우 작성상태이다`(){
        // given
        val addTodoState = AddTodoState(restDays = setOf(DayOfWeek.SATURDAY))

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
