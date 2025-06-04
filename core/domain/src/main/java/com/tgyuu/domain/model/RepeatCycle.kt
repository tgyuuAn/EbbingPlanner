package com.tgyuu.domain.model

enum class RepeatCycle(val displayName: String, val intervals: List<Int>) {
    SAME_DAY("당일만", listOf(0)),
    D1_7_15("당일, 1일, 7일, 15일", listOf(0, 1, 7, 15)),
    D1_7_15_30("당일, 1일, 7일, 15일, 30일", listOf(0, 1, 7, 15, 30)),
    D1_7_15_30_60("당일, 1일, 7일, 15일, 30일, 60일", listOf(0, 1, 7, 15, 30, 60)),
}

//
//sealed class RepeatCycle(open val displayName: String, open val intervals: List<Int>) {
//    data object SameDay : RepeatCycle("당일만", listOf(0))
//    data object D1_7_15 : RepeatCycle("당일, 1일, 7일, 15일", listOf(0, 1, 7, 15))
//    data object D1_7_15_30 : RepeatCycle("당일, 1일, 7일, 15일, 30일", listOf(0, 1, 7, 15, 30))
//    data object D1_7_15_30_60 : RepeatCycle("당일, 1일, 7일, 15일, 30일, 60일", listOf(0, 1, 7, 15, 30, 60))
//
//    data class Custom(override val displayName: String, override val intervals: List<Int>) :
//        RepeatCycle(displayName, intervals)
//}
