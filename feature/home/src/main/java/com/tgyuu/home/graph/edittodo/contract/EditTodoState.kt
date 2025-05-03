package com.tgyuu.home.graph.edittodo.contract

import com.tgyuu.common.base.UiState
import com.tgyuu.domain.model.DefaultTodoTag
import com.tgyuu.domain.model.TodoSchedule
import com.tgyuu.domain.model.TodoTag
import com.tgyuu.common.ui.InputState
import java.time.LocalDate

data class EditTodoState(
    val schedulesByDateMap: Map<LocalDate, List<TodoSchedule>> = emptyMap(),
    val originSchedule: TodoSchedule? = null,
    val selectedDate: LocalDate = LocalDate.now(),
    val title: String = "",
    val titleInputState: InputState = InputState.DEFAULT,
    val priority: String? = null,
    val tag: TodoTag = DefaultTodoTag,
    val tagList: List<TodoTag> = emptyList(),
) : UiState {
    val isSaveEnabled = title.isNotEmpty()
    val isInputFieldIncomplete: Boolean = titleInputState == InputState.WARNING
}
