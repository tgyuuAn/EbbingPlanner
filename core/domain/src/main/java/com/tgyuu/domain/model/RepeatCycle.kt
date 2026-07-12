package com.tgyuu.domain.model

data class RepeatCycle(
    val id: Int,
    val intervals: List<Int>,
) {
    companion object {
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
