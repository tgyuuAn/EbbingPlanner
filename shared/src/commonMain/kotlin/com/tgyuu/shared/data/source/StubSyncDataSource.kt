package com.tgyuu.shared.data.source

import com.tgyuu.shared.domain.model.sync.ConnectInfo
import com.tgyuu.shared.domain.model.sync.RepeatCycleForSync
import com.tgyuu.shared.domain.model.sync.TodoInfoForSync
import com.tgyuu.shared.domain.model.sync.TodoScheduleForSync
import com.tgyuu.shared.domain.model.sync.TodoTagForSync
import kotlinx.datetime.LocalDateTime

/**
 * Stub implementation for SyncDataSource.
 * Replace with Firebase Firestore or Ktor-based implementation for production.
 */
class StubSyncDataSource : SyncDataSource {
    override suspend fun uploadData(
        uuid: String,
        schedules: List<TodoScheduleForSync>,
        infos: List<TodoInfoForSync>,
        repeatCycles: List<RepeatCycleForSync>,
        tags: List<TodoTagForSync>,
    ): LocalDateTime {
        throw UnsupportedOperationException("Sync not yet implemented. Configure Firebase or network backend.")
    }

    override suspend fun downloadData(
        uuid: String,
        lastSyncTime: LocalDateTime,
    ): Result<SyncData> {
        return Result.failure(UnsupportedOperationException("Sync not yet implemented."))
    }

    override suspend fun generateConnectCode(
        uuid: String,
        connectCode: String,
    ): LocalDateTime {
        throw UnsupportedOperationException("Sync not yet implemented.")
    }

    override suspend fun connectAnother(connectCode: String): Result<ConnectInfo?> {
        return Result.failure(UnsupportedOperationException("Sync not yet implemented."))
    }

    override suspend fun getLastSyncTime(uuid: String): LocalDateTime? = null
}
