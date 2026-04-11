package com.tgyuu.home.graph.main.contract

import androidx.compose.runtime.Immutable
import com.tgyuu.common.base.UiState
import com.tgyuu.designsystem.model.TodoScheduleUiModel
import com.tgyuu.domain.model.SortType
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentMapOf
import java.time.LocalDate

@Immutable
data class HomeState(
    val isLoading: Boolean = true,
    val schedulesByDateMap: ImmutableMap<LocalDate, ImmutableList<TodoScheduleUiModel>> = persistentMapOf(),
    val schedulesByTodoInfo: ImmutableMap<Int, ImmutableList<TodoScheduleUiModel>> = persistentMapOf(),
    val sortType: SortType = SortType.CREATED,
    val showWidgetNudgeDialog: Boolean = false,
    val mondayStart: Boolean = false,
) : UiState
