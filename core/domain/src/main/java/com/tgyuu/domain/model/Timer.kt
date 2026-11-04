package com.tgyuu.domain.model

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class Timer {
    fun startTimer(durationInSec: Long = DEFAULT_DURATION_IN_SEC): Flow<Long> = flow {
        var remainingTime = durationInSec

        while (remainingTime > 0) {
            emit(remainingTime)
            delay(TICK_INTERVAL)
            remainingTime--
        }

        emit(TIMEOUT_FLAG) // 타이머 만료를 나타내는 플래그를 방출
    }

    companion object {
        const val DEFAULT_DURATION_IN_SEC = 600L
        const val TIMEOUT_FLAG = -1L
        const val TICK_INTERVAL = 1000L
    }
}
