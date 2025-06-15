package com.tgyuu.network.model.sync

import com.google.firebase.Timestamp

data class GetDownloadDataResponse(
    val schedules: List<TodoScheduleDto> = emptyList(),
    val todoInfos: List<TodoInfoDto> = emptyList(),
    val repeatCycles: List<RepeatCycleDto> = emptyList(),
    val tags: List<TodoTagDto> = emptyList(),
    val syncedAt: Timestamp? = null,
)
