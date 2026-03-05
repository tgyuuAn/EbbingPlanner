package com.tgyuu.domain.model

import java.time.DayOfWeek

data class TodoInfo(
    val id: Int,
    val title: String,
    val tagId: Int,
    val restDays: Set<DayOfWeek> = emptySet(),
)
