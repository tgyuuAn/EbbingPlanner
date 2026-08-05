package com.tgyuu.shared.common

/**
 * 콤마 구분 List<Int> 직렬화/역직렬화 공통 코덱.
 * Settings(최근 사용순)와 Room(TypeConverter)에서 동일한 와이어 포맷을 공유한다.
 */
fun List<Int>.toCsv(): String = joinToString(",")

fun String?.toIntListFromCsv(): List<Int> =
    this?.split(",")?.mapNotNull { it.trim().toIntOrNull() } ?: emptyList()
