package com.tgyuu.home.graph.edittodo.contract

import com.tgyuu.common.base.UiIntent
import com.tgyuu.common.event.BottomSheetContent
import com.tgyuu.designsystem.model.TodoTagUiModel
import java.time.LocalDate

sealed class EditTodoIntent : UiIntent {
    data object OnBackClick : EditTodoIntent()
    data class OnSelectedDataChangeClick(val content: BottomSheetContent) : EditTodoIntent()
    data class OnSelectedDateChange(val selectedDate: LocalDate) : EditTodoIntent()
    data class OnTitleChange(val title: String) : EditTodoIntent()
    data class OnPriorityChange(val priority: String) : EditTodoIntent()
    data class OnTagDropDownClick(val content: BottomSheetContent) : EditTodoIntent()
    data class OnTagChange(val tag: TodoTagUiModel) : EditTodoIntent()
    data object OnAddTagClick : EditTodoIntent()
    data object OnSaveClick : EditTodoIntent()
}
