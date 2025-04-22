package com.tgyuu.domain

import java.time.LocalDate

data class TodoInfo(
    val id: Int,
    val title: String,
    val description: String,
    val tagId: Int,
    val startDate: LocalDate,
    val createdAt: LocalDate,
    val updatedAt: LocalDate,
)
