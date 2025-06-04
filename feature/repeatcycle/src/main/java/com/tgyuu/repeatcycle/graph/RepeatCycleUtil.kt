package com.tgyuu.repeatcycle.graph

fun parsingIntervals(intervals: String): Result<List<Int>> = runCatching {
    intervals.split(",")
        .map { it.trim() }
        .filter { it.isNotBlank() }
        .map { it.toInt() }
        .sorted()
}
