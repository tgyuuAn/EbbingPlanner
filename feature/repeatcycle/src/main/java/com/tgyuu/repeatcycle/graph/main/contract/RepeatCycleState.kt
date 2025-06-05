package com.tgyuu.repeatcycle.graph.main.contract

import com.tgyuu.common.base.UiState
import com.tgyuu.domain.model.RepeatCycle

data class RepeatCycleState(
    val repeatCycleList: List<RepeatCycle> = emptyList(),
) : UiState
