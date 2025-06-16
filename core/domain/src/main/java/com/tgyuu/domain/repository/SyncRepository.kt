package com.tgyuu.domain.repository

import java.time.ZonedDateTime

interface SyncRepository {
    suspend fun ensureUUIDExists()
    suspend fun getUuid(): String
    suspend fun getLinkedUUID(): String?
    suspend fun getServerLastUpdatedAt(): Result<ZonedDateTime?>
    suspend fun getLocalSyncedAt(): ZonedDateTime?
    suspend fun syncUpData(): Result<ZonedDateTime>
    suspend fun generateConnectCode(connectCode: String): Result<ZonedDateTime>
    suspend fun getMyConnectCode(): String?
    suspend fun getConnectCodeExpiration(): ZonedDateTime?
    suspend fun connectAnother(connectCode: String): Result<Unit>
}
