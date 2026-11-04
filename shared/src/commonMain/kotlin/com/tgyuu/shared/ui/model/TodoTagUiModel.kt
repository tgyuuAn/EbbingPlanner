package com.tgyuu.shared.ui.model

import androidx.compose.runtime.Immutable
import kotlinx.datetime.LocalDate

@Immutable
data class TodoTagUiModel(
    val id: Int,
    val name: String,
    val color: Int,
    val createdAt: LocalDate,
)
