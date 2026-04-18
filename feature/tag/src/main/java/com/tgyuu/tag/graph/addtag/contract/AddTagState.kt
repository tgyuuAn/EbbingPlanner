package com.tgyuu.tag.graph.addtag.contract

import com.tgyuu.common.base.UiState
import com.tgyuu.domain.model.DefaultTodoTag

data class AddTagState(
    val name: String = "",
    val colorValue: Int = DefaultTodoTag.color,
) : UiState {
    val isSaveEnabled: Boolean = name.isNotEmpty()
}
