package com.tgyuu.network.model

import com.tgyuu.domain.model.TodoSchedule
import com.tgyuu.network.toDate
import com.tgyuu.network.toLocalDate
import java.util.Date

data class TodoScheduleDto(
    val id: Int,
    val infoId: Int,
    val title: String,
    val tagId: Int,
    val name: String,
    val color: Int,
    val date: Date,
    val memo: String,
    val priority: Int,
    val isDone: Boolean,
    val createdAt: Date,
    val infoCreatedAt: Date
) {
    fun toDomain(): TodoSchedule = TodoSchedule(
        id = id,
        infoId = infoId,
        date = date.toLocalDate(),
        memo = memo,
        priority = priority,
        isDone = isDone,
        createdAt = createdAt.toLocalDate(),
        title = title,
        tagId = tagId,
        name = name,
        color = color,
        infoCreatedAt = infoCreatedAt.toLocalDate(),
    )
}

fun TodoSchedule.toDto(): TodoScheduleDto = TodoScheduleDto(
    id = id,
    infoId = infoId,
    title = title,
    tagId = tagId,
    name = name,
    color = color,
    date = date.toDate(),
    memo = memo,
    priority = priority,
    isDone = isDone,
    createdAt = createdAt.toDate(),
    infoCreatedAt = infoCreatedAt.toDate()
)
