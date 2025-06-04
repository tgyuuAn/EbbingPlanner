package com.tgyuu.domain.model

import java.time.DayOfWeek

data class RepeatCycle(
    val id: Int,
    val displayName: String,
    val intervals: List<Int>,
    val restDays: List<DayOfWeek>,
)

val DefaultRepeatCycles: List<RepeatCycle> = listOf(
    RepeatCycle(
        id = 0,
        displayName = "당일만",
        intervals = listOf(0),
        restDays = emptyList(),
    ),
    RepeatCycle(
        id = 1,
        displayName = "당일, 1일, 7일, 15일",
        intervals = listOf(0, 1, 7, 15),
        restDays = emptyList(),
    ),
    RepeatCycle(
        id = 2,
        displayName = "당일, 1일, 7일, 15일, 30일",
        intervals = listOf(0, 1, 7, 15, 30),
        restDays = emptyList(),
    ),
    RepeatCycle(
        id = 3,
        displayName = "당일, 1일, 7일, 15일, 30일, 60일",
        intervals = listOf(0, 1, 7, 15, 30, 60),
        restDays = emptyList(),
    ),
)
