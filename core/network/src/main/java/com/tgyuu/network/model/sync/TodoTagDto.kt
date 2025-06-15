package com.tgyuu.network.model.sync

import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp
import com.tgyuu.domain.model.sync.TodoTagForSync
import com.tgyuu.network.defaultDate
import com.tgyuu.network.toDate
import com.tgyuu.network.toLocalDate
import com.tgyuu.network.toLocalDateTime
import java.util.Date

data class TodoTagDto(
    val id: Int = -1,
    val name: String = "",
    val color: Int = -1,
    val createdAt: Date = defaultDate,
    @PropertyName("deleted") val isDeleted: Boolean = false,
    val updatedAt: Date = defaultDate,
    @ServerTimestamp var uploadedAt: Date? = null,
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
