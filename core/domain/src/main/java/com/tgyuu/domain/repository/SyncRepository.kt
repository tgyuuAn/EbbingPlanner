package com.tgyuu.domain.repository

import com.tgyuu.domain.model.sync.ConnectInfo
import kotlinx.datetime.LocalDateTime

interface SyncRepository {
    suspend fun ensureUUIDExists()
    suspend fun getUuid(): String
    suspend fun getConnectedUuid(): String?
    suspend fun getServerLastUpdatedAt(): LocalDateTime?
    suspend fun getLocalSyncedAt(): LocalDateTime?
    suspend fun syncUpData(): LocalDateTime
    suspend fun generateConnectCode(connectCode: String): LocalDateTime
    suspend fun getMyConnectCode(): String?
    suspend fun getConnectCodeExpiration(): LocalDateTime?
    suspend fun connectAnother(connectCode: String): ConnectInfo?
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
