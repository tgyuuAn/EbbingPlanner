package com.tgyuu.shared.ui.feature.schedule

import androidx.compose.runtime.Immutable
import com.tgyuu.shared.base.UiIntent
import com.tgyuu.shared.base.UiState
import com.tgyuu.shared.ui.model.TodoInfoUiModel
import com.tgyuu.shared.ui.model.TodoScheduleUiModel
import com.tgyuu.shared.ui.model.TodoTagUiModel
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

sealed class ScheduleIntent : UiIntent {
    data class OnToggleTagExpand(val tagId: Int) : ScheduleIntent()
    data class OnToggleInfoExpand(val infoId: Int) : ScheduleIntent()
    data class OnScheduleClick(val schedule: TodoScheduleUiModel) : ScheduleIntent()
    data object OnNavigateToAddTodo : ScheduleIntent()
    data class OnSaveTag(val tagId: Int, val name: String, val color: Int) : ScheduleIntent()
    data class OnDeleteTag(val tagId: Int) : ScheduleIntent()
    data class OnRequestDeleteTag(val tagId: Int, val tagName: String) : ScheduleIntent()
    data object OnClearPendingDeleteTag : ScheduleIntent()

    // 수정하기
    data class OnUpdateInfoClick(val schedule: TodoScheduleUiModel) : ScheduleIntent()
    data class OnUpdateDateClick(val schedule: TodoScheduleUiModel) : ScheduleIntent()

    // 삭제하기
    data class OnDeleteSingleClick(val schedule: TodoScheduleUiModel) : ScheduleIntent()
    data class OnDeleteRemainingClick(val schedule: TodoScheduleUiModel) : ScheduleIntent()

    // 미루기
    data class OnDelaySingleClick(val schedule: TodoScheduleUiModel) : ScheduleIntent()
    data class OnDelayAllClick(val schedule: TodoScheduleUiModel) : ScheduleIntent()

    // 메모
    data class OnMemoClick(val schedule: TodoScheduleUiModel) : ScheduleIntent()
    data class OnDeleteMemoClick(val schedule: TodoScheduleUiModel) : ScheduleIntent()
}
