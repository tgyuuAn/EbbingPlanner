package com.tgyuu.home.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class NextProgressTest {

    @Test
    fun `범위 제목은 끝 다음 번호부터 열린 범위로 추천한다`() {
        assertEquals(
            NextProgress(title = "경제원론 51~", subject = "경제원론", start = "51"),
            suggestNextProgress("경제원론 1-50"),
        )
        assertEquals("회계 101~", suggestNextProgress("회계 51 ~ 100").title)
    }

    @Test
    fun `단일 숫자는 1 늘리고 단위를 유지한다`() {
        assertEquals(
            NextProgress(title = "민법총론 3강", subject = "민법총론", start = "3강"),
            suggestNextProgress("민법총론 2강"),
        )
        assertEquals("Day 06 단어", suggestNextProgress("Day 05 단어").title)
    }

    @Test
    fun `숫자가 없거나 끝을 비운 범위는 제목을 그대로 추천한다`() {
        assertEquals("영단어", suggestNextProgress(" 영단어 ").title)
        assertNull(suggestNextProgress("영단어").start)
        assertEquals("경제원론 51~", suggestNextProgress("경제원론 51~").title)
        assertNull(suggestNextProgress("경제원론 51~").start)
    }

    @Test
    fun `받침 유무로 조사를 고른다`() {
        assertTrue("경제원론".hasFinalConsonant())
        assertFalse("영단어 Day".hasFinalConsonant())
        assertFalse("회계".hasFinalConsonant())
        assertTrue("Chapter 3".hasFinalConsonant())
        assertFalse("Chapter 2".hasFinalConsonant())
    }
}
