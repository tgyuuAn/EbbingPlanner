package com.tgyuu.home.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class NextProgressTest {

    @ParameterizedTest(name = "{0} → {1}")
    @CsvSource(
        delimiter = '|',
        value = [
            // 범위는 시작점만 이어가고 끝은 비워요. 구분자와 앞자리 0은 유지하고, 끝에 붙은 단위는 함께 비워요.
            "경제원론 1-50 | 경제원론 51-",
            "수학의 정석 p.120-135 | 수학의 정석 p.136-",
            "토익 RC Part5 1~30 | 토익 RC Part5 31~",
            "회계원리 Ch3 연습문제 1~10번 | 회계원리 Ch3 연습문제 11~",
            "해커스 토익 LC 1-2강 | 해커스 토익 LC 3-",
            "영단어 DAY 01-03 | 영단어 DAY 04-",
            "TOEIC 1-50 단어 | TOEIC 51- 단어",
            // 숫자 하나는 1 늘려요. 앞이 크거나 같은 "3-1"은 범위가 아니라 단원 표기예요.
            "민법총론 2강 | 민법총론 3강",
            "C++ 기초 2강 | C++ 기초 3강",
            "Ch.3 | Ch.4",
            "3장 | 4장",
            "Day 5 | Day 6",
            "수능특강 국어 3-1 | 수능특강 국어 3-2",
            "2024 기출 1회 | 2024 기출 2회",
            "정처기 실기 10/20 | 정처기 실기 11/20",
            // 진도가 아닌 숫자(연도·급수·월·시간·분량)만 있으면 그대로 둬요.
            "JLPT N3 단어 | JLPT N3 단어",
            "2024 기출 | 2024 기출",
            "3월 모의고사 | 3월 모의고사",
            "5분 스피치 | 5분 스피치",
            "100문제 풀기 | 100문제 풀기",
            "영단어 | 영단어",
            // 끝을 비운 범위는 어디까지 했는지 몰라 그대로 둬요.
            "경제원론 51~ | 경제원론 51~",
            "경제원론 51- | 경제원론 51-",
        ],
    )
    fun `제목 하나로 다음 진도를 추정한다`(title: String, expected: String) {
        assertEquals(expected, suggestNextProgress(title).title)
    }

    @ParameterizedTest(name = "{0} → {1} → {2}")
    @CsvSource(
        delimiter = '|',
        value = [
            // 바뀐 숫자만 같은 간격으로 이어가요. 고정된 숫자(N3, 2024)는 그대로예요.
            "JLPT N3 단어 1-50 | JLPT N3 단어 51-100 | JLPT N3 단어 101-",
            "2024 기출 1회 | 2024 기출 2회 | 2024 기출 3회",
            "Day 1 | Day 3 | Day 5",
            "3월 모의고사 | 4월 모의고사 | 5월 모의고사",
            // 범위는 마지막 끝 다음부터 시작해요.
            "경제원론 1-50 | 경제원론 51-80 | 경제원론 81-",
            // 구분자나 대소문자가 달라도 같은 과목으로 봐요.
            "toeic 1~50 | TOEIC 51-100 | TOEIC 101-",
        ],
    )
    fun `직전 제목과 비교해 바뀐 숫자만 이어간다`(previous: String, latest: String, expected: String) {
        assertEquals(expected, suggestNextProgress(latest, previous).title)
    }

    @Test
    fun `진도가 줄었거나 과목이 다르면 제목 하나로 추정한다`() {
        assertEquals("Day 4", suggestNextProgress("Day 3", previousTitle = "Day 5").title)
        assertEquals("민법총론 3강", suggestNextProgress("민법총론 2강", previousTitle = "경제원론 1-50").title)
        // 끝을 비운 채 저장했으면 어디까지 했는지 몰라 그대로 이어가요.
        assertEquals("TOEIC 101-", suggestNextProgress("TOEIC 101-", previousTitle = "TOEIC 51-100").title)
    }

    @Test
    fun `토스트에 쓸 과목명과 시작 진도를 함께 돌려준다`() {
        assertEquals(
            NextProgress(title = "경제원론 51-", subject = "경제원론", start = "51", basis = NextProgress.Basis.RANGE),
            suggestNextProgress("경제원론 1-50"),
        )
        assertEquals(
            NextProgress(title = "민법총론 3강", subject = "민법총론", start = "3강", basis = NextProgress.Basis.NUMBER),
            suggestNextProgress("민법총론 2강"),
        )
        assertNull(suggestNextProgress("영단어").start)
        assertEquals(NextProgress.Basis.NONE, suggestNextProgress("영단어").basis)
        assertEquals(
            NextProgress.Basis.HISTORY,
            suggestNextProgress("TOEIC 51-100", previousTitle = "TOEIC 1-50").basis,
        )
    }

    @Test
    fun `숫자만 다른 제목은 같은 과목 키를 갖는다`() {
        assertEquals(progressKey("TOEIC 1-50"), progressKey("toeic  51 ~ 100"))
        assertEquals(progressKey("TOEIC 1-50"), progressKey("TOEIC 101-"))
        assertFalse(progressKey("TOEIC 1-50") == progressKey("TOEFL 1-50"))
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
