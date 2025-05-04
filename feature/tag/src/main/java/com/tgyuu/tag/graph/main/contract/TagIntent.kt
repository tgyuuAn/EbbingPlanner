package com.tgyuu.tag.graph.main.contract

import com.tgyuu.common.base.UiIntent
import com.tgyuu.domain.model.TodoTag

sealed class TagIntent : UiIntent {
    data object OnBackClick : TagIntent()
    data class OnEditClick(val tag: TodoTag) : TagIntent()
    data class OnDeleteClick(val tag: TodoTag) : TagIntent()
}
