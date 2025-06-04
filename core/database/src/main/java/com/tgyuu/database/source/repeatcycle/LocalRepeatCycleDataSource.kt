package com.tgyuu.database.source.repeatcycle

import com.tgyuu.database.model.RepeatCycleEntity
import com.tgyuu.domain.model.RepeatCycle
import java.time.DayOfWeek

interface LocalRepeatCycleDataSource {
    suspend fun insertTag(repeatCycle: RepeatCycle): Long
    suspend fun insertTag(
        intervals: List<Int>,
        restDays: List<DayOfWeek>,
    ): Long

    suspend fun updateRepeatCycle(repeatCycle: RepeatCycle)
    suspend fun deleteRepeatCycle(repeatCycle: RepeatCycle)
    suspend fun getRepeatCycles(): List<RepeatCycleEntity>
    suspend fun getRepeatCycle(id: Int): RepeatCycleEntity
}
