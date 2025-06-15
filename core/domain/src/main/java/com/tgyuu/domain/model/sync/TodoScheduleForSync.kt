package com.tgyuu.domain.model.sync

import java.time.LocalDate
import java.time.LocalDateTime

data class TodoScheduleForSync(
    val id: Int,
    val infoId: Int,
    val date: LocalDate,
    val memo: String,
    val priority: Int,
    val isDone: Boolean,
    val createdAt: LocalDate,
    val isDeleted: Boolean,
    val updatedAt: LocalDateTime,
)
