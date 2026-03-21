package com.tgyuu.network.source.error

import com.google.firebase.crashlytics.FirebaseCrashlytics

class ErrorDataSourceImpl constructor(
    private val firebaseCrashlytics: FirebaseCrashlytics,
) : ErrorDataSource {
    override suspend fun logError(exception: Throwable) {
        firebaseCrashlytics.recordException(exception)
    }

    override suspend fun logMessage(message: String) {
        firebaseCrashlytics.log(message)
    }

    override suspend fun setUserId(userId: String) {
        firebaseCrashlytics.setUserId(userId)
    }

    override suspend fun clearUserId() {
        firebaseCrashlytics.setUserId("")
    }
}
