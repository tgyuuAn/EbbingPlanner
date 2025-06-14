package com.tgyuu.domain.repository

interface SyncRepository {
    suspend fun ensureUUIDExists()
    suspend fun getUUID(): String
}
