package com.tgyuu.home.model

import com.tgyuu.designsystem.model.TodoTagUiModel
import com.tgyuu.domain.model.TodoTag
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

fun TodoTag.toUiModel(): TodoTagUiModel {
    return TodoTagUiModel(
        id = id,
        name = name,
        color = color,
        createdAt = createdAt,
    )
}

fun List<TodoTag>.toUiModels(): ImmutableList<TodoTagUiModel> {
    return map { it.toUiModel() }.toImmutableList()
}

fun TodoTagUiModel.toDomainModel(): TodoTag {
    return TodoTag(
        id = id,
        name = name,
        color = color,
        createdAt = createdAt,
    )
}
