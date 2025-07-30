package com.tgyuu.dashboard.contract

import com.tgyuu.common.base.UiState
import com.tgyuu.domain.model.TodoInfo
import com.tgyuu.domain.model.TodoSchedule
import com.tgyuu.domain.model.TodoTag

data class ScheduleState(
    val tags: List<TodoTag> = emptyList(),
    val selectedTag: TodoTag? = null,
    val todoInfoMap: Map<Int, List<TodoInfo>> = emptyMap(),
    val selectedTodoInfo: TodoInfo? = null,
    val todoScheduleMap: Map<Int, List<TodoSchedule>> = emptyMap(),
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

    val todoInfos: List<TodoInfo>
        get() = selectedTag?.let { todoInfoMap[it.id] } ?: emptyList()

    val todoSchedules: List<TodoSchedule>
        get() = selectedTodoInfo?.let { todoScheduleMap[it.id] } ?: emptyList()
}
