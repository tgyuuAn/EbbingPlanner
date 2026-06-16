package com.tgyuu.home.model

import com.tgyuu.common.ui.resource.ResourceProvider
import com.tgyuu.designsystem.model.RepeatCycleUiModel
import com.tgyuu.designsystem.model.toDisplayName
import com.tgyuu.domain.model.RepeatCycle
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

fun RepeatCycle.toUiModel(resourceProvider: ResourceProvider): RepeatCycleUiModel {
    return RepeatCycleUiModel(
        id = id,
        intervals = intervals.toImmutableList(),
        displayName = toDisplayName(resourceProvider),
    )
}

fun List<RepeatCycle>.toUiModels(resourceProvider: ResourceProvider): ImmutableList<RepeatCycleUiModel> {
    return map { it.toUiModel(resourceProvider) }.toImmutableList()
}

fun RepeatCycleUiModel.toDomainModel(): RepeatCycle {
    return RepeatCycle(
        id = id,
        intervals = intervals.toList(),
    )
}
