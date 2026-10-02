package com.tgyuu.shared.domain.model.sync

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

data class TodoScheduleForSync(
    val id: Int,
    val infoId: Int,
    val date: LocalDate,
    val memo: String,
    val isPinned: Boolean,
    val isDone: Boolean,
    val createdAt: LocalDate,
    val isDeleted: Boolean,
    val updatedAt: LocalDateTime,
)
