package com.tgyuu.shared.data.source

import com.tgyuu.shared.domain.model.sync.ConnectInfo
import com.tgyuu.shared.domain.model.sync.RepeatCycleForSync
import com.tgyuu.shared.domain.model.sync.TodoInfoForSync
import com.tgyuu.shared.domain.model.sync.TodoScheduleForSync
import com.tgyuu.shared.domain.model.sync.TodoTagForSync
import kotlinx.datetime.LocalDateTime

/**
 * Sync data source interface - platform specific implementations
 */
interface SyncDataSource {
    /**
     * Upload local data to remote
     * @return lastUpdatedAt timestamp
     */
    suspend fun uploadData(
        uuid: String,
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
    ): LocalDateTime

    /**
     * Try to connect using a code
     * @return ConnectInfo if code is valid, null otherwise
     */
    suspend fun connectAnother(connectCode: String): Result<ConnectInfo?>

    /**
     * Get the last sync timestamp from remote
     */
    suspend fun getLastSyncTime(uuid: String): LocalDateTime?
}

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
