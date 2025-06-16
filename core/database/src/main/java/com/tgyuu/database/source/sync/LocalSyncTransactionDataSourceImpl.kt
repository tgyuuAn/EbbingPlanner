package com.tgyuu.database.source.sync

import com.tgyuu.database.dao.SyncDao
import com.tgyuu.database.model.toEntity
import com.tgyuu.domain.model.sync.RepeatCycleForSync
import com.tgyuu.domain.model.sync.TodoInfoForSync
import com.tgyuu.domain.model.sync.TodoScheduleForSync
import com.tgyuu.domain.model.sync.TodoTagForSync
import javax.inject.Inject

class LocalSyncTransactionDataSourceImpl @Inject constructor(
    private val syncDao: SyncDao,
) : LocalSyncTransactionDataSource {
    override suspend fun replaceAllData(
        infos: List<TodoInfoForSync>,
        tags: List<TodoTagForSync>,
        schedules: List<TodoScheduleForSync>,
        repeatCycles: List<RepeatCycleForSync>,
    ) = syncDao.replaceAllData(
        infos = infos.map(TodoInfoForSync::toEntity),
        tags = tags.map(TodoTagForSync::toEntity),
        schedules = schedules.map(TodoScheduleForSync::toEntity),
        repeatCycles = repeatCycles.map(RepeatCycleForSync::toEntity),
    )
}
