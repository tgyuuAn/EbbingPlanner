package com.tgyuu.shared.ui.feature.schedule

import androidx.compose.runtime.Immutable
import com.tgyuu.shared.base.UiIntent
import com.tgyuu.shared.base.UiState
import com.tgyuu.shared.ui.model.TodoInfoUiModel
import com.tgyuu.shared.ui.model.TodoScheduleUiModel
import com.tgyuu.shared.ui.model.TodoTagUiModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf

@Immutable
data class ScheduleState(
    val isLoading: Boolean = true,
    val tags: ImmutableList<TodoTagUiModel> = persistentListOf(),
    val selectedTag: TodoTagUiModel? = null,
    val todoInfoMap: ImmutableMap<Int, ImmutableList<TodoInfoUiModel>> = persistentMapOf(),
    val selectedTodoInfo: TodoInfoUiModel? = null,
    val todoScheduleMap: ImmutableMap<Int, ImmutableList<TodoScheduleUiModel>> = persistentMapOf(),
) : UiState {

    val todoInfoAchievementRateMap: Map<Int, Float>
        get() = todoInfoMap.values.flatten().associate { info ->
            val schedules = todoScheduleMap[info.id].orEmpty()
            val doneCount = schedules.count { it.isDone }
            val totalCount = schedules.size
            info.id to if (totalCount > 0) doneCount.toFloat() / totalCount else 0f
        }

    val tagAchievementRateMap: Map<Int, Float>
        get() = tags.associate { tag ->
            val infos = todoInfoMap[tag.id].orEmpty()
            val infoRates = infos.mapNotNull { todoInfoAchievementRateMap[it.id] }
            val avgRate = if (infoRates.isNotEmpty()) infoRates.sum() / infoRates.size else 0f
            tag.id to avgRate
        }

    val todoInfos: ImmutableList<TodoInfoUiModel>
        get() = selectedTag?.let { todoInfoMap[it.id] } ?: persistentListOf()

    val todoSchedules: ImmutableList<TodoScheduleUiModel>
        get() = selectedTodoInfo?.let { todoScheduleMap[it.id] } ?: persistentListOf()
}

sealed class ScheduleIntent : UiIntent {
    data object OnBackClick : ScheduleIntent()
    data class OnTagClick(val tag: TodoTagUiModel) : ScheduleIntent()
    data class OnInfoClick(val todoInfo: TodoInfoUiModel) : ScheduleIntent()
    data class OnScheduleClick(val schedule: TodoScheduleUiModel) : ScheduleIntent()
}
