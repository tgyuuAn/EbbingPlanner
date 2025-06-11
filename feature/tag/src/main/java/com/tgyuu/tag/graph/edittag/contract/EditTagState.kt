package com.tgyuu.tag.graph.edittag.contract

import com.tgyuu.common.base.UiState
import com.tgyuu.domain.model.DefaultTodoTag
import com.tgyuu.domain.model.TodoTag

data class EditTagState(
    val originTag: TodoTag? = null,
    val name: String = "",
    val colorValue: Int = DefaultTodoTag.color,
) : UiState {
    val isSaveEnabled: Boolean = name.isNotEmpty()
}
