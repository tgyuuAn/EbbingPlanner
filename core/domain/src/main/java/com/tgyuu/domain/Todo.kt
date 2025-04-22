package com.tgyuu.domain

import java.time.LocalDate

data class Todo(
    val id: Int,
    val infoId: Int,
    val tagId: Int,
    val date: LocalDate,
    val memo: String,
    val priority: Int,
    val isDone: Boolean,
    val createdAt: LocalDate,
    val updatedAt: LocalDate,
)
