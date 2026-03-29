package com.tgyuu.memo.graph.addmemo.contract

import com.tgyuu.common.base.UiState
import com.tgyuu.domain.model.TodoSchedule

data class AddMemoState(
    val originSchedule: TodoSchedule? = null,
    val memo: String = "",
    val showSaveDialog: Boolean = false,
    val relatedScheduleCount: Int = 0,
) : UiState {
    val isSaveEnabled = memo.isNotEmpty()
}
