package com.tgyuu.shared.platform

/**
 * iOS Firebase Crashlytics ErrorDataSource.
 * Delegates to native Swift FirebaseErrorBridge via lambda injection.
 */
class FirebaseErrorDataSource(
    private val onLogError: (String) -> Unit = {},
    private val onSetUserId: (String) -> Unit = {},
    private val onClearUserId: () -> Unit = {},
) : ErrorDataSource {
    override fun logError(exception: Throwable) {
        onLogError(exception.message ?: exception.toString())
    }

    override fun setUserId(userId: String) {
        onSetUserId(userId)
    }

    override fun clearUserId() {
        onClearUserId()
    }
}
