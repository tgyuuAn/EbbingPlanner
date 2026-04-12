package com.tgyuu.shared.platform

interface ErrorDataSource {
    fun logError(exception: Throwable)
    fun setUserId(userId: String)
    fun clearUserId()
}

class DebugErrorDataSource : ErrorDataSource {
    private var userId: String = ""

    override fun logError(exception: Throwable) {
        println("Error [userId=$userId]: ${exception.message}\n${exception.stackTraceToString()}")
    }

    override fun setUserId(userId: String) {
        this.userId = userId
    }

    override fun clearUserId() {
        userId = ""
    }
}
