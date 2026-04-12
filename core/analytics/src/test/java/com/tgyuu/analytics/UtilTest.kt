package com.tgyuu.analytics

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class UtilTest {

    @ParameterizedTest
    @CsvSource(
        "HomeRoute?workedDate=2024-06-30, Home",
        "com.tgyuu.navigation.SettingRoute, Setting",
        "AddTodoRoute, AddTodo",
        "UnknownScreen, UnknownScreen",
    )
    fun `복잡한 route 문자열에서 화면 이름만 추출한다`(input: String, expected: String) {
        // when
        val result = mapScreenName(input)

        // then
        assertEquals(expected, result)
    }
}
