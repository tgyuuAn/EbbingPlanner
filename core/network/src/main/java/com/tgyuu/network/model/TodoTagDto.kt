package com.tgyuu.network.model

import com.tgyuu.domain.model.TodoTag
import com.tgyuu.network.toDate
import com.tgyuu.network.toLocalDate
import java.util.Date

data class TodoTagDto(
    val id: Int,
    val name: String,
    val color: Int,
    val createdAt: Date,
) {
    fun toDomain() = TodoTag(
        id = id,
        name = name,
        color = color,
        createdAt = createdAt.toLocalDate(),
    )
}

fun TodoTag.toDto(): TodoTagDto = TodoTagDto(
    id = id,
    name = name,
    color = color,
    createdAt = createdAt.toDate()
)
