package com.tgyuu.repeatcycle.graph.main.contract

import androidx.compose.runtime.Immutable
import com.tgyuu.common.base.UiState
import com.tgyuu.designsystem.model.RepeatCycleUiModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class RepeatCycleState(
    val repeatCycleList: ImmutableList<RepeatCycleUiModel> = persistentListOf(),
) : UiState
