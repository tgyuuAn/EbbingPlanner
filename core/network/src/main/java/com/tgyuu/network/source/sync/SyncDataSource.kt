package com.tgyuu.network.source.sync

import com.tgyuu.domain.model.sync.RepeatCycleForSync
import com.tgyuu.domain.model.sync.TodoInfoForSync
import com.tgyuu.domain.model.sync.TodoScheduleForSync
import com.tgyuu.domain.model.sync.TodoTagForSync
import com.tgyuu.network.model.sync.GetDownloadDataResponse
import com.tgyuu.network.model.sync.GetSyncInfoResponse
import java.time.ZonedDateTime
import java.util.Date

interface SyncDataSource {
    suspend fun getSyncInfo(uuid: String): Result<GetSyncInfoResponse>

    suspend fun uploadData(
        uuid: String,
        schedules: List<TodoScheduleForSync>,
        infos: List<TodoInfoForSync>,
        repeatCycles: List<RepeatCycleForSync>,
        tags: List<TodoTagForSync>,
    ): Result<ZonedDateTime>

    suspend fun downloadData(uuid: String, lastSyncTime: Date): Result<GetDownloadDataResponse>
}
