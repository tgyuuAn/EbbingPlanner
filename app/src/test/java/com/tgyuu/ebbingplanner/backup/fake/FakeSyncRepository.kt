package com.tgyuu.ebbingplanner.backup.fake

import com.tgyuu.domain.model.sync.ConnectResult
import com.tgyuu.domain.model.sync.ConnectedPeer
import com.tgyuu.domain.model.sync.RestoreResult
import com.tgyuu.domain.model.sync.ServerSyncInfo
import com.tgyuu.domain.repository.SyncRepository
import java.time.ZonedDateTime

class FakeSyncRepository : SyncRepository {
    var shouldSyncFail: Boolean = false
    var syncUpCallCount: Int = 0
    var serverLastUpdatedAt: ZonedDateTime? = null

    override suspend fun ensureUUIDExists() {}
    override suspend fun getUuid(): String = "test-uuid"
    override suspend fun getConnectedUuid(): String? = null
    override suspend fun getDeviceName(): String = "test-device"
    override suspend fun getServerLastUpdatedAt(): ServerSyncInfo? =
        serverLastUpdatedAt?.let { ServerSyncInfo(lastUpdatedAt = it, connectedDeviceName = "") }
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
    override suspend fun connectAnother(connectCode: String): ConnectResult =
        ConnectResult.InvalidOrExpired
    override suspend fun restoreByDeviceId(deviceIdPrefix: String): RestoreResult =
        RestoreResult.NotFound
    override suspend fun disconnectAnother() {}
    override suspend fun pollConnectedPeer(): ConnectedPeer? = null
    override suspend fun getStoredPeer(): ConnectedPeer? = null
    override suspend fun setStoredPeer(peer: ConnectedPeer?) {}
    override suspend fun setLinkCode(code: String?) {}
    override suspend fun getLinkCode(): String? = null
    override suspend fun isLinkAlive(): Boolean = false
    override suspend fun clearLinkLocal() {}
    override suspend fun clearMyConnectCode() {}
}
