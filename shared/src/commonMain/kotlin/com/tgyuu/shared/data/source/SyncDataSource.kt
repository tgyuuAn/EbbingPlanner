package com.tgyuu.shared.data.source

import com.tgyuu.shared.domain.model.sync.ConnectInfo
import com.tgyuu.shared.domain.model.sync.ConnectedPeer
import com.tgyuu.shared.domain.model.sync.RepeatCycleForSync
import com.tgyuu.shared.domain.model.sync.TodoInfoForSync
import com.tgyuu.shared.domain.model.sync.TodoScheduleForSync
import com.tgyuu.shared.domain.model.sync.TodoTagForSync
import kotlinx.datetime.LocalDateTime

/**
 * Sync data source interface - platform specific implementations
 * (Android core/network의 SyncRemoteDataSource를 kotlinx-datetime 기반으로 미러링)
 */
interface SyncDataSource {
    /** 서버의 sync_info(마지막 업로드 시각 + 기기 이름) 조회 */
    suspend fun getSyncInfo(uuid: String): SyncInfoResult?

    /** uuid prefix로 서버에 존재하는 기기 검색 (복원용) */
    suspend fun findSyncInfosByUuidPrefix(prefix: String): Result<List<SyncDeviceMatch>>

    /**
     * Upload local data to remote
     * @return lastUpdatedAt timestamp
     */
    suspend fun uploadData(
        uuid: String,
        deviceName: String,
        schedules: List<TodoScheduleForSync>,
        infos: List<TodoInfoForSync>,
        repeatCycles: List<RepeatCycleForSync>,
        tags: List<TodoTagForSync>,
    ): LocalDateTime

    /**
     * Download data from remote that was updated after lastSyncTime
     */
    suspend fun downloadData(
        uuid: String,
        lastSyncTime: LocalDateTime,
    ): Result<SyncData>

    /**
     * Generate a connect code for device linking
     * @return expiration time
     */
    suspend fun generateConnectCode(
        uuid: String,
        connectCode: String,
        deviceName: String,
    ): LocalDateTime

    /**
     * Try to connect using a code
     * @return ConnectInfo if code is valid, null otherwise
     */
    suspend fun connectAnother(connectCode: String): Result<ConnectInfo?>

    /** 코드 소유자에게 "누가 연결했는지" 마킹 */
    suspend fun markConnected(
        connectCode: String,
        connectorUuid: String,
        connectorDeviceName: String,
    )

    /** 내 코드로 연결한 상대 기기 조회 (폴링용) */
    suspend fun getConnectedPeer(connectCode: String): ConnectedPeer?

    /** 연결 코드 삭제 */
    suspend fun deleteConnectCode(connectCode: String)
}

data class SyncInfoResult(
    val lastUpdatedAt: LocalDateTime?,
    val deviceName: String,
)

data class SyncDeviceMatch(
    val uuid: String,
    val deviceName: String,
)

/**
 * Data class for sync data response
 */
data class SyncData(
    val schedules: List<TodoScheduleForSync>,
    val todoInfos: List<TodoInfoForSync>,
    val repeatCycles: List<RepeatCycleForSync>,
    val tags: List<TodoTagForSync>,
    val syncedAt: LocalDateTime?,
)
