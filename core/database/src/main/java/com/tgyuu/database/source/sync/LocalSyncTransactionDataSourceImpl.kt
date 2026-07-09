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
    ) {
        val tagEntities = tags.map(TodoTagForSync::toEntity)
        val tagIds = tagEntities.map { it.id }.toSet() + DEFAULT_TAG_ID

        val infoEntities = infos.map(TodoInfoForSync::toEntity)
            .filter { it.tagId in tagIds }
        val infoIds = infoEntities.map { it.id }.toSet()

        val scheduleEntities = schedules.map(TodoScheduleForSync::toEntity)
            .filter { it.infoId in infoIds }

        syncDao.replaceAllData(
            infos = infoEntities,
            tags = tagEntities,
            schedules = scheduleEntities,
            repeatCycles = repeatCycles.map(RepeatCycleForSync::toEntity),
        )
    }

    private companion object {
        private const val DEFAULT_TAG_ID = 1
    }
}
