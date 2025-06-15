package com.tgyuu.data.repository

import com.tgyuu.common.suspendRunCatching
import com.tgyuu.database.source.repeatcycle.LocalRepeatCycleDataSource
import com.tgyuu.database.source.tag.LocalTagDataSource
import com.tgyuu.database.source.todo.LocalTodoDataSource
import com.tgyuu.datastore.datasource.sync.LocalSyncDataSource
import com.tgyuu.domain.model.sync.RepeatCycleForSync
import com.tgyuu.domain.model.sync.TodoInfoForSync
import com.tgyuu.domain.model.sync.TodoScheduleForSync
import com.tgyuu.domain.model.sync.TodoTagForSync
import com.tgyuu.domain.repository.SyncRepository
import com.tgyuu.network.model.sync.GetSyncInfoResponse
import com.tgyuu.network.source.SyncDataSource
import com.tgyuu.network.toDate
import com.tgyuu.network.toZonedDateTimeOrNull
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZonedDateTime
import javax.inject.Inject

class SyncRepositoryImpl @Inject constructor(
    private val syncDataSource: SyncDataSource,
    private val localTagDataSource: LocalTagDataSource,
    private val localTodoDataSource: LocalTodoDataSource,
    private val localRepeatCycleDataSource: LocalRepeatCycleDataSource,
    private val localSyncDataSource: LocalSyncDataSource,
) : SyncRepository {
    override suspend fun ensureUUIDExists() = localSyncDataSource.ensureUUIDExists()
    override suspend fun getUUID(): String = localSyncDataSource.uuid.first()
    override suspend fun getLinkedUUID(): String = localSyncDataSource.linkedUuid.first()
    override suspend fun getServerLastUpdatedAt(): Result<ZonedDateTime?> =
        syncDataSource.getSyncInfo(getUUID())
            .map(GetSyncInfoResponse::toDomain)

    override suspend fun getLocalSyncedAt(): ZonedDateTime? =
        localSyncDataSource.lastSyncTime.first()

    override suspend fun syncUpData(): Result<ZonedDateTime> = suspendRunCatching {
        downloadData().getOrThrow()
        uploadData().getOrThrow()
    }

    private suspend fun uploadData(): Result<ZonedDateTime> = coroutineScope {
        val uuid = getUUID()
        val schedules = async { loadSchedulesForSync() }
        val infos = async { loadTodoInfosForSync() }
        val repeatCycles = async { loadRepeatCyclesForSync() }
        val tags = async { loadTagsForSync() }

        syncDataSource.uploadData(
            uuid = uuid,
            schedules = schedules.await(),
            infos = infos.await(),
            repeatCycles = repeatCycles.await(),
            tags = tags.await(),
        ).onSuccess {
            // 업로드 이후 로컬에 softDelete 데이터 제거
            val schedulesDeleteJob = launch { localTodoDataSource.hardDeleteAllTodos() }
            val repeatCyclesDeleteJob =
                launch { localRepeatCycleDataSource.hardDeleteAllRepeatCycles() }
            val tagsDeleteJob = launch { localTagDataSource.hardDeleteAllTags() }

            schedulesDeleteJob.join()
            repeatCyclesDeleteJob.join()
            tagsDeleteJob.join()

            // 클라이언트 동기화 시간 갱신
            localSyncDataSource.setSyncedAt(it)
        }
    }

    private suspend fun downloadData(): Result<ZonedDateTime?> = suspendRunCatching {
        val uuid = getUUID()
        val lastSyncTime = localSyncDataSource.lastSyncTime.first()
            ?.toLocalDateTime() ?: LocalDateTime.MIN

        val response = syncDataSource.downloadData(uuid, lastSyncTime.toDate())
            .getOrThrow()

        response.syncedAt.toZonedDateTimeOrNull()
    }


    private suspend fun loadSchedulesForSync(): List<TodoScheduleForSync> {
        val lastSyncTime = localSyncDataSource.lastSyncTime.first()
            ?.toLocalDateTime() ?: LocalDateTime.MIN

        return localTodoDataSource.getSchedulesForSync(lastSyncTime)
    }

    private suspend fun loadTagsForSync(): List<TodoTagForSync> {
        val lastSyncTime = localSyncDataSource.lastSyncTime.first()
            ?.toLocalDateTime() ?: LocalDateTime.MIN

        return localTagDataSource.getTagsForSync(lastSyncTime)
    }

    private suspend fun loadRepeatCyclesForSync(): List<RepeatCycleForSync> {
        val lastSyncTime = localSyncDataSource.lastSyncTime.first()
            ?.toLocalDateTime() ?: LocalDateTime.MIN

        return localRepeatCycleDataSource.getRepeatCyclesForSync(lastSyncTime)
    }

    private suspend fun loadTodoInfosForSync(): List<TodoInfoForSync> {
        val lastSyncTime = localSyncDataSource.lastSyncTime.first()
            ?.toLocalDateTime() ?: LocalDateTime.MIN

        return localTodoDataSource.getTodoInfosForSync(lastSyncTime)
    }
}
