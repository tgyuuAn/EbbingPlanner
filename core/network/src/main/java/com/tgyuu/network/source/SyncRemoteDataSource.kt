package com.tgyuu.network.source

import com.tgyuu.domain.model.sync.ConnectInfo
import com.tgyuu.domain.model.sync.ConnectedPeer
import com.tgyuu.domain.model.sync.RepeatCycleForSync
import com.tgyuu.domain.model.sync.TodoInfoForSync
import com.tgyuu.domain.model.sync.TodoScheduleForSync
import com.tgyuu.domain.model.sync.TodoTagForSync
import java.time.ZonedDateTime
import java.util.Date

data class SyncInfoResult(
    val lastUpdatedAt: ZonedDateTime?,
    val deviceName: String,
)

data class SyncDeviceMatch(
    val uuid: String,
    val deviceName: String,
)

data class SyncDownloadResult(
    val schedules: List<TodoScheduleForSync>,
    val todoInfos: List<TodoInfoForSync>,
    val repeatCycles: List<RepeatCycleForSync>,
    val tags: List<com.tgyuu.domain.model.sync.TodoTagForSync>,
    val syncedAt: ZonedDateTime?,
)

interface SyncRemoteDataSource {
    suspend fun getSyncInfo(uuid: String): SyncInfoResult?
    suspend fun findSyncInfosByUuidPrefix(prefix: String): Result<List<SyncDeviceMatch>>
    suspend fun uploadData(
        uuid: String,
        deviceName: String,
        schedules: List<TodoScheduleForSync>,
        infos: List<TodoInfoForSync>,
        repeatCycles: List<RepeatCycleForSync>,
        tags: List<TodoTagForSync>,
    ): ZonedDateTime
    suspend fun downloadData(uuid: String, lastSyncTime: Date): Result<SyncDownloadResult>
    suspend fun generateConnectCode(uuid: String, connectCode: String, deviceName: String): ZonedDateTime
    suspend fun connectAnother(connectCode: String): Result<ConnectInfo?>
    suspend fun markConnected(connectCode: String, connectorUuid: String, connectorDeviceName: String)
    suspend fun getConnectedPeer(connectCode: String): ConnectedPeer?
    suspend fun deleteConnectCode(connectCode: String)
}
