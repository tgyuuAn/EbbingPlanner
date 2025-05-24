package com.tgyuu.memo.contract

import com.tgyuu.common.base.UiState
import com.tgyuu.common.ui.InputState
import com.tgyuu.domain.model.TodoSchedule

data class MemoState(
    val originSchedule: TodoSchedule? = null,
    val memo: String = "",
    val memoInputState: InputState,
) : UiState {
    val isSaveEnabled = memo.isNotEmpty()
    val isInputFieldIncomplete: Boolean = memoInputState == InputState.WARNING
}
