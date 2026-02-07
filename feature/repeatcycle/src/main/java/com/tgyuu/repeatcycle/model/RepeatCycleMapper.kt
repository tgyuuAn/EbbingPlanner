package com.tgyuu.repeatcycle.model

import com.tgyuu.designsystem.model.RepeatCycleUiModel
import com.tgyuu.domain.model.RepeatCycle
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

fun RepeatCycle.toUiModel(): RepeatCycleUiModel {
    return RepeatCycleUiModel(
        id = id,
        intervals = intervals.toImmutableList(),
        displayName = toDisplayName(),
    )
}

fun List<RepeatCycle>.toUiModels(): ImmutableList<RepeatCycleUiModel> {
    return map { it.toUiModel() }.toImmutableList()
}

fun RepeatCycleUiModel.toDomainModel(): RepeatCycle {
    return RepeatCycle(
        id = id,
        intervals = intervals.toList(),
    )
}
