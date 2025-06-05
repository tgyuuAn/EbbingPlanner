package com.tgyuu.database.source.repeatcycle

import com.tgyuu.database.model.RepeatCycleEntity
import com.tgyuu.domain.model.RepeatCycle

interface LocalRepeatCycleDataSource {
    suspend fun insertTag(repeatCycle: RepeatCycle): Long
    suspend fun insertRepeatCycle(intervals: List<Int>): Long
    suspend fun updateRepeatCycle(repeatCycle: RepeatCycle)
    suspend fun deleteRepeatCycle(repeatCycle: RepeatCycle)
    suspend fun getRepeatCycles(): List<RepeatCycleEntity>
    suspend fun getRepeatCycle(id: Int): RepeatCycleEntity
}
