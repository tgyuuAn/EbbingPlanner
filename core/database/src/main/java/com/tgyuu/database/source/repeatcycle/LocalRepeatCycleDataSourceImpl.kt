package com.tgyuu.database.source.repeatcycle

import com.tgyuu.database.converter.EbbingConverters
import com.tgyuu.database.dao.RepeatCyclesDao
import com.tgyuu.database.model.RepeatCycleEntity
import com.tgyuu.database.model.toEntity
import com.tgyuu.domain.model.RepeatCycle
import com.tgyuu.domain.model.sync.RepeatCycleForSync
import java.time.LocalDateTime
import javax.inject.Inject

class LocalRepeatCycleDataSourceImpl @Inject constructor(
    private val repeatCyclesDao: RepeatCyclesDao,
) : LocalRepeatCycleDataSource {
    override suspend fun insertRepeatCycle(intervals: List<Int>): Long =
        repeatCyclesDao.insertRepeatCycle(RepeatCycleEntity(intervals = intervals))

    override suspend fun updateRepeatCycle(repeatCycle: RepeatCycle) {
        val json = EbbingConverters().fromIntList(repeatCycle.intervals)
        repeatCyclesDao.updateRepeatCycle(
            id = repeatCycle.id,
            intervalsJson = json!!,
        )
    }

    override suspend fun softDeleteRepeatCycle(repeatCycle: RepeatCycle) =
        repeatCyclesDao.softDeleteRepeatCycle(repeatCycle.toEntity().id)

    override suspend fun hardDeleteRepeatCycle(repeatCycle: RepeatCycle) =
        repeatCyclesDao.hardDeleteRepeatCycle(repeatCycle.toEntity())

    override suspend fun hardDeleteAllRepeatCycles() = repeatCyclesDao.hardDeleteAllRepeatCycles()

    override suspend fun getRepeatCycles(): List<RepeatCycleEntity> =
        repeatCyclesDao.getRepeatCycles()

    override suspend fun getRepeatCycle(id: Int): RepeatCycleEntity =
        repeatCyclesDao.getRepeatCycle(id)

    override suspend fun getRepeatCyclesForSync(lastSyncTime: LocalDateTime): List<RepeatCycleForSync> =
        repeatCyclesDao.getRepeatCyclesForSync(lastSyncTime)
}
