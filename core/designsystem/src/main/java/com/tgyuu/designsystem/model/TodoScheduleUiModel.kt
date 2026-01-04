package com.tgyuu.designsystem.model

import androidx.compose.runtime.Immutable
import kotlinx.datetime.LocalDate

@Immutable
data class TodoScheduleUiModel(
    val id: Int,
    val infoId: Int,
    val title: ClickableText,
    val tagId: Int,
    val name: String,
    val color: Int,
    val date: LocalDate,
    val memo: ClickableText,
    val priority: Int,
    val isDone: Boolean,
    val createdAt: LocalDate,
    val infoCreatedAt: LocalDate,
)
