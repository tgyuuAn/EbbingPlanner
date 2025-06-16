package com.tgyuu.database.source.sync

import com.tgyuu.domain.model.sync.RepeatCycleForSync
import com.tgyuu.domain.model.sync.TodoInfoForSync
import com.tgyuu.domain.model.sync.TodoScheduleForSync
import com.tgyuu.domain.model.sync.TodoTagForSync

interface LocalSyncTransactionDataSource {
    suspend fun replaceAllData(
        infos: List<TodoInfoForSync>,
        tags: List<TodoTagForSync>,
        schedules: List<TodoScheduleForSync>,
        repeatCycles: List<RepeatCycleForSync>,
    )
}
