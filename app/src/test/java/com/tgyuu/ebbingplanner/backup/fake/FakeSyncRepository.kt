package com.tgyuu.ebbingplanner.backup.fake

import com.tgyuu.domain.model.sync.ConnectInfo
import com.tgyuu.domain.repository.SyncRepository
import java.time.ZonedDateTime

class FakeSyncRepository : SyncRepository {
    var shouldSyncFail: Boolean = false
    var syncUpCallCount: Int = 0
    var serverLastUpdatedAt: ZonedDateTime? = null

    override suspend fun ensureUUIDExists() {}
    override suspend fun getUuid(): String = "test-uuid"
    override suspend fun getConnectedUuid(): String? = null
    override suspend fun getServerLastUpdatedAt(): ZonedDateTime? = serverLastUpdatedAt
    override suspend fun getLocalSyncedAt(): ZonedDateTime? = null

    override suspend fun syncUpData(): ZonedDateTime {
        syncUpCallCount++
        if (shouldSyncFail) throw Exception("sync failed")
        return ZonedDateTime.now()
    }

    override suspend fun generateConnectCode(connectCode: String): ZonedDateTime =
        ZonedDateTime.now()

    override suspend fun getMyConnectCode(): String? = null
    override suspend fun getConnectCodeExpiration(): ZonedDateTime? = null
    override suspend fun connectAnother(connectCode: String): ConnectInfo? = null
    override suspend fun disconnectAnother() {}
}
