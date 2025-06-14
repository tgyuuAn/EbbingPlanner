package com.tgyuu.domain.repository

import java.time.ZonedDateTime

interface SyncRepository {
    suspend fun ensureUUIDExists()
    suspend fun getUUID(): String
    suspend fun getServerLastUpdatedAt(): Result<ZonedDateTime?>
    suspend fun getLocalSyncedAt(): ZonedDateTime?
    suspend fun uploadData(): Result<ZonedDateTime>
    suspend fun downloadData(): Result<ZonedDateTime?>
}
