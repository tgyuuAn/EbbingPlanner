package com.tgyuu.shared.domain.model

data class RepeatCycle(
    val id: Int,
    val intervals: List<Int>,
) {
    fun toDisplayName(): String {
        if (intervals.isEmpty()) return DISPLAY_ERROR

        return when {
            id == DAILY_REPEAT_ID && intervals.size <= 1 -> "매일하기"
            id == DAILY_REPEAT_ID -> "매일하기 (${intervals.size}일)"
            intervals.size == 1 && intervals.first() == 0 -> "당일만"
            else -> intervals.joinToString(", ") { day ->
                if (day == 0) "당일" else "${day}일"
            }
        }
    }

    companion object {
        const val DISPLAY_ERROR = "올바른 형태로 작성해주세요."
        const val DAILY_REPEAT_ID = -5
        const val MAX_DAILY_REPEAT_DAYS = 365
    }
}

val DefaultRepeatCycles: List<RepeatCycle> = listOf(
    RepeatCycle(
        id = -1,
        intervals = listOf(0),
    ),
    RepeatCycle(
        id = RepeatCycle.DAILY_REPEAT_ID,
        intervals = listOf(0),
    ),
    RepeatCycle(
        id = -2,
        intervals = listOf(0, 1, 7, 15),
    ),
    RepeatCycle(
        id = -3,
        intervals = listOf(0, 1, 7, 15, 30),
    ),
    RepeatCycle(
        id = -4,
        intervals = listOf(0, 1, 7, 15, 30, 60),
    ),
)
