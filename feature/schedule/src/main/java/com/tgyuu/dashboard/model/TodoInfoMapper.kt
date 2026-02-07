package com.tgyuu.dashboard.model

import com.tgyuu.designsystem.model.TodoInfoUiModel
import com.tgyuu.domain.model.TodoInfo
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

fun TodoInfo.toUiModel(): TodoInfoUiModel {
    return TodoInfoUiModel(
        id = id,
        title = title,
        tagId = tagId,
    )
}

fun List<TodoInfo>.toUiModels(): ImmutableList<TodoInfoUiModel> {
    return map { it.toUiModel() }.toImmutableList()
}

fun TodoInfoUiModel.toDomainModel(): TodoInfo {
    return TodoInfo(
        id = id,
        title = title,
        tagId = tagId,
    )
}
