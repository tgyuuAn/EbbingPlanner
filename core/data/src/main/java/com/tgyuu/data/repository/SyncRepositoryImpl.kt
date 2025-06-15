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
import com.tgyuu.network.defaultDate
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
    override suspend fun getLinkedUUID(): String? = localSyncDataSource.linkedUuid.first()
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
        coroutineScope {
            val uuid = getUUID()
            val lastSyncTime = localSyncDataSource.lastSyncTime
                .first()
                ?.toLocalDateTime()
                ?.toDate() ?: defaultDate

            val response = syncDataSource.downloadData(uuid, lastSyncTime)
                .getOrThrow()

            // 각 항목에 대해서 updatedAt을 비교하여, 로컬보다 더 이후에 변경된 항목만 반영
            // 만약 softDeleted 된 항이라면 제거, id가 없다면 새로 생성, 그 외는 수정
            val repeatCyclesJob = launch {
                response.repeatCycles.forEach { dto ->
                    val repeatCycle = dto.toDomain()

                    if (repeatCycle.isDeleted) {
                        localRepeatCycleDataSource.hardDeleteRepeatCycle(repeatCycle.id)
                    } else {
                        val local = localRepeatCycleDataSource.getRepeatCycle(repeatCycle.id)

                        if (local == null) {
                            localRepeatCycleDataSource.insertRepeatCycle(repeatCycle)
                        } else if (repeatCycle.updatedAt > local.updatedAt) {
                            localRepeatCycleDataSource.updateRepeatCycle(repeatCycle)
                        }
                    }
                }
            }

            val todoInfosJob = launch {
                response.todoInfos.forEach { dto ->
                    val todoInfo = dto.toDomain()
                    val local = localTodoDataSource.getTodoScheduleEntity(todoInfo.id)

                    if (local == null) {
                        localTodoDataSource.insertTodoInfo(todoInfo)
                    } else if (todoInfo.updatedAt > local.updatedAt) {
                        localTodoDataSource.updateTodoInfo(todoInfo)
                    }
                }
            }

            val schedulesJob = launch {
                response.schedules.forEach { dto ->
                    val schedule = dto.toDomain()

                    if (schedule.isDeleted) {
                        localTodoDataSource.hardDeleteTodo(schedule.id)
                    } else {
                        val local = localTodoDataSource.getTodoScheduleEntity(schedule.id)

                        if (local == null) {
                            localTodoDataSource.insertSchedule(schedule)
                        } else if (schedule.updatedAt > local.updatedAt) {
                            localTodoDataSource.updateSchedule(schedule)
                        }
                    }
                }
            }

            val tagsJob = launch {
                response.tags.forEach { dto ->
                    val tag = dto.toDomain()

                    if (tag.isDeleted) {
                        localTagDataSource.hardDeleteTag(tag.id)
                    } else {
                        val local = localTagDataSource.getTag(tag.id)

                        if (local == null) {
                            localTagDataSource.insertTag(tag)
                        } else if (tag.updatedAt > local.updatedAt) {
                            localTagDataSource.updateTag(tag)
                        }
                    }
                }
            }

            repeatCyclesJob.join()
            todoInfosJob.join()
            tagsJob.join()
            schedulesJob.join()

            val syncedAt = response.syncedAt.toZonedDateTimeOrNull()
            localSyncDataSource.setSyncedAt(syncedAt)

            syncedAt
        }
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
