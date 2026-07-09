package com.tgyuu.data.repository

import com.tgyuu.database.source.repeatcycle.LocalRepeatCycleDataSource
import com.tgyuu.database.source.sync.LocalSyncTransactionDataSource
import com.tgyuu.database.source.tag.LocalTagDataSource
import com.tgyuu.database.source.todo.LocalTodoDataSource
import com.tgyuu.datastore.datasource.sync.LocalSyncDataSource
import com.tgyuu.deviceinfo.DeviceInfoProvider
import com.tgyuu.domain.model.sync.ConnectResult
import com.tgyuu.domain.model.sync.ConnectedPeer
import com.tgyuu.domain.model.sync.RepeatCycleForSync
import com.tgyuu.domain.model.sync.RestoreResult
import com.tgyuu.domain.model.sync.ServerSyncInfo
import com.tgyuu.domain.model.sync.TodoInfoForSync
import com.tgyuu.domain.model.sync.TodoScheduleForSync
import com.tgyuu.domain.model.sync.TodoTagForSync
import com.tgyuu.common.suspendRunCatching
import com.tgyuu.domain.repository.ErrorRepository
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
    private val deviceInfoProvider: DeviceInfoProvider,
    private val errorRepository: ErrorRepository,
) : SyncRepository {
    override suspend fun ensureUUIDExists() = localSyncDataSource.ensureUUIDExists()
    override suspend fun getUuid(): String = localSyncDataSource.uuid.first()
    override suspend fun getConnectedUuid(): String? = localSyncDataSource.connectedUuid.first()
    override suspend fun getDeviceName(): String = deviceInfoProvider.getDeviceName()

    override suspend fun getServerLastUpdatedAt(): ServerSyncInfo? = coroutineScope {
        val uuidDeferred = async { getUuid() }
        val connectedUuidDeferred = async { getConnectedUuid() }

        val uuid = uuidDeferred.await()
        val connectedUuid = connectedUuidDeferred.await()

        val result = syncDataSource.getSyncInfo(connectedUuid ?: uuid) ?: return@coroutineScope null

        ServerSyncInfo(
            lastUpdatedAt = result.lastUpdatedAt,
            connectedDeviceName = result.deviceName,
        )
    }

    override suspend fun getLocalSyncedAt(): ZonedDateTime? =
        localSyncDataSource.lastSyncTime.first()

    override suspend fun syncUpData(): ZonedDateTime {
        downloadData()
        return uploadData()
    }

    override suspend fun generateConnectCode(connectCode: String): ZonedDateTime =
        coroutineScope {
            suspendRunCatching { uploadData() }
                .onFailure { errorRepository.logError(it) }

            val response = syncDataSource.generateConnectCode(
                uuid = getUuid(),
                connectCode = connectCode,
                deviceName = getDeviceName(),
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

    override suspend fun connectAnother(connectCode: String): ConnectResult {
        if (getConnectedUuid() != null) return ConnectResult.AlreadyLinkedSelf

        val info = syncDataSource.connectAnother(connectCode).getOrThrow()
            ?: return ConnectResult.InvalidOrExpired

        if (!info.isValid()) return ConnectResult.InvalidOrExpired

        val myUuid = getUuid()
        if (info.uuid == myUuid) return ConnectResult.Success(info)

        val existingConnectedUuid = info.connectedUuid
        if (!existingConnectedUuid.isNullOrEmpty() && existingConnectedUuid != myUuid) {
            return ConnectResult.CodeAlreadyTaken
        }

        localSyncDataSource.setLastSyncTime(null)
        localSyncDataSource.setConnectedUuid(info.uuid)
        replaceData()

        syncDataSource.markConnected(
            connectCode = connectCode,
            connectorUuid = myUuid,
            connectorDeviceName = getDeviceName(),
        )
        localSyncDataSource.setLinkCode(connectCode)
        return ConnectResult.Success(info)
    }

    override suspend fun restoreByDeviceId(deviceIdPrefix: String): RestoreResult {
        val prefix = deviceIdPrefix.trim()
            .substringAfterLast('·')
            .substringAfterLast(' ')
            .trim()
            .lowercase()

        if (prefix.length < UUID_PREFIX_MIN_LENGTH) return RestoreResult.NotFound
        if (prefix.any { it !in UUID_ALLOWED_CHARS }) return RestoreResult.NotFound
        if (getUuid().lowercase().startsWith(prefix)) return RestoreResult.SelfDevice

        val matches = syncDataSource.findSyncInfosByUuidPrefix(prefix).getOrThrow()
        val target = when {
            matches.isEmpty() -> return RestoreResult.NotFound
            matches.size > 1 -> return RestoreResult.Ambiguous
            else -> matches.first()
        }

        val response = syncDataSource.downloadData(target.uuid, Date(0L)).getOrThrow()

        val hasAliveData = response.todoInfos.isNotEmpty() &&
            response.schedules.any { !it.isDeleted }
        if (!hasAliveData) return RestoreResult.EmptyData

        localSyncTransactionDataSource.replaceAllData(
            infos = response.todoInfos,
            repeatCycles = response.repeatCycles,
            tags = response.tags,
            schedules = response.schedules,
        )

        return RestoreResult.Success(target.deviceName)
    }

    override suspend fun disconnectAnother() {
        localSyncDataSource.linkCode.first()?.let { syncDataSource.deleteConnectCode(it) }
        localSyncDataSource.setConnectedUuid(null)
        localSyncDataSource.setLastSyncTime(null)
        localSyncDataSource.setPeer(null, null)
        localSyncDataSource.setLinkCode(null)
    }

    override suspend fun setLinkCode(code: String?) = localSyncDataSource.setLinkCode(code)

    override suspend fun getLinkCode(): String? = localSyncDataSource.linkCode.first()

    override suspend fun isLinkAlive(): Boolean {
        val code = localSyncDataSource.linkCode.first() ?: return false
        return syncDataSource.getConnectedPeer(code) != null
    }

    override suspend fun clearLinkLocal() {
        localSyncDataSource.setConnectedUuid(null)
        localSyncDataSource.setLastSyncTime(null)
        localSyncDataSource.setPeer(null, null)
        localSyncDataSource.setLinkCode(null)
    }

    override suspend fun clearMyConnectCode() {
        localSyncDataSource.setConnectCode(null)
        localSyncDataSource.setConnectCodeExpirationTime(null)
    }

    override suspend fun pollConnectedPeer(): ConnectedPeer? {
        val myConnectCode = localSyncDataSource.connectCode.first() ?: return null
        return syncDataSource.getConnectedPeer(myConnectCode)
    }

    override suspend fun getStoredPeer(): ConnectedPeer? {
        val uuid = localSyncDataSource.peerUuid.first() ?: return null
        val deviceName = localSyncDataSource.peerDeviceName.first() ?: return null
        return ConnectedPeer(uuid = uuid, deviceName = deviceName)
    }

    override suspend fun setStoredPeer(peer: ConnectedPeer?) {
        localSyncDataSource.setPeer(peer?.uuid, peer?.deviceName)
    }

    private suspend fun uploadData(): ZonedDateTime = coroutineScope {
        val uuidDeferred = async { getUuid() }
        val linkedUuidDeferred = async { getConnectedUuid() }
        val deviceNameDeferred = async { getDeviceName() }

        val schedules = async { loadSchedulesForSync() }
        val infos = async { loadTodoInfosForSync() }
        val repeatCycles = async { loadRepeatCyclesForSync() }
        val tags = async { loadTagsForSync() }

        val uuid = uuidDeferred.await()
        val linkedUuid = linkedUuidDeferred.await()
        val deviceName = deviceNameDeferred.await()

        val response = syncDataSource.uploadData(
            uuid = linkedUuid ?: uuid,
            deviceName = deviceName,
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
        const val UUID_PREFIX_MIN_LENGTH = 8
        const val UUID_ALLOWED_CHARS = "0123456789abcdef-"
    }
}
