package com.tgyuu.domain

enum class RepeatCycle(val displayName: String, val intervals: List<Int>) {
    SAME_DAY("당일만", listOf(0)),
    D1_7_15("1일, 7일, 15일", listOf(1, 7, 15)),
    D1_7_15_30("1일, 7일, 15일, 30일", listOf(1, 7, 15, 30)),
    D1_7_15_30_60("1일, 7일, 15일, 30일, 60일", listOf(1, 7, 15, 30, 60)),
}
