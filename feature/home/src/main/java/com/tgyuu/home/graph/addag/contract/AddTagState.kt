package com.tgyuu.home.graph.addag.contract

import com.tgyuu.common.base.UiState
import com.tgyuu.domain.model.DefaultTodoTag
import com.tgyuu.home.graph.InputState

data class AddTagState(
    val name: String = "",
    val nameInputState: InputState = InputState.DEFAULT,
    val colorValue: Int = DefaultTodoTag.color,
) : UiState {
    val isSaveEnabled: Boolean = name.isNotEmpty()
    val isInputFieldIncomplete: Boolean = nameInputState == InputState.WARNING
}
