package com.tgyuu.shared.navigation

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class WidgetDeepLinkTest {

    @Test
    fun `홈 링크는 날짜를 함께 해석한다`() {
        assertEquals(
            WidgetDeepLink.Home("2026-11-07"),
            WidgetDeepLink.parse("ebbingplanner://home?date=2026-11-07"),
        )
    }

    @Test
    fun `일정 등록 링크는 날짜를 함께 해석한다`() {
        assertEquals(
            WidgetDeepLink.AddTodo("2026-11-03"),
            WidgetDeepLink.parse("ebbingplanner://addtodo?date=2026-11-03"),
        )
    }

    @Test
    fun `날짜가 없거나 형식이 다르면 앱만 연다`() {
        assertEquals(WidgetDeepLink.Open, WidgetDeepLink.parse("ebbingplanner://home"))
        assertEquals(WidgetDeepLink.Open, WidgetDeepLink.parse("ebbingplanner://addtodo?date=11-03"))
        assertEquals(WidgetDeepLink.Open, WidgetDeepLink.parse("ebbingplanner://open"))
    }

    @Test
    fun `알 수 없는 경로나 스킴이 없는 문자열은 무시한다`() {
        assertNull(WidgetDeepLink.parse("ebbingplanner://unknown"))
        assertNull(WidgetDeepLink.parse("not a url"))
    }
}
