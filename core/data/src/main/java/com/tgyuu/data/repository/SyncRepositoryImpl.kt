package com.tgyuu.data.repository

import com.tgyuu.database.source.repeatcycle.LocalRepeatCycleDataSource
import com.tgyuu.database.source.sync.LocalSyncTransactionDataSource
import com.tgyuu.database.source.tag.LocalTagDataSource
import com.tgyuu.database.source.todo.LocalTodoDataSource
import com.tgyuu.datastore.datasource.sync.LocalSyncDataSource
import com.tgyuu.domain.model.sync.ConnectInfo
import com.tgyuu.domain.model.sync.RepeatCycleForSync
import com.tgyuu.domain.model.sync.TodoInfoForSync
import com.tgyuu.domain.model.sync.TodoScheduleForSync
import com.tgyuu.domain.model.sync.TodoTagForSync
import com.tgyuu.domain.repository.SyncRepository
import com.tgyuu.network.defaultDate
import com.tgyuu.network.source.SyncDataSource
import com.tgyuu.network.toDate
import com.tgyuu.network.toLocalDateTime
import com.tgyuu.network.toZonedDateTimeOrNull
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import javax.inject.Inject

class SyncRepositoryImpl @Inject constructor(
    private val syncDataSource: SyncDataSource,
    private val localTagDataSource: LocalTagDataSource,
    private val localTodoDataSource: LocalTodoDataSource,
    private val localRepeatCycleDataSource: LocalRepeatCycleDataSource,
    private val localSyncDataSource: LocalSyncDataSource,
    private val localSyncTransactionDataSource: LocalSyncTransactionDataSource,
) : SyncRepository {
    override suspend fun ensureUUIDExists() = localSyncDataSource.ensureUUIDExists()
    override suspend fun getUuid(): String = localSyncDataSource.uuid.first()
    override suspend fun getConnectedUuid(): String? = localSyncDataSource.connectedUuid.first()
    override suspend fun getServerLastUpdatedAt(): ZonedDateTime? = coroutineScope {
        val uuidDeferred = async { getUuid() }
        val connectedUuidDeferred = async { getConnectedUuid() }

        val uuid = uuidDeferred.await()
        val connectedUuid = connectedUuidDeferred.await()

        syncDataSource.getSyncInfo(connectedUuid ?: uuid)
            .toDomain()
    }

    override suspend fun getLocalSyncedAt(): ZonedDateTime? =
        localSyncDataSource.lastSyncTime.first()

    override suspend fun syncUpData(): ZonedDateTime {
        downloadData()
        return uploadData()
    }

    override suspend fun generateConnectCode(connectCode: String): ZonedDateTime =
        coroutineScope {
            val response = syncDataSource.generateConnectCode(
                uuid = getUuid(),
                connectCode = connectCode,
            )

            val codeExpirationJob = launch {
                localSyncDataSource.setConnectCodeExpirationTime(response)
            }
            val connectCodeJob = launch {
                localSyncDataSource.setConnectCode(connectCode)
            }

            codeExpirationJob.join()
            connectCodeJob.join()
            response
        }

    override suspend fun getMyConnectCode(): String? = localSyncDataSource.connectCode.first()
    override suspend fun getConnectCodeExpiration(): ZonedDateTime? {
        val expiration = localSyncDataSource.connectCodeExpirationTime.first() ?: return null
        val now = ZonedDateTime.now()
        if (expiration.isAfter(now)) return expiration

        // 만료된 시간이라면 저장된 데이터를 비워줌
        localSyncDataSource.setConnectCodeExpirationTime(null)
        localSyncDataSource.setConnectCode(null)
        return null
    }

    override suspend fun connectAnother(connectCode: String): ConnectInfo? {
        val dto = syncDataSource.connectAnother(connectCode)
            .getOrThrow()
        if (dto == null) return null

        val info = dto.toDomain()
        if (!info.isValid()) return null

        val myUuid = getUuid()
        if (info.uuid == myUuid) return info

        localSyncDataSource.setLastSyncTime(null)
        replaceData()
        localSyncDataSource.setConnectedUuid(info.uuid)
        return info
    }

    override suspend fun disconnectAnother() {
        localSyncDataSource.setConnectedUuid(null)
        localSyncDataSource.setLastSyncTime(null)
    }

    private suspend fun uploadData(): ZonedDateTime = coroutineScope {
        val uuidDeferred = async { getUuid() }
        val linkedUuidDeferred = async { getConnectedUuid() }

        val schedules = async { loadSchedulesForSync() }
        val infos = async { loadTodoInfosForSync() }
        val repeatCycles = async { loadRepeatCyclesForSync() }
        val tags = async { loadTagsForSync() }

        val uuid = uuidDeferred.await()
        val linkedUuid = linkedUuidDeferred.await()

        val response = syncDataSource.uploadData(
            uuid = linkedUuid ?: uuid,
            schedules = schedules.await(),
            infos = infos.await(),
            repeatCycles = repeatCycles.await(),
            tags = tags.await(),
        )

        // 업로드 이후 로컬에 softDelete 데이터 제거
        val repeatCyclesDeleteJob =
            launch { localRepeatCycleDataSource.hardDeleteAllRepeatCycles() }
        localTodoDataSource.hardDeleteAllTodos()
        localTagDataSource.hardDeleteAllTags()
        repeatCyclesDeleteJob.join()

        // 클라이언트 동기화 시간 갱신
        localSyncDataSource.setLastSyncTime(response)
        response
    }

    private suspend fun downloadData() = coroutineScope {
        val uuidDeferred = async { getUuid() }
        val connectedUuidDeferred = async { getConnectedUuid() }

        val uuid = uuidDeferred.await()
        val connectedUuid = connectedUuidDeferred.await()

        val lastSyncTime = localSyncDataSource.lastSyncTime
            .first()
            ?.toLocalDateTime()
            ?.toDate() ?: defaultDate

        val response = syncDataSource.downloadData(connectedUuid ?: uuid, lastSyncTime)
            .getOrThrow()

        // 1 : 삽입/업데이트만 수행 (isDeleted가 아닌 항목들)
        // 각 항목에 대해서 updatedAt을 비교하여, 로컬보다 더 이후에 변경된 항목만 반영
        val repeatCyclesJob = launch {
            response.repeatCycles.forEach { dto ->
                val repeatCycle = dto.toDomain()

                if (!repeatCycle.isDeleted) {
                    val local = localRepeatCycleDataSource.getRepeatCycle(repeatCycle.id)

                    if (local == null) {
                        localRepeatCycleDataSource.insertRepeatCycle(repeatCycle)
                    } else if (repeatCycle.updatedAt >= local.updatedAt) {
                        localRepeatCycleDataSource.updateRepeatCycle(repeatCycle)
                    }
                }
            }
        }

        response.tags.forEach { dto ->
            val tag = dto.toDomain()

            if (!tag.isDeleted) {
                val local = localTagDataSource.getTag(tag.id)

                if (local == null) {
                    localTagDataSource.insertTag(tag)
                } else if (tag.updatedAt >= local.updatedAt) {
                    localTagDataSource.updateTag(tag)
                }
            }
        }

        response.todoInfos.forEach { dto ->
            val todoInfo = dto.toDomain()
            val local = localTodoDataSource.getTodoScheduleEntity(todoInfo.id)

            if (local == null) {
                localTodoDataSource.insertTodoInfo(todoInfo)
            } else if (todoInfo.updatedAt >= local.updatedAt) {
                localTodoDataSource.updateTodoInfo(todoInfo)
            }
        }

        response.schedules.forEach { dto ->
            val schedule = dto.toDomain()

            if (!schedule.isDeleted) {
                val local = localTodoDataSource.getTodoScheduleEntity(schedule.id)

                if (local == null) {
                    localTodoDataSource.insertSchedule(schedule)
                } else if (schedule.updatedAt >= local.updatedAt) {
                    localTodoDataSource.updateSchedule(schedule)
                }
            }
        }

        repeatCyclesJob.join()

        // 2 : Foreign Key 제약조건을 고려하여 삭제 수행
        // Schedule -> Tag -> RepeatCycle 순서로 삭제
        response.schedules.forEach { dto ->
            val schedule = dto.toDomain()
            if (schedule.isDeleted) {
                localTodoDataSource.hardDeleteTodo(schedule.id)
            }
        }

        response.tags.forEach { dto ->
            val tag = dto.toDomain()
            if (tag.isDeleted) {
                localTagDataSource.hardDeleteTag(tag.id)
            }
        }

        response.repeatCycles.forEach { dto ->
            val repeatCycle = dto.toDomain()
            if (repeatCycle.isDeleted) {
                localRepeatCycleDataSource.hardDeleteRepeatCycle(repeatCycle.id)
            }
        }
    }

    private suspend fun loadSchedulesForSync(): List<TodoScheduleForSync> {
        val lastSyncTime = localSyncDataSource.lastSyncTime.first()
            ?.toLocalDateTime() ?: defaultDate.toLocalDateTime()

        return localTodoDataSource.getSchedulesForSync(lastSyncTime)
    }

    private suspend fun loadTagsForSync(): List<TodoTagForSync> {
        val lastSyncTime = localSyncDataSource.lastSyncTime.first()
            ?.toLocalDateTime() ?: defaultDate.toLocalDateTime()

        return localTagDataSource.getTagsForSync(lastSyncTime)
    }

    private suspend fun loadRepeatCyclesForSync(): List<RepeatCycleForSync> {
        val lastSyncTime = localSyncDataSource.lastSyncTime.first()
            ?.toLocalDateTime() ?: defaultDate.toLocalDateTime()

        return localRepeatCycleDataSource.getRepeatCyclesForSync(lastSyncTime)
    }

    private suspend fun loadTodoInfosForSync(): List<TodoInfoForSync> {
        val lastSyncTime = localSyncDataSource.lastSyncTime.first()
            ?.toLocalDateTime() ?: defaultDate.toLocalDateTime()

        return localTodoDataSource.getTodoInfosForSync(lastSyncTime)
    }

    private suspend fun replaceData(): ZonedDateTime? = coroutineScope {
        val uuidDeferred = async { getUuid() }
        val connectedUuidDeferred = async { getConnectedUuid() }

        val uuid = uuidDeferred.await()
        val connectedUuid = connectedUuidDeferred.await()

        val lastSyncTime = localSyncDataSource.lastSyncTime
            .first()
            ?.toLocalDateTime()
            ?.toDate() ?: defaultDate

        val response = syncDataSource.downloadData(connectedUuid ?: uuid, lastSyncTime)
            .getOrThrow()

        val repeatCycles = response.repeatCycles.map { it.toDomain() }
        val tags = response.tags.map { it.toDomain() }
        val infos = response.todoInfos.map { it.toDomain() }
        val schedules = response.schedules.map { it.toDomain() }

        localSyncTransactionDataSource.replaceAllData(
            infos = infos,
            repeatCycles = repeatCycles,
            tags = tags,
            schedules = schedules
        )

        val syncedAt = response.syncedAt.toZonedDateTimeOrNull()
        localSyncDataSource.setLastSyncTime(syncedAt)
        syncedAt
    }
}
