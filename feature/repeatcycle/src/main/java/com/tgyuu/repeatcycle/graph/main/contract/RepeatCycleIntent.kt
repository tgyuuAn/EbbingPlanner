package com.tgyuu.repeatcycle.graph.main.contract

import com.tgyuu.common.base.UiIntent
import com.tgyuu.domain.model.RepeatCycle

sealed class RepeatCycleIntent : UiIntent {
    data object OnBackClick : RepeatCycleIntent()
    data class OnEditClick(val repeatCycle: RepeatCycle) : RepeatCycleIntent()
    data class OnDeleteClick(val repeatCycle: RepeatCycle) : RepeatCycleIntent()
}
