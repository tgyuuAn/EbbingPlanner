package com.tgyuu.shared.navigation

import kotlin.test.Test
import kotlin.test.assertEquals

class TabStackSaverTest {
    private val home = Configuration.Home()

    @Test
    fun `일정 탭 하위 화면은 다른 탭에 다녀오면 복원된다`() {
        val saver = TabStackSaver()
        val scheduleStack = listOf(home, Configuration.Schedule, Configuration.Memo(3))

        val settingStack = saver.navigate(scheduleStack, RootTab.SETTING)
        assertEquals(listOf(home, Configuration.Setting), settingStack)

        assertEquals(scheduleStack, saver.navigate(settingStack, RootTab.SCHEDULE))
    }

    @Test
    fun `탭마다 따로 저장된다`() {
        val saver = TabStackSaver()
        val settingTag = listOf(home, Configuration.Setting, Configuration.Tag, Configuration.AddTag)
        val schedule = saver.navigate(settingTag, RootTab.SCHEDULE)
        val scheduleMemo = schedule + Configuration.Memo(1)

        assertEquals(settingTag, saver.navigate(scheduleMemo, RootTab.SETTING))
        assertEquals(scheduleMemo, saver.navigate(settingTag, RootTab.SCHEDULE))
    }

    @Test
    fun `하위 화면에서 같은 탭을 다시 누르면 그대로 둔다`() {
        val saver = TabStackSaver()
        val stack = listOf(home, Configuration.Schedule, Configuration.EditTodo(7))

        assertEquals(stack, saver.navigate(stack, RootTab.SCHEDULE))
    }

    @Test
    fun `홈 탭은 항상 홈 루트로 돌아간다`() {
        val saver = TabStackSaver()
        val homeSub = listOf(home, Configuration.Memo(2))

        val setting = saver.navigate(homeSub, RootTab.SETTING)
        assertEquals(listOf(home), saver.navigate(setting, RootTab.HOME))
        assertEquals(listOf(home), saver.navigate(homeSub, RootTab.HOME))
    }

    @Test
    fun `복원한 스택은 한 번 쓰면 비워진다`() {
        val saver = TabStackSaver()
        saver.navigate(listOf(home, Configuration.Setting, Configuration.Theme), RootTab.HOME)

        assertEquals(listOf(home, Configuration.Setting, Configuration.Theme), saver.navigate(listOf(home), RootTab.SETTING))
        // 설정 루트로 돌아간 상태를 다시 저장 → 다음 복원은 루트
        saver.navigate(listOf(home, Configuration.Setting), RootTab.HOME)
        assertEquals(listOf(home, Configuration.Setting), saver.navigate(listOf(home), RootTab.SETTING))
    }

    @Test
    fun `clear 후에는 탭 루트로 간다`() {
        val saver = TabStackSaver()
        saver.navigate(listOf(home, Configuration.Schedule, Configuration.Memo(1)), RootTab.HOME)
        saver.clear()

        assertEquals(listOf(home, Configuration.Schedule), saver.navigate(listOf(home), RootTab.SCHEDULE))
    }
}
