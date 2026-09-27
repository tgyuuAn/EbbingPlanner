package com.tgyuu.ebbingplanner.backup.fake

import com.tgyuu.domain.repository.ErrorRepository

class FakeErrorRepository : ErrorRepository {
    val loggedErrors = mutableListOf<Throwable>()
    var userId: String? = null

    override suspend fun logError(exception: Throwable) {
        loggedErrors += exception
    }

    override suspend fun setUserId(userId: String) {
        this.userId = userId
    }

    override suspend fun clearUserId() {
        userId = ""
    }
}
