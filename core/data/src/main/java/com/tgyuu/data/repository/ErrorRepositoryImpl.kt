package com.tgyuu.data.repository

import com.tgyuu.network.source.error.ErrorDataSource
import com.tgyuu.domain.repository.ErrorRepository

class ErrorRepositoryImpl constructor(
    private val errorDataSource: ErrorDataSource,
) : ErrorRepository {
    override suspend fun logError(exception: Throwable) = errorDataSource.logError(exception)
    override suspend fun setUserId(userId: String) = errorDataSource.setUserId(userId)
    override suspend fun clearUserId() = errorDataSource.clearUserId()
}
