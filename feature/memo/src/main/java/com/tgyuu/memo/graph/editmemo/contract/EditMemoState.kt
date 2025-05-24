package com.tgyuu.memo.graph.editmemo.contract

import com.tgyuu.common.base.UiState
import com.tgyuu.common.ui.InputState
import com.tgyuu.domain.model.TodoSchedule

data class EditMemoState(
    val originSchedule: TodoSchedule? = null,
    val memo: String = "",
    val memoInputState: InputState = InputState.DEFAULT,
) : UiState {
    val isSaveEnabled = memo.isNotEmpty()
    val isInputFieldIncomplete: Boolean = memoInputState == InputState.WARNING
}
