package com.tgyuu.tag.graph.edittag.contract

import com.tgyuu.common.base.UiState
import com.tgyuu.common.ui.InputState
import com.tgyuu.domain.model.DefaultTodoTag

data class EditTagState(
    val name: String = "",
    val nameInputState: InputState = InputState.DEFAULT,
    val colorValue: Int = DefaultTodoTag.color,
) : UiState {
    val isSaveEnabled: Boolean = name.isNotEmpty()
    val isInputFieldIncomplete: Boolean = nameInputState == InputState.WARNING
}
