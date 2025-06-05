package com.tgyuu.repeatcycle.util

import com.tgyuu.domain.model.RepeatCycle.Companion.DISPLAY_ERROR

internal fun parsingIntervals(intervals: String): Result<List<Int>> = runCatching {
    intervals.split(",")
        .asSequence()
        .map { it.trim() }
        .filter { it.isNotBlank() }
        .filter { it.length < 4 }
        .map { it.toInt() }
        .distinct()
        .sorted()
        .toList()
}

fun List<Int>.toPreviewIntervals(): String {
    if (isEmpty()) return DISPLAY_ERROR

    return when {
        this.size == 1 && this.first() == 0 -> "당일만"
        else -> this.joinToString(", ") { day ->
            if (day == 0) "당일" else "${day}일"
        }
    }
}
