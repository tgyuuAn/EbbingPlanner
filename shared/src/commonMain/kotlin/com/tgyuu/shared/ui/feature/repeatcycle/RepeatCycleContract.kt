package com.tgyuu.shared.ui.feature.repeatcycle

import androidx.compose.runtime.Immutable
import com.tgyuu.shared.base.UiIntent
import com.tgyuu.shared.base.UiState
import com.tgyuu.shared.ui.model.RepeatCycleUiModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class RepeatCycleState(
    val repeatCycleList: ImmutableList<RepeatCycleUiModel> = persistentListOf(),
    val isLoading: Boolean = true,
) : UiState

sealed class RepeatCycleIntent : UiIntent {
    data object OnBackClick : RepeatCycleIntent()
    data object OnAddClick : RepeatCycleIntent()
    data class OnEditClick(val repeatCycle: RepeatCycleUiModel) : RepeatCycleIntent()
    data class OnDeleteClick(val repeatCycle: RepeatCycleUiModel) : RepeatCycleIntent()
}
