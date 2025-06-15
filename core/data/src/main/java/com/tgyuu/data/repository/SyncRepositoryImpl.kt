package com.tgyuu.data.repository

import com.tgyuu.common.suspendRunCatching
import com.tgyuu.datastore.datasource.sync.LocalSyncDataSource
import com.tgyuu.domain.repository.SyncRepository
import com.tgyuu.domain.repository.TodoRepository
import com.tgyuu.network.model.sync.GetSyncInfoResponse
import com.tgyuu.network.source.SyncDataSource
import com.tgyuu.network.toZonedDateTimeOrNull
import kotlinx.coroutines.flow.first
import java.time.ZonedDateTime
import javax.inject.Inject

class SyncRepositoryImpl @Inject constructor(
    private val todoRepository: TodoRepository,
    private val syncDataSource: SyncDataSource,
    private val localSyncDataSource: LocalSyncDataSource,
) : SyncRepository {
    override suspend fun ensureUUIDExists() = localSyncDataSource.ensureUUIDExists()
    override suspend fun getUUID(): String = localSyncDataSource.uuid.first()
    override suspend fun getServerLastUpdatedAt(): Result<ZonedDateTime?> =
        syncDataSource.getSyncInfo(getUUID())
            .map(GetSyncInfoResponse::toDomain)

    override suspend fun getLocalSyncedAt(): ZonedDateTime? = localSyncDataSource.syncedAt.first()

    override suspend fun uploadData(): Result<ZonedDateTime> {
        val uuid = getUUID()
        val schedules = todoRepository.loadSchedulesForSync()
        val infos = todoRepository.loadTodoInfosForSync()
        val repeatCycles = todoRepository.loadRepeatCyclesForSync()
        val tags = todoRepository.loadTagsForSync()

//        softDelete된 데이터 업로드 후 제거,

        return syncDataSource.uploadData(
            uuid = uuid,
            schedules = schedules,
            infos = infos,
            repeatCycles = repeatCycles,
            tags = tags,
        ).onSuccess {
            localSyncDataSource.setSyncedAt(it)
        }
    }

    override suspend fun downloadData(): Result<ZonedDateTime?> = suspendRunCatching {
        val uuid = getUUID()
        val response = syncDataSource.downloadData(uuid)
            .getOrThrow()

//        다운 받은 데이터

        response.syncedAt.toZonedDateTimeOrNull()
    }
}
