package com.tgyuu.network.source.log

import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LogDataSource @Inject constructor(
    private val workManager: WorkManager,
) {
    fun logHeapDump() {
        val data = Data.Builder()
            .putLong(HeapDumpWorker.KEY_START_TIME, System.currentTimeMillis())
            .build()

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED) // 와이파이만 허용
            .setRequiresBatteryNotLow(true) // 배터리 많을 때에만 작업
            .build()

        val dumpWork = OneTimeWorkRequestBuilder<HeapDumpWorker>()
            .setBackoffCriteria(
                // 재시도할 떄 마다 재시도 주기가 10초씩 증가
                BackoffPolicy.LINEAR,
                10,
                TimeUnit.SECONDS,
            )
            .setInputData(data)
            .setConstraints(constraints)
            .build()

        workManager.enqueue(dumpWork)
    }
}
