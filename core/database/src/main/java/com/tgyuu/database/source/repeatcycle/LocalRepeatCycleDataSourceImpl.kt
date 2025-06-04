package com.tgyuu.database.source.repeatcycle

import com.tgyuu.database.dao.RepeatCyclesDao
import com.tgyuu.database.model.RepeatCycleEntity
import com.tgyuu.database.model.toEntity
import com.tgyuu.domain.model.RepeatCycle
import java.time.DayOfWeek
import javax.inject.Inject

class LocalRepeatCycleDataSourceImpl @Inject constructor(
    private val repeatCyclesDao: RepeatCyclesDao,
) : LocalRepeatCycleDataSource {
    override suspend fun insertTag(repeatCycle: RepeatCycle): Long =
        repeatCyclesDao.insertRepeatCycle(repeatCycle.toEntity())

    override suspend fun insertTag(intervals: List<Int>, restDays: List<DayOfWeek>): Long =
        repeatCyclesDao.insertRepeatCycle(
            RepeatCycleEntity(
                intervals = intervals,
                restDays = restDays,
            )
        )

    override suspend fun updateRepeatCycle(repeatCycle: RepeatCycle) =
        repeatCyclesDao.updateRepeatCycle(repeatCycle.toEntity())

    override suspend fun deleteRepeatCycle(repeatCycle: RepeatCycle) =
        repeatCyclesDao.deleteRepeatCycle(repeatCycle.toEntity())

    override suspend fun getRepeatCycles(): List<RepeatCycleEntity> =
        repeatCyclesDao.getRepeatCycles()

    override suspend fun getRepeatCycle(id: Int): RepeatCycleEntity =
        repeatCyclesDao.getRepeatCycle(id)
}
