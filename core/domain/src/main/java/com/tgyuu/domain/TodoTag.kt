package com.tgyuu.domain

import java.time.LocalDate

data class TodoTag(
    val id: Int,
    val name: String,
    val color: Int,
    val startDate: LocalDate,
    val createdAt: LocalDate,
    val updatedAt: LocalDate,
)
