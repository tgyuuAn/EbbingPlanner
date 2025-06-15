package com.tgyuu.database.source.repeatcycle

import com.tgyuu.database.model.RepeatCycleEntity
import com.tgyuu.domain.model.RepeatCycle
import com.tgyuu.domain.model.sync.RepeatCycleForSync
import java.time.LocalDateTime

interface LocalRepeatCycleDataSource {
    suspend fun insertRepeatCycle(intervals: List<Int>): Long
    suspend fun insertRepeatCycle(repeatCycle: RepeatCycleForSync): Long

    suspend fun updateRepeatCycle(repeatCycle: RepeatCycle)
    suspend fun updateRepeatCycle(repeatCycle: RepeatCycleForSync)

    suspend fun softDeleteRepeatCycle(repeatCycle: RepeatCycle)
    suspend fun hardDeleteRepeatCycle(id: Int)
    suspend fun hardDeleteAllRepeatCycles()

    suspend fun getRepeatCycles(): List<RepeatCycleEntity>
    suspend fun getRepeatCycle(id: Int): RepeatCycleEntity?
    suspend fun getRepeatCyclesForSync(lastSyncTime: LocalDateTime): List<RepeatCycleForSync>
}
