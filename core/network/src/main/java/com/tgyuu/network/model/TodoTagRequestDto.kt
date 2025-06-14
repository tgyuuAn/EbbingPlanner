package com.tgyuu.network.model

import com.tgyuu.domain.model.TodoTag
import com.tgyuu.network.toDate
import java.util.Date

data class TodoTagRequestDto(
    val id: Int,
    val name: String,
    val color: Int,
    val createdAt: Date,
)

fun TodoTag.toRequestDto(): TodoTagRequestDto = TodoTagRequestDto(
    id = id,
    name = name,
    color = color,
    createdAt = createdAt.toDate()
)
