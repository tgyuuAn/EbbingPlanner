package com.tgyuu.domain.model.sync

import java.time.LocalDate
import java.time.LocalDateTime

data class TodoTagForSync(
    val id: Int,
    val name: String,
    val color: Int,
    val createdAt: LocalDate,
    val isDeleted: Boolean,
    val updatedAt: LocalDateTime,
) 
