package com.tgyuu.domain.model

import java.time.LocalDate

data class TodoInfo(
    val id: Int,
    val title: String,
    val description: String,
    val tagId: Int,
    val createdAt: LocalDate,
    val updatedAt: LocalDate,
)
