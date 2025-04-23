package com.tgyuu.domain.model

import java.time.LocalDate

data class Todo(
    val id: Int,
    val infoId: Int,
    val date: LocalDate,
    val memo: String,
    val priority: Int,
    val isDone: Boolean,
    val createdAt: LocalDate,
    val updatedAt: LocalDate,
)
