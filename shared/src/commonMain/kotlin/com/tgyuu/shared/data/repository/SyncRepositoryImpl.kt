package com.tgyuu.shared.data.repository

import com.tgyuu.shared.common.now
import com.tgyuu.shared.data.source.SyncData
import com.tgyuu.shared.data.source.SyncDataSource
import com.tgyuu.shared.database.model.toEntity
import com.tgyuu.shared.database.dao.RepeatCyclesDao
import com.tgyuu.shared.database.dao.SyncDao
import com.tgyuu.shared.database.dao.TodoSchedulesDao
import com.tgyuu.shared.database.dao.TodoTagsDao
import com.tgyuu.shared.domain.model.sync.ConnectInfo
import com.tgyuu.shared.domain.repository.SyncRepository
import com.tgyuu.shared.platform.Settings
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlin.random.Random

class SyncRepositoryImpl(
    private val settings: Settings,
    private val syncDataSource: SyncDataSource,
    private val syncDao: SyncDao,
    private val schedulesDao: TodoSchedulesDao,
    private val repeatCyclesDao: RepeatCyclesDao? = null,
    private val tagsDao: TodoTagsDao? = null,
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

    override suspend fun getServerLastUpdatedAt(): LocalDateTime? =
        parseDateTime(settings.getString(KEY_SERVER_LAST_UPDATED, ""))

    override suspend fun getLocalSyncedAt(): LocalDateTime? =
        parseDateTime(settings.getString(KEY_LOCAL_SYNCED_AT, ""))

    override suspend fun syncUpData(): LocalDateTime {
        val uuid = getUuid()
        val lastSyncTime = getLocalSyncedAt()

        // Download remote changes first (연동된 기기가 있으면 그 기기 데이터를 받음)
        val downloadUuid = getConnectedUuid() ?: uuid
        val downloadSince = lastSyncTime ?: LocalDateTime(2000, 1, 1, 0, 0, 0)
        syncDataSource.downloadData(downloadUuid, downloadSince).onSuccess { syncData ->
            applyDownloadedData(syncData)
        }

        // Upload local changes
        val epoch = LocalDateTime(2000, 1, 1, 0, 0, 0)
        val syncTime = lastSyncTime ?: epoch
        val schedules = schedulesDao.loadAllSchedulesForSync(syncTime)
        val infos = schedulesDao.loadAllTodoInfosForSync(syncTime)
        val syncedAt = syncDataSource.uploadData(
            uuid = uuid,
            schedules = schedules,
            infos = infos,
            repeatCycles = repeatCyclesDao?.getRepeatCyclesForSync(syncTime)?.map { it.toSyncModel() } ?: emptyList(),
            tags = tagsDao?.getTagsForSync(syncTime) ?: emptyList(),
        )

        settings.putString(KEY_LOCAL_SYNCED_AT, syncedAt.toString())
        settings.putString(KEY_SERVER_LAST_UPDATED, syncedAt.toString())
        return syncedAt
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
        val uuid = getUuid()
        val expiration = syncDataSource.generateConnectCode(uuid, connectCode)
        settings.putString(KEY_CONNECT_CODE, connectCode)
        settings.putString(KEY_CODE_EXPIRATION, expiration.toString())
        return expiration
    }

    override suspend fun getMyConnectCode(): String? {
        val code = settings.getString(KEY_CONNECT_CODE, "")
        return code.ifEmpty { null }
    }

    override suspend fun getConnectCodeExpiration(): LocalDateTime? =
        parseDateTime(settings.getString(KEY_CODE_EXPIRATION, ""))

    override suspend fun connectAnother(connectCode: String): ConnectInfo? {
        val result = syncDataSource.connectAnother(connectCode)
        return result.getOrNull()?.also { info ->
            settings.putString(KEY_CONNECTED_UUID, info.uuid)
        }
    }

    override suspend fun disconnectAnother() {
        settings.remove(KEY_CONNECTED_UUID)
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
        private const val INVALID_UUID = "INVALID"
        private const val KEY_UUID = "UUID"
        private const val KEY_CONNECTED_UUID = "CONNECTED_UUID"
        private const val KEY_SERVER_LAST_UPDATED = "SERVER_LAST_UPDATED"
        private const val KEY_LOCAL_SYNCED_AT = "LOCAL_SYNCED_AT"
        private const val KEY_CONNECT_CODE = "CONNECT_CODE"
        private const val KEY_CODE_EXPIRATION = "CODE_EXPIRATION_TIME"
    }
}
