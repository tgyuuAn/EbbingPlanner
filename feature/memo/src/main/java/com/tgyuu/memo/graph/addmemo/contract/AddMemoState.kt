package com.tgyuu.memo.graph.addmemo.contract

import com.tgyuu.common.base.UiState
import com.tgyuu.common.ui.InputState
import com.tgyuu.domain.model.TodoSchedule

data class AddMemoState(
    val originSchedule: TodoSchedule? = null,
    val memo: String = "",
    val memoInputState: InputState = InputState.DEFAULT,
) : UiState {
    val isSaveEnabled = memo.isNotEmpty()
    val isInputFieldIncomplete: Boolean = memoInputState == InputState.WARNING
}
