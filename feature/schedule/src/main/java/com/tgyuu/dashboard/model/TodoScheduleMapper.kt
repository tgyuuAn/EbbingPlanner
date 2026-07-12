package com.tgyuu.dashboard.model

import com.tgyuu.designsystem.model.ClickableText
import com.tgyuu.designsystem.model.TodoScheduleUiModel
import com.tgyuu.domain.model.TodoSchedule
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

fun TodoSchedule.toUiModel(): TodoScheduleUiModel {
    return TodoScheduleUiModel(
        id = id,
        infoId = infoId,
        title = ClickableText.from(title),
        tagId = tagId,
        name = name,
        color = color,
        date = date,
        memo = ClickableText.from(memo),
        isPinned = isPinned,
        isDone = isDone,
        createdAt = createdAt,
        infoCreatedAt = infoCreatedAt,
    )
}

fun List<TodoSchedule>.toUiModels(): ImmutableList<TodoScheduleUiModel> {
    return map { it.toUiModel() }.toImmutableList()
}

fun TodoScheduleUiModel.toDomainModel(): TodoSchedule {
    return TodoSchedule(
        id = id,
        infoId = infoId,
        title = title.originalText,
        tagId = tagId,
        name = name,
        color = color,
        date = date,
        memo = memo.originalText,
        isPinned = isPinned,
        isDone = isDone,
        createdAt = createdAt,
        infoCreatedAt = infoCreatedAt,
    )
}
