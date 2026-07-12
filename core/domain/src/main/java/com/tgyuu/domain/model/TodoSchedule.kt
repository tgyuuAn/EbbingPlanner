package com.tgyuu.domain.model

import kotlinx.datetime.LocalDate

data class TodoSchedule(
    val id: Int,
    val infoId: Int,
    val title: String,
    val tagId: Int,
    val name: String,
    val color: Int,
    val date: LocalDate,
    val memo: String,
    val isPinned: Boolean,
    val isDone: Boolean,
    val createdAt: LocalDate,
    val infoCreatedAt: LocalDate,
)
