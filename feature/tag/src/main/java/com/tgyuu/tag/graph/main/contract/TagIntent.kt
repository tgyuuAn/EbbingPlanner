package com.tgyuu.tag.graph.main.contract

import com.tgyuu.common.base.UiIntent
import com.tgyuu.designsystem.model.TodoTagUiModel

sealed class TagIntent : UiIntent {
    data object OnBackClick : TagIntent()
    data class OnEditClick(val tag: TodoTagUiModel) : TagIntent()
    data class OnDeleteClick(val tag: TodoTagUiModel) : TagIntent()
}
