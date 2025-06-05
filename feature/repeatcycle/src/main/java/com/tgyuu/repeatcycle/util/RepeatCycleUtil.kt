package com.tgyuu.repeatcycle.util

fun parsingIntervals(intervals: String): Result<List<Int>> = runCatching {
    intervals.split(",")
        .map { it.trim() }
        .filter { it.isNotBlank() }
        .map { it.toInt() }
        .distinct()
        .sorted()
}
