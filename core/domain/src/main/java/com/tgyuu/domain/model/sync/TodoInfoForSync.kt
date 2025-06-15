package com.tgyuu.domain.model.sync

import java.time.LocalDate
import java.time.LocalDateTime

data class TodoInfoForSync(
    val id: Int,
    val title: String,
    val tagId: Int,
    val createdAt: LocalDate,
    val updatedAt: LocalDateTime,
)
