package com.tgyuu.domain.model

import java.time.LocalDate

data class TodoSchedule(
    val id: Int,
    val infoId: Int,
    val title: String,
    val tagId: Int,
    val name: String,
    val color: Int,
    val date: LocalDate,
    val memo: String,
    val priority: Int,
    val isDone: Boolean,
    val createdAt: LocalDate,
)
