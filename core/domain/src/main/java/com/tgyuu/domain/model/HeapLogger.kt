package com.tgyuu.domain.model

import com.tgyuu.domain.repository.LogRepository
import javax.inject.Inject

class HeapLogger @Inject constructor(
    private val logRepository: LogRepository,
) {
    fun logHeapDump() = logRepository.logHeapDump()
}
