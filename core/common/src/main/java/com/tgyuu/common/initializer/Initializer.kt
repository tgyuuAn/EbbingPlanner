package com.tgyuu.common.initializer

interface Initializer {
    val priority: Int
    suspend fun initialize()

    companion object {
        const val PRIORITY_LOW = 3
        const val PRIORITY_MID = 2
        const val PRIORITY_HIGH = 1
    }
}
