package com.tgyuu.shared.domain.model.sync

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

data class TodoTagForSync(
    val id: Int,
    val name: String,
    val color: Int,
    val createdAt: LocalDate,
    val isDeleted: Boolean,
    val updatedAt: LocalDateTime,
)
