package com.tgyuu.designsystem.model

import androidx.compose.runtime.Immutable

@Immutable
data class TodoInfoUiModel(
    val id: Int,
    val title: String,
    val tagId: Int,
)
