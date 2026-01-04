package com.tgyuu.domain.model.sync

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

data class TodoInfoForSync(
    val id: Int,
    val title: String,
    val tagId: Int,
    val createdAt: LocalDate,
    val updatedAt: LocalDateTime,
    val restDays: String = "",
)
