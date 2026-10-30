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
        val deletedTagIds = tagEntities.filter { it.isDeleted }.map { it.id }.toSet() - DEFAULT_TAG_ID

        // 서버에는 삭제된 태그를 참조하는 todo_info가 남아 있을 수 있다(예: 데이터 초기화 후 업로드되지 않은 info).
        // 그대로 넣으면 이후 hardDeleteAllTags에서 FK 위반이 나므로, 태그 삭제 시와 같이 기본 태그로 옮긴다.
        val infoEntities = infos.map(TodoInfoForSync::toEntity)
            .filter { it.tagId in tagIds }
            .map { if (it.tagId in deletedTagIds) it.copy(tagId = DEFAULT_TAG_ID) else it }
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
