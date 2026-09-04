package com.tgyuu.shared.data.repository

import com.tgyuu.shared.common.deviceName
import com.tgyuu.shared.common.now
import com.tgyuu.shared.data.source.SyncData
import com.tgyuu.shared.data.source.SyncDataSource
import com.tgyuu.shared.database.dao.RepeatCyclesDao
import com.tgyuu.shared.database.dao.SyncDao
import com.tgyuu.shared.database.dao.TodoSchedulesDao
import com.tgyuu.shared.database.dao.TodoTagsDao
import com.tgyuu.shared.database.model.toEntity
import com.tgyuu.shared.domain.model.sync.ConnectResult
import com.tgyuu.shared.domain.model.sync.ConnectedPeer
import com.tgyuu.shared.domain.model.sync.RestoreResult
import com.tgyuu.shared.domain.model.sync.ServerSyncInfo
import com.tgyuu.shared.domain.repository.ErrorRepository
import com.tgyuu.shared.domain.repository.SyncRepository
import com.tgyuu.shared.platform.Settings
import kotlinx.datetime.LocalDateTime
import kotlin.random.Random

class SyncRepositoryImpl(
    private val settings: Settings,
    private val syncDataSource: SyncDataSource,
    private val syncDao: SyncDao,
    private val schedulesDao: TodoSchedulesDao,
    private val repeatCyclesDao: RepeatCyclesDao? = null,
    private val tagsDao: TodoTagsDao? = null,
    private val errorRepository: ErrorRepository? = null,
) : SyncRepository {

    override suspend fun ensureUUIDExists() {
        val uuid = settings.getString(KEY_UUID, INVALID_UUID)
        if (uuid == INVALID_UUID) {
            settings.putString(KEY_UUID, generateUUID())
        }
    }

    override suspend fun getUuid(): String {
        ensureUUIDExists()
        return settings.getString(KEY_UUID, INVALID_UUID)
    }

    override suspend fun getConnectedUuid(): String? {
        val uuid = settings.getString(KEY_CONNECTED_UUID, "")
        return uuid.ifEmpty { null }
    }

    override suspend fun getDeviceName(): String = deviceName()

    override suspend fun getServerLastUpdatedAt(): ServerSyncInfo? {
        val uuid = getUuid()
        val connectedUuid = getConnectedUuid()

        val result = syncDataSource.getSyncInfo(connectedUuid ?: uuid) ?: return null

        return ServerSyncInfo(
            lastUpdatedAt = result.lastUpdatedAt,
            connectedDeviceName = result.deviceName,
        )
    }

    override suspend fun getLocalSyncedAt(): LocalDateTime? =
        parseDateTime(settings.getString(KEY_LOCAL_SYNCED_AT, ""))

    override suspend fun syncUpData(): LocalDateTime {
        downloadData()
        return uploadData()
    }

    private suspend fun uploadData(): LocalDateTime {
        val uuid = getUuid()
        val linkedUuid = getConnectedUuid()
        val lastSyncTime = getLocalSyncedAt() ?: EPOCH

        val schedules = schedulesDao.loadAllSchedulesForSync(lastSyncTime)
        val infos = schedulesDao.loadAllTodoInfosForSync(lastSyncTime)
        val repeatCycles = repeatCyclesDao?.getRepeatCyclesForSync(lastSyncTime)
            ?.map { it.toSyncModel() } ?: emptyList()
        val tags = tagsDao?.getTagsForSync(lastSyncTime) ?: emptyList()

        val syncedAt = syncDataSource.uploadData(
            uuid = linkedUuid ?: uuid,
            deviceName = getDeviceName(),
            schedules = schedules,
            infos = infos,
            repeatCycles = repeatCycles,
            tags = tags,
        )

        // 업로드 이후 로컬에 softDelete 데이터 제거
        schedulesDao.hardDeleteAllSchedule()
        tagsDao?.hardDeleteAllTags()
        repeatCyclesDao?.hardDeleteAllRepeatCycles()

        // 클라이언트 동기화 시간 갱신
        settings.putString(KEY_LOCAL_SYNCED_AT, syncedAt.toString())
        return syncedAt
    }

    private suspend fun downloadData() {
        val uuid = getUuid()
        val downloadUuid = getConnectedUuid() ?: uuid
        val downloadSince = getLocalSyncedAt() ?: EPOCH

        syncDataSource.downloadData(downloadUuid, downloadSince)
            .getOrThrow()
            .let { applyDownloadedData(it) }
    }

    /**
     * 다운로드한 데이터를 로컬 DB에 머지한다. (last-write-wins: updatedAt이 더 최신인 것만 반영)
     */
    private suspend fun applyDownloadedData(data: SyncData) {
        // 1) 삽입/업데이트 (isDeleted 아닌 항목)
        data.repeatCycles.forEach { rc ->
            if (!rc.isDeleted) {
                val local = repeatCyclesDao?.getRepeatCycle(rc.id)
                if (local == null) repeatCyclesDao?.insertRepeatCycle(rc.toEntity())
                else if (rc.updatedAt >= local.updatedAt) repeatCyclesDao?.updateRepeatCycle(rc.toEntity())
            }
        }
        data.tags.forEach { tag ->
            if (!tag.isDeleted) {
                val local = tagsDao?.getTag(tag.id)
                if (local == null) tagsDao?.insertTag(tag.toEntity())
                else if (tag.updatedAt >= local.updatedAt) tagsDao?.updateTag(tag.toEntity())
            }
        }
        data.todoInfos.forEach { info ->
            if (tagsDao?.getTag(info.tagId) == null) return@forEach
            val local = schedulesDao.loadTodoInfoEntity(info.id)
            if (local == null) schedulesDao.insertTodoInfo(info.toEntity())
            else if (info.updatedAt >= local.updatedAt) schedulesDao.updateTodoInfo(info.toEntity())
        }
        data.schedules.forEach { s ->
            if (!s.isDeleted) {
                if (schedulesDao.loadTodoInfoEntity(s.infoId) == null) return@forEach
                val local = schedulesDao.loadTodoScheduleEntity(s.id)
                if (local == null) schedulesDao.insertTodoSchedule(s.toEntity())
                else if (s.updatedAt >= local.updatedAt) schedulesDao.updateTodoSchedule(s.toEntity())
            }
        }

        // 2) 삭제 (FK 제약 고려: 스케줄 → 태그/반복주기 순)
        data.schedules.forEach { if (it.isDeleted) schedulesDao.hardDeleteSchedule(it.id) }
        data.tags.forEach { if (it.isDeleted) tagsDao?.hardDeleteTag(it.id) }
        data.repeatCycles.forEach { if (it.isDeleted) repeatCyclesDao?.hardDeleteRepeatCycle(it.id) }
    }

    override suspend fun generateConnectCode(connectCode: String): LocalDateTime {
        runCatching { uploadData() }
            .onFailure { errorRepository?.logError(it) }

        val expiration = syncDataSource.generateConnectCode(
            uuid = getUuid(),
            connectCode = connectCode,
            deviceName = getDeviceName(),
        )
        settings.putString(KEY_CONNECT_CODE, connectCode)
        settings.putString(KEY_CODE_EXPIRATION, expiration.toString())
        return expiration
    }

    override suspend fun getMyConnectCode(): String? {
        val code = settings.getString(KEY_CONNECT_CODE, "")
        return code.ifEmpty { null }
    }

    override suspend fun getConnectCodeExpiration(): LocalDateTime? {
        val expiration = parseDateTime(settings.getString(KEY_CODE_EXPIRATION, "")) ?: return null
        val now = LocalDateTime.now()
        if (expiration > now) return expiration

        // 만료된 시간이라면 저장된 데이터를 비워줌
        settings.remove(KEY_CODE_EXPIRATION)
        settings.remove(KEY_CONNECT_CODE)
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

        settings.remove(KEY_LOCAL_SYNCED_AT)
        settings.putString(KEY_CONNECTED_UUID, info.uuid)
        replaceData()

        syncDataSource.markConnected(
            connectCode = connectCode,
            connectorUuid = myUuid,
            connectorDeviceName = getDeviceName(),
        )
        setLinkCode(connectCode)
        return ConnectResult.Success(info)
    }

    override suspend fun restoreByDeviceId(deviceIdPrefix: String): RestoreResult {
        if (getConnectedUuid() != null) return RestoreResult.LinkedDevice

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

        val response = syncDataSource.downloadData(target.uuid, EPOCH).getOrThrow()

        val hasAliveData = response.todoInfos.isNotEmpty() &&
            response.schedules.any { !it.isDeleted }
        if (!hasAliveData) return RestoreResult.EmptyData

        syncDao.replaceAllData(
            infos = response.todoInfos.map { it.toEntity() },
            repeatCycles = response.repeatCycles.map { it.toEntity() },
            tags = response.tags.map { it.toEntity() },
            schedules = response.schedules.map { it.toEntity() },
        )

        return RestoreResult.Success(target.deviceName)
    }

    override suspend fun disconnectAnother() {
        getLinkCode()?.let { syncDataSource.deleteConnectCode(it) }
        settings.remove(KEY_CONNECTED_UUID)
        settings.remove(KEY_LOCAL_SYNCED_AT)
        setStoredPeer(null)
        setLinkCode(null)
    }

    override suspend fun pollConnectedPeer(): ConnectedPeer? {
        val myConnectCode = getMyConnectCode() ?: return null
        return syncDataSource.getConnectedPeer(myConnectCode)
    }

    override suspend fun getStoredPeer(): ConnectedPeer? {
        val uuid = settings.getString(KEY_PEER_UUID, "").ifEmpty { null } ?: return null
        val deviceName = settings.getString(KEY_PEER_DEVICE_NAME, "").ifEmpty { null } ?: return null
        return ConnectedPeer(uuid = uuid, deviceName = deviceName)
    }

    override suspend fun setStoredPeer(peer: ConnectedPeer?) {
        if (peer == null) {
            settings.remove(KEY_PEER_UUID)
            settings.remove(KEY_PEER_DEVICE_NAME)
        } else {
            settings.putString(KEY_PEER_UUID, peer.uuid)
            settings.putString(KEY_PEER_DEVICE_NAME, peer.deviceName)
        }
    }

    override suspend fun setLinkCode(code: String?) {
        if (code == null) {
            settings.remove(KEY_LINK_CODE)
        } else {
            settings.putString(KEY_LINK_CODE, code)
        }
    }

    override suspend fun getLinkCode(): String? =
        settings.getString(KEY_LINK_CODE, "").ifEmpty { null }

    override suspend fun isLinkAlive(): Boolean {
        val code = getLinkCode() ?: return false
        return syncDataSource.getConnectedPeer(code) != null
    }

    override suspend fun clearLinkLocal() {
        settings.remove(KEY_CONNECTED_UUID)
        settings.remove(KEY_LOCAL_SYNCED_AT)
        setStoredPeer(null)
        setLinkCode(null)
    }

    override suspend fun clearMyConnectCode() {
        settings.remove(KEY_CONNECT_CODE)
        settings.remove(KEY_CODE_EXPIRATION)
    }

    private suspend fun replaceData(): LocalDateTime? {
        val uuid = getUuid()
        val downloadUuid = getConnectedUuid() ?: uuid
        val downloadSince = getLocalSyncedAt() ?: EPOCH

        val response = syncDataSource.downloadData(downloadUuid, downloadSince).getOrThrow()

        syncDao.replaceAllData(
            infos = response.todoInfos.map { it.toEntity() },
            repeatCycles = response.repeatCycles.map { it.toEntity() },
            tags = response.tags.map { it.toEntity() },
            schedules = response.schedules.map { it.toEntity() },
        )

        response.syncedAt?.let { settings.putString(KEY_LOCAL_SYNCED_AT, it.toString()) }
        return response.syncedAt
    }

    private fun parseDateTime(value: String): LocalDateTime? {
        if (value.isEmpty()) return null
        return try { LocalDateTime.parse(value) } catch (_: Exception) { null }
    }

    private fun generateUUID(): String {
        val chars = "abcdef0123456789"
        fun segment(len: Int) = buildString { repeat(len) { append(chars[Random.nextInt(chars.length)]) } }
        return "${segment(8)}-${segment(4)}-${segment(4)}-${segment(4)}-${segment(12)}"
    }

    companion object {
        private val EPOCH = LocalDateTime(1970, 1, 1, 0, 0)
        private const val UUID_PREFIX_MIN_LENGTH = 8
        private const val UUID_ALLOWED_CHARS = "0123456789abcdef-"

        private const val INVALID_UUID = "INVALID"
        private const val KEY_UUID = "UUID"
        private const val KEY_CONNECTED_UUID = "CONNECTED_UUID"
        private const val KEY_LOCAL_SYNCED_AT = "LOCAL_SYNCED_AT"
        private const val KEY_CONNECT_CODE = "CONNECT_CODE"
        private const val KEY_CODE_EXPIRATION = "CODE_EXPIRATION_TIME"
        private const val KEY_LINK_CODE = "LINK_CODE"
        private const val KEY_PEER_UUID = "PEER_UUID"
        private const val KEY_PEER_DEVICE_NAME = "PEER_DEVICE_NAME"
    }
}
