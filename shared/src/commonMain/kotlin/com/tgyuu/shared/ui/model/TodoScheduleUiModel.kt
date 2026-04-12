package com.tgyuu.shared.ui.model

import androidx.compose.runtime.Immutable
import kotlinx.datetime.LocalDate

@Immutable
data class TodoScheduleUiModel(
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
    val infoCreatedAt: LocalDate,
)
