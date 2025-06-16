package com.tgyuu.domain.repository

interface ErrorRepository {
    suspend fun logError(exception: Throwable)
    suspend fun setUserId(userId: String)
    suspend fun clearUserId()
}
