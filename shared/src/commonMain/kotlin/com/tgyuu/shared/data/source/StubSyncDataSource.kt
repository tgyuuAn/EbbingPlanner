package com.tgyuu.shared.data.source

import com.tgyuu.shared.domain.model.sync.ConnectInfo
import com.tgyuu.shared.domain.model.sync.ConnectedPeer
import com.tgyuu.shared.domain.model.sync.RepeatCycleForSync
import com.tgyuu.shared.domain.model.sync.TodoInfoForSync
import com.tgyuu.shared.domain.model.sync.TodoScheduleForSync
import com.tgyuu.shared.domain.model.sync.TodoTagForSync
import kotlinx.datetime.LocalDateTime

/**
 * Stub implementation for SyncDataSource.
 * Replace with Supabase or Ktor-based implementation for production.
 */
class StubSyncDataSource : SyncDataSource {
    override suspend fun getSyncInfo(uuid: String): SyncInfoResult? = null

    override suspend fun findSyncInfosByUuidPrefix(prefix: String): Result<List<SyncDeviceMatch>> =
        Result.failure(UnsupportedOperationException("Sync not yet implemented."))

    override suspend fun uploadData(
        uuid: String,
        deviceName: String,
        schedules: List<TodoScheduleForSync>,
        infos: List<TodoInfoForSync>,
        repeatCycles: List<RepeatCycleForSync>,
        tags: List<TodoTagForSync>,
    ): LocalDateTime {
        throw UnsupportedOperationException("Sync not yet implemented. Configure network backend.")
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
        deviceName: String,
    ): LocalDateTime {
        throw UnsupportedOperationException("Sync not yet implemented.")
    }

    override suspend fun connectAnother(connectCode: String): Result<ConnectInfo?> {
        return Result.failure(UnsupportedOperationException("Sync not yet implemented."))
    }

    override suspend fun markConnected(
        connectCode: String,
        connectorUuid: String,
        connectorDeviceName: String,
    ) {
        throw UnsupportedOperationException("Sync not yet implemented.")
    }

    override suspend fun getConnectedPeer(connectCode: String): ConnectedPeer? = null

    override suspend fun deleteConnectCode(connectCode: String) {
        // no-op
    }
}
