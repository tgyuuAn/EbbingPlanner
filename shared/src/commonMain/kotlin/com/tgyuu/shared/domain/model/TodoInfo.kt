package com.tgyuu.shared.domain.model

import kotlinx.datetime.DayOfWeek

data class TodoInfo(
    val id: Int,
    val title: String,
    val tagId: Int,
    val restDays: Set<DayOfWeek> = emptySet(),
)
