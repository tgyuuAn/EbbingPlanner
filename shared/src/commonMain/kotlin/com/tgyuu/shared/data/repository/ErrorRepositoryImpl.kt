package com.tgyuu.shared.data.repository

import com.tgyuu.shared.domain.repository.ErrorRepository
import com.tgyuu.shared.platform.ErrorDataSource

class ErrorRepositoryImpl(
    private val errorDataSource: ErrorDataSource,
) : ErrorRepository {
    override suspend fun logError(exception: Throwable) {
        errorDataSource.logError(exception)
    }

    override suspend fun setUserId(userId: String) {
        errorDataSource.setUserId(userId)
    }

    override suspend fun clearUserId() {
        errorDataSource.clearUserId()
    }
}
