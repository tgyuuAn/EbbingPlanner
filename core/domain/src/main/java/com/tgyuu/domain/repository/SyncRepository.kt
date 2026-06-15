package com.tgyuu.domain.repository

import com.tgyuu.domain.model.sync.ConnectInfo
import kotlinx.coroutines.flow.Flow
import java.time.ZonedDateTime

interface SyncRepository {
    suspend fun ensureUUIDExists()
    suspend fun getUuid(): String
    suspend fun getConnectedUuid(): String?
    suspend fun getServerLastUpdatedAt(): ZonedDateTime?
    suspend fun getLocalSyncedAt(): ZonedDateTime?
    suspend fun syncUpData(): ZonedDateTime
    suspend fun generateConnectCode(connectCode: String): ZonedDateTime
    suspend fun getMyConnectCode(): String?
    suspend fun getConnectCodeExpiration(): ZonedDateTime?
    suspend fun connectAnother(connectCode: String): ConnectInfo?
    suspend fun disconnectAnother()
    fun getBackupPending(): Flow<Boolean>
    suspend fun setBackupPending(pending: Boolean)
}
