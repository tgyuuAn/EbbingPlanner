package com.tgyuu.tag.graph.main.contract

import androidx.compose.runtime.Immutable
import com.tgyuu.common.base.UiState
import com.tgyuu.designsystem.model.TodoTagUiModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class TagState(
    val tagList: ImmutableList<TodoTagUiModel> = persistentListOf(),
) : UiState
