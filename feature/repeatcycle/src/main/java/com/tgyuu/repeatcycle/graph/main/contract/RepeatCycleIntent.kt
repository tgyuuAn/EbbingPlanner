package com.tgyuu.repeatcycle.graph.main.contract

import com.tgyuu.common.base.UiIntent
import com.tgyuu.designsystem.model.RepeatCycleUiModel

sealed class RepeatCycleIntent : UiIntent {
    data object OnBackClick : RepeatCycleIntent()
    data class OnEditClick(val repeatCycle: RepeatCycleUiModel) : RepeatCycleIntent()
    data class OnDeleteClick(val repeatCycle: RepeatCycleUiModel) : RepeatCycleIntent()
}
