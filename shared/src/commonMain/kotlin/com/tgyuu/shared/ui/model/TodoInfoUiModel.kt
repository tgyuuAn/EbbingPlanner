package com.tgyuu.shared.ui.model

import androidx.compose.runtime.Immutable

@Immutable
data class TodoInfoUiModel(
    val id: Int,
    val title: String,
    val tagId: Int,
)
