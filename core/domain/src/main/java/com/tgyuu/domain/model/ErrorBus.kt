package com.tgyuu.domain.model

import com.tgyuu.domain.repository.ErrorRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

class ErrorBus(private val errorRepository: ErrorRepository) {
    private val _errorEvent = Channel<Throwable>(DEFAULT_BUFFER_SIZE)
    val errorEvent = _errorEvent.receiveAsFlow()

    suspend fun sendError(error: Throwable) {
        _errorEvent.send(error)
        errorRepository.logError(error)
    }

    suspend fun setUserId(userId: String) = errorRepository.setUserId(userId)
    suspend fun clearUserId() = errorRepository.clearUserId()
}
