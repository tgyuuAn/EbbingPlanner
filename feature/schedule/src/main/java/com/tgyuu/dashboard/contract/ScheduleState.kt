package com.tgyuu.dashboard.contract

import androidx.compose.runtime.Immutable
import com.tgyuu.common.base.UiState
import com.tgyuu.designsystem.model.TodoInfoUiModel
import com.tgyuu.designsystem.model.TodoScheduleUiModel
import com.tgyuu.designsystem.model.TodoTagUiModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf

@Immutable
data class ScheduleState(
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
