package com.tgyuu.domain.repository

import com.tgyuu.domain.model.sync.ConnectResult
import com.tgyuu.domain.model.sync.ConnectedPeer
import com.tgyuu.domain.model.sync.ServerSyncInfo
import java.time.ZonedDateTime

interface SyncRepository {
    suspend fun ensureUUIDExists()
    suspend fun getUuid(): String
    suspend fun getConnectedUuid(): String?
    suspend fun getDeviceName(): String
    suspend fun getServerLastUpdatedAt(): ServerSyncInfo?
    suspend fun getLocalSyncedAt(): ZonedDateTime?
    suspend fun syncUpData(): ZonedDateTime
    suspend fun generateConnectCode(connectCode: String): ZonedDateTime
    suspend fun getMyConnectCode(): String?
    suspend fun getConnectCodeExpiration(): ZonedDateTime?
    suspend fun connectAnother(connectCode: String): ConnectResult
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
