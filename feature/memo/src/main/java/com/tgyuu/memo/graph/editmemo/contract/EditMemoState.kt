package com.tgyuu.memo.graph.editmemo.contract

import com.tgyuu.common.base.UiState
import com.tgyuu.domain.model.TodoSchedule

data class EditMemoState(
    val originSchedule: TodoSchedule? = null,
    val memo: String = "",
) : UiState {
    val isSaveEnabled = memo.isNotEmpty()
}
