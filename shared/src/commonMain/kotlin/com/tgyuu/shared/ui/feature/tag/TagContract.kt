package com.tgyuu.shared.ui.feature.tag

import androidx.compose.runtime.Immutable
import com.tgyuu.shared.base.UiIntent
import com.tgyuu.shared.base.UiState
import com.tgyuu.shared.ui.model.TodoTagUiModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class TagState(
    val tagList: ImmutableList<TodoTagUiModel> = persistentListOf(),
    val isLoading: Boolean = true,
) : UiState

sealed class TagIntent : UiIntent {
    data object OnBackClick : TagIntent()
    data object OnAddClick : TagIntent()
    data class OnEditClick(val tag: TodoTagUiModel) : TagIntent()
    data class OnDeleteClick(val tag: TodoTagUiModel) : TagIntent()
}
