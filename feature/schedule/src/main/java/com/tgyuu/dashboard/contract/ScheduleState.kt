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

@Immutable
data class ScheduleState(
    val tags: ImmutableList<TodoTagUiModel> = persistentListOf(),
    val infosByTagMap: ImmutableMap<Int, ImmutableList<TodoInfoUiModel>> = persistentMapOf(),
    val schedulesByInfoMap: ImmutableMap<Int, ImmutableList<TodoScheduleUiModel>> = persistentMapOf(),
    val expandedTagIds: ImmutableSet<Int> = persistentSetOf(),
    val expandedInfoIds: ImmutableSet<Int> = persistentSetOf(),
    val infoScheduleCountMap: ImmutableMap<Int, Int> = persistentMapOf(),
    val infoAchievementRateMap: ImmutableMap<Int, Float> = persistentMapOf(),
    val infoAllDoneMap: ImmutableMap<Int, Boolean> = persistentMapOf(),
    val tagScheduleCountMap: ImmutableMap<Int, Int> = persistentMapOf(),
    val tagAchievementRateMap: ImmutableMap<Int, Float> = persistentMapOf(),
    val tagAllDoneMap: ImmutableMap<Int, Boolean> = persistentMapOf(),
    val visibleTags: ImmutableList<TodoTagUiModel> = persistentListOf(),
    val pendingDeleteTag: Pair<Int, String>? = null,
) : UiState
