package com.tgyuu.shared.domain

import com.tgyuu.shared.domain.model.TodoSchedule
import com.tgyuu.shared.domain.repository.ConfigRepository
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNull

class AlarmLogicTest {

    @Test
    fun parseAlarmTime_validValue() {
        assertEquals(7 to 5, ConfigRepository.parseAlarmTime("7:5"))
        assertEquals(23 to 59, ConfigRepository.parseAlarmTime("23:59"))
    }

    @Test
    fun parseAlarmTime_fallsBackTo1830WhenInvalid() {
        val default = 18 to 30
        assertEquals(default, ConfigRepository.parseAlarmTime(null))
        assertEquals(default, ConfigRepository.parseAlarmTime(""))
        assertEquals(default, ConfigRepository.parseAlarmTime("24:00"))
        assertEquals(default, ConfigRepository.parseAlarmTime("10:60"))
        assertEquals(default, ConfigRepository.parseAlarmTime("-1:10"))
        assertEquals(default, ConfigRepository.parseAlarmTime("ab:cd"))
        assertEquals(default, ConfigRepository.parseAlarmTime("10:20:30"))
    }

    @Test
    fun resolveAlarmMessage_replacesAllLocaleTokens() {
        assertEquals("영어 을 확인", ConfigRepository.resolveAlarmMessage("{할일} 을 확인", "영어"))
        assertEquals("Check A", ConfigRepository.resolveAlarmMessage("Check {task}", "A"))
        assertEquals("Aを確認", ConfigRepository.resolveAlarmMessage("{タスク}を確認", "A"))
        assertEquals("A/A/A", ConfigRepository.resolveAlarmMessage("{할일}/{task}/{タスク}", "A"))
        assertEquals("no token", ConfigRepository.resolveAlarmMessage("no token", "A"))
    }

    @Test
    fun firstUndoneTitle_pinnedFirstAmongUndone() {
        val list = listOf(
            schedule(1, "done-pinned", isPinned = true, isDone = true),
            schedule(2, "first-normal"),
            schedule(3, "pinned"),
            schedule(4, "pinned2", isPinned = true),
        ).let { it.take(2) + it[2] + it[3] }
        assertEquals("pinned2", AlarmRescheduler.firstUndoneTitle(list))
        assertEquals(
            "first-normal",
            AlarmRescheduler.firstUndoneTitle(listOf(schedule(2, "first-normal"), schedule(3, "second"))),
        )
        assertNull(AlarmRescheduler.firstUndoneTitle(listOf(schedule(1, "x", isDone = true))))
        assertNull(AlarmRescheduler.firstUndoneTitle(emptyList()))
    }

    @Test
    fun notificationIdOf_isStablePerDate() {
        assertEquals(20261031, AlarmRescheduler.notificationIdOf(LocalDate(2026, 10, 31)))
        assertNotEquals(
            AlarmRescheduler.notificationIdOf(LocalDate(2026, 1, 2)),
            AlarmRescheduler.notificationIdOf(LocalDate(2026, 2, 1)),
        )
    }

    private fun schedule(
        id: Int,
        title: String,
        isPinned: Boolean = false,
        isDone: Boolean = false,
    ) = TodoSchedule(
        id = id,
        infoId = id,
        title = title,
        tagId = 0,
        name = "",
        color = 0,
        date = LocalDate(2026, 10, 31),
        memo = "",
        isPinned = isPinned,
        isDone = isDone,
        createdAt = LocalDate(2026, 10, 1),
        infoCreatedAt = LocalDate(2026, 10, 1),
    )
}
