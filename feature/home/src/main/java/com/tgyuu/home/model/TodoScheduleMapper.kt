package com.tgyuu.home.model

import com.tgyuu.designsystem.model.ClickableText
import com.tgyuu.designsystem.model.TodoScheduleUiModel
import com.tgyuu.domain.model.TodoSchedule

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
        priority = priority,
        isDone = isDone,
        createdAt = createdAt,
        infoCreatedAt = infoCreatedAt,
    )
}

fun List<TodoSchedule>.toUiModels(): List<TodoScheduleUiModel> {
    return map { it.toUiModel() }
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
        priority = priority,
        isDone = isDone,
        createdAt = createdAt,
        infoCreatedAt = infoCreatedAt,
    )
}
