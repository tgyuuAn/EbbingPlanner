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
import com.tgyuu.network.source.SyncRemoteDataSource
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.util.Date
import javax.inject.Inject

class SyncRepositoryImpl @Inject constructor(
    private val syncDataSource: SyncRemoteDataSource,
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
        val info = syncDataSource.connectAnother(connectCode).getOrThrow()
            ?: return null

        if (!info.isValid()) return null

        val myUuid = getUuid()
        if (info.uuid == myUuid) return info

        localSyncDataSource.setLastSyncTime(null)
        localSyncDataSource.setConnectedUuid(info.uuid)
        replaceData()
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

        val lastSyncTime = localSyncDataSource.lastSyncTime.first()
            ?.let { Date.from(it.toInstant()) }
            ?: Date(0L)

        val response = syncDataSource.downloadData(connectedUuid ?: uuid, lastSyncTime)
            .getOrThrow()

        // 1 : 삽입/업데이트만 수행 (isDeleted가 아닌 항목들)
        // 각 항목에 대해서 updatedAt을 비교하여, 로컬보다 더 이후에 변경된 항목만 반영
        val repeatCyclesJob = launch {
            response.repeatCycles.forEach { repeatCycle ->
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

        response.tags.forEach { tag ->
            if (!tag.isDeleted) {
                val local = localTagDataSource.getTag(tag.id)

                if (local == null) {
                    localTagDataSource.insertTag(tag)
                } else if (tag.updatedAt >= local.updatedAt) {
                    localTagDataSource.updateTag(tag)
                }
            }
        }

        response.todoInfos.forEach { todoInfo ->
            val tagExists = localTagDataSource.getTag(todoInfo.tagId) != null
            if (!tagExists) return@forEach

            val local = localTodoDataSource.getTodoInfoEntity(todoInfo.id)

            if (local == null) {
                localTodoDataSource.insertTodoInfo(todoInfo)
            } else if (todoInfo.updatedAt >= local.updatedAt) {
                localTodoDataSource.updateTodoInfo(todoInfo)
            }
        }

        response.schedules.forEach { schedule ->
            if (!schedule.isDeleted) {
                val infoExists = localTodoDataSource.getTodoInfoEntity(schedule.infoId) != null
                if (!infoExists) return@forEach

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
        response.schedules.forEach { schedule ->
            if (schedule.isDeleted) {
                localTodoDataSource.hardDeleteTodo(schedule.id)
            }
        }

        response.tags.forEach { tag ->
            if (tag.isDeleted) {
                localTagDataSource.hardDeleteTag(tag.id)
            }
        }

        response.repeatCycles.forEach { repeatCycle ->
            if (repeatCycle.isDeleted) {
                localRepeatCycleDataSource.hardDeleteRepeatCycle(repeatCycle.id)
            }
        }
    }

    private suspend fun loadSchedulesForSync(): List<TodoScheduleForSync> {
        val lastSyncTime = localSyncDataSource.lastSyncTime.first()
            ?.toLocalDateTime() ?: EPOCH

        return localTodoDataSource.getSchedulesForSync(lastSyncTime)
    }

    private suspend fun loadTagsForSync(): List<TodoTagForSync> {
        val lastSyncTime = localSyncDataSource.lastSyncTime.first()
            ?.toLocalDateTime() ?: EPOCH

        return localTagDataSource.getTagsForSync(lastSyncTime)
    }

    private suspend fun loadRepeatCyclesForSync(): List<RepeatCycleForSync> {
        val lastSyncTime = localSyncDataSource.lastSyncTime.first()
            ?.toLocalDateTime() ?: EPOCH

        return localRepeatCycleDataSource.getRepeatCyclesForSync(lastSyncTime)
    }

    private suspend fun loadTodoInfosForSync(): List<TodoInfoForSync> {
        val lastSyncTime = localSyncDataSource.lastSyncTime.first()
            ?.toLocalDateTime() ?: EPOCH

        return localTodoDataSource.getTodoInfosForSync(lastSyncTime)
    }

    private suspend fun replaceData(): ZonedDateTime? = coroutineScope {
        val uuidDeferred = async { getUuid() }
        val connectedUuidDeferred = async { getConnectedUuid() }

        val uuid = uuidDeferred.await()
        val connectedUuid = connectedUuidDeferred.await()

        val lastSyncTime = localSyncDataSource.lastSyncTime.first()
            ?.let { Date.from(it.toInstant()) }
            ?: Date(0L)

        val response = syncDataSource.downloadData(connectedUuid ?: uuid, lastSyncTime)
            .getOrThrow()

        localSyncTransactionDataSource.replaceAllData(
            infos = response.todoInfos,
            repeatCycles = response.repeatCycles,
            tags = response.tags,
            schedules = response.schedules,
        )

        localSyncDataSource.setLastSyncTime(response.syncedAt)
        response.syncedAt
    }

    private companion object {
        val EPOCH: LocalDateTime = LocalDateTime.of(1970, 1, 1, 0, 0)
    }
}
