package com.tgyuu.network.model.sync

import com.google.firebase.firestore.PropertyName
import com.tgyuu.domain.model.sync.TodoTagForSync
import com.tgyuu.network.toDate
import com.tgyuu.network.toLocalDate
import com.tgyuu.network.toLocalDateTime
import java.util.Date

data class TodoTagDto(
    val id: Int,
    val name: String,
    val color: Int,
    val createdAt: Date,
    @PropertyName("deleted") val isDeleted: Boolean,
    val updatedAt: Date,
) {
    fun toDomain() = TodoTagForSync(
        id = id,
        name = name,
        color = color,
        createdAt = createdAt.toLocalDate(),
        isDeleted = isDeleted,
        updatedAt = updatedAt.toLocalDateTime(),
    )
}

fun TodoTagForSync.toDto(): TodoTagDto = TodoTagDto(
    id = id,
    name = name,
    color = color,
    createdAt = createdAt.toDate(),
    isDeleted = isDeleted,
    updatedAt = updatedAt.toDate(),
)
