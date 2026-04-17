package com.tgyuu.shared.ui.feature.home

import androidx.compose.runtime.Immutable
import com.tgyuu.shared.base.UiIntent
import com.tgyuu.shared.base.UiState
import com.tgyuu.shared.domain.model.SortType
import com.tgyuu.shared.ui.model.TodoScheduleUiModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.datetime.LocalDate

@Immutable
data class HomeState(
    val isLoading: Boolean = true,
    val currentDate: LocalDate? = null,
    val selectedDate: LocalDate? = null,
    val schedulesByDateMap: ImmutableMap<LocalDate, ImmutableList<TodoScheduleUiModel>> = persistentMapOf(),
    val schedulesByTodoInfo: ImmutableMap<Int, ImmutableList<TodoScheduleUiModel>> = persistentMapOf(),
    val sortType: SortType = SortType.CREATED,
    val mondayStart: Boolean = false,
    val showWidgetNudgeDialog: Boolean = false,
    val showInAppReviewDialog: Boolean = false,
) : UiState {

    val todaySchedules: ImmutableList<TodoScheduleUiModel>
        get() = selectedDate?.let { schedulesByDateMap[it] } ?: persistentListOf()

    val datesWithSchedules: Set<LocalDate>
        get() = schedulesByDateMap.keys
}

sealed class HomeIntent : UiIntent {
    // Navigation
    data class OnDateSelected(val date: LocalDate) : HomeIntent()
    data class OnCurrentDateChanged(val currentDate: LocalDate) : HomeIntent()
    data class OnAddTodoClick(val selectedDate: LocalDate) : HomeIntent()
    data object OnSettingClick : HomeIntent()
    data object OnScheduleClick : HomeIntent()
    data object OnSyncClick : HomeIntent()

    // Schedule actions
    data class OnCheckChanged(val schedule: TodoScheduleUiModel) : HomeIntent()
    data class OnEditScheduleClick(val schedule: TodoScheduleUiModel) : HomeIntent()

    // Sort
    data object OnSortTypeClick : HomeIntent()
    data class OnUpdateSortType(val sortType: SortType) : HomeIntent()

    // Delete
    data class OnDeleteSingleClick(val schedule: TodoScheduleUiModel) : HomeIntent()
    data class OnDeleteRemainingClick(val schedule: TodoScheduleUiModel) : HomeIntent()
    data class OnDeleteMemoClick(val schedule: TodoScheduleUiModel) : HomeIntent()

    // Update
    data class OnUpdateInfoClick(val schedule: TodoScheduleUiModel) : HomeIntent()
    data class OnUpdateDateClick(val schedule: TodoScheduleUiModel) : HomeIntent()

    // Delay
    data class OnDelaySingleClick(
        val schedule: TodoScheduleUiModel,
        val includeRestDays: Boolean = false,
    ) : HomeIntent()
    data class OnDelayAllClick(
        val schedule: TodoScheduleUiModel,
        val includeRestDays: Boolean = false,
    ) : HomeIntent()

    // Memo
    data class OnMemoClick(val schedule: TodoScheduleUiModel) : HomeIntent()

    // Widget
    data object OnWidgetNudgeDismiss : HomeIntent()
}
