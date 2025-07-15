package com.tgyuu.data.repository

import com.tgyuu.domain.repository.LogRepository
import com.tgyuu.network.source.log.LogDataSource
import javax.inject.Inject

class LogRepositoryImpl @Inject constructor(
    private val logDataSource: LogDataSource,
) : LogRepository {
    override fun logHeapDump() = logDataSource.logHeapDump()
}
