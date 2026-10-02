package com.tgyuu.shared.domain.repository

import com.tgyuu.shared.domain.model.sync.ConnectResult
import com.tgyuu.shared.domain.model.sync.ConnectedPeer
import com.tgyuu.shared.domain.model.sync.RestoreResult
import com.tgyuu.shared.domain.model.sync.ServerSyncInfo
import kotlinx.datetime.LocalDateTime

interface SyncRepository {
    suspend fun ensureUUIDExists()
    suspend fun getUuid(): String
    suspend fun getConnectedUuid(): String?
    suspend fun getDeviceName(): String
    suspend fun getServerLastUpdatedAt(): ServerSyncInfo?
    suspend fun getLocalSyncedAt(): LocalDateTime?
    suspend fun syncUpData(): LocalDateTime
    suspend fun generateConnectCode(connectCode: String): LocalDateTime
    suspend fun getMyConnectCode(): String?
    suspend fun getConnectCodeExpiration(): LocalDateTime?
    suspend fun connectAnother(connectCode: String): ConnectResult
    suspend fun restoreByDeviceId(deviceIdPrefix: String): RestoreResult
    suspend fun disconnectAnother()
    suspend fun pollConnectedPeer(): ConnectedPeer?
    suspend fun getStoredPeer(): ConnectedPeer?
    suspend fun setStoredPeer(peer: ConnectedPeer?)
    suspend fun setLinkCode(code: String?)
    suspend fun getLinkCode(): String?
    suspend fun isLinkAlive(): Boolean
    suspend fun clearLinkLocal()
    suspend fun clearMyConnectCode()
}
