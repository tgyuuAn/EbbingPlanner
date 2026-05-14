package com.tgyuu.dashboard.contract

import androidx.compose.runtime.Immutable
import com.tgyuu.common.base.UiState
import com.tgyuu.designsystem.model.TodoInfoUiModel
import com.tgyuu.designsystem.model.TodoScheduleUiModel
import com.tgyuu.designsystem.model.TodoTagUiModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toImmutableList

@Immutable
data class ScheduleState(
    val tags: ImmutableList<TodoTagUiModel> = persistentListOf(),
    val infosByTagMap: ImmutableMap<Int, ImmutableList<TodoInfoUiModel>> = persistentMapOf(),
    val schedulesByInfoMap: ImmutableMap<Int, ImmutableList<TodoScheduleUiModel>> = persistentMapOf(),
    val expandedTagIds: ImmutableSet<Int> = persistentSetOf(),
    val expandedInfoIds: ImmutableSet<Int> = persistentSetOf(),
) : UiState {
    // Info 레벨
    val infoScheduleCountMap: Map<Int, Int>
        get() = schedulesByInfoMap.mapValues { it.value.size }

    val infoAchievementRateMap: Map<Int, Float>
        get() = schedulesByInfoMap.mapValues { (_, schedules) ->
            if (schedules.isEmpty()) 0f
            else schedules.count { it.isDone }.toFloat() / schedules.size
        }

    val infoAllDoneMap: Map<Int, Boolean>
        get() = schedulesByInfoMap.mapValues { (_, schedules) ->
            schedules.isNotEmpty() && schedules.all { it.isDone }
        }

    // Tag 레벨
    val tagScheduleCountMap: Map<Int, Int>
        get() = tags.associate { tag ->
            val infos = infosByTagMap[tag.id].orEmpty()
            tag.id to infos.sumOf { info -> schedulesByInfoMap[info.id]?.size ?: 0 }
        }

    val tagAchievementRateMap: Map<Int, Float>
        get() = tags.associate { tag ->
            val infos = infosByTagMap[tag.id].orEmpty()
            val allSchedules = infos.flatMap { schedulesByInfoMap[it.id].orEmpty() }
            val rate = if (allSchedules.isEmpty()) 0f
            else allSchedules.count { it.isDone }.toFloat() / allSchedules.size
            tag.id to rate
        }

    val tagAllDoneMap: Map<Int, Boolean>
        get() = tags.associate { tag ->
            val infos = infosByTagMap[tag.id].orEmpty()
            val allSchedules = infos.flatMap { schedulesByInfoMap[it.id].orEmpty() }
            tag.id to (allSchedules.isNotEmpty() && allSchedules.all { it.isDone })
        }

    val visibleTags: ImmutableList<TodoTagUiModel>
        get() = tags.filter { (tagScheduleCountMap[it.id] ?: 0) > 0 }.toImmutableList()
}
