package com.tgyuu.network.source.error

interface ErrorDataSource {
    suspend fun logError(exception: Throwable)
    suspend fun logMessage(message: String)
    suspend fun setUserId(userId: String)
    suspend fun clearUserId()
}
