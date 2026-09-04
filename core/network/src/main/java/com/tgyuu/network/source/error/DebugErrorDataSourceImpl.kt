package com.tgyuu.network.source.error

import android.util.Log

class DebugErrorDataSourceImpl constructor() : ErrorDataSource {
    private var userId: String = ""

    override suspend fun logError(exception: Throwable) {
        Log.e(
            "DebugErrorDataSource",
            "userId : $userId, exception : ${exception.stackTraceToString()}"
        )
    }

    override suspend fun setUserId(userId: String) {
        this.userId = userId
    }

    override suspend fun clearUserId() {
        this.userId = ""
    }
}
