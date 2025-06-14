package com.tgyuu.network.model

import com.tgyuu.domain.model.TodoSchedule
import com.tgyuu.network.toDate
import java.util.Date
import java.time.LocalDate
import java.time.ZoneId

data class TodoScheduleRequestDto(
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
)

fun TodoSchedule.toRequestDto(): TodoScheduleRequestDto = TodoScheduleRequestDto(
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
