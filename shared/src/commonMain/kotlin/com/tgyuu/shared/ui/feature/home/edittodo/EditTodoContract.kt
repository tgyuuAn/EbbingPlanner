package com.tgyuu.shared.ui.feature.home.edittodo

import androidx.compose.runtime.Immutable
import com.tgyuu.shared.base.UiIntent
import com.tgyuu.shared.base.UiState
import com.tgyuu.shared.common.now
import com.tgyuu.shared.domain.model.TodoSchedule
import com.tgyuu.shared.ui.model.TodoScheduleUiModel
import com.tgyuu.shared.ui.model.TodoTagUiModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate

@Immutable
data class EditTodoState(
    val schedulesByDateMap: ImmutableMap<LocalDate, ImmutableList<TodoScheduleUiModel>> = persistentMapOf(),
    val originSchedule: TodoSchedule? = null,
    val selectedDate: LocalDate = LocalDate.now(),
    val title: String = "",
    val isPinned: Boolean = false,
    val tag: TodoTagUiModel? = null,
    val tagList: ImmutableList<TodoTagUiModel> = persistentListOf(),
    val restDays: ImmutableSet<DayOfWeek> = persistentSetOf(),
    val mondayStart: Boolean = false,
    val isLoading: Boolean = false,
) : UiState {
    val isSaveEnabled: Boolean = title.isNotEmpty()
}

sealed class EditTodoIntent : UiIntent {
    data object OnBackClick : EditTodoIntent()
    data class OnSelectedDateChange(val selectedDate: LocalDate) : EditTodoIntent()
    data class OnTitleChange(val title: String) : EditTodoIntent()
    data class OnPinnedChange(val isPinned: Boolean) : EditTodoIntent()
    data class OnTagChange(val tag: TodoTagUiModel) : EditTodoIntent()
    data object OnAddTagClick : EditTodoIntent()
    data object OnSaveClick : EditTodoIntent()
}
