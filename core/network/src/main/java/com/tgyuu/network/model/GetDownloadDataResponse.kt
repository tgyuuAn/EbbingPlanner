package com.tgyuu.network.model

import com.google.firebase.Timestamp
import com.tgyuu.domain.model.RepeatCycle

data class GetDownloadDataResponse(
    val schedules: List<TodoScheduleDto> = emptyList(),
    val repeatCycles: List<RepeatCycle> = emptyList(),
    val tags: List<TodoTagDto> = emptyList(),
    val syncedAt: Timestamp? = null,
)
