package com.tgyuu.network.model.sync

import com.google.firebase.firestore.PropertyName
import com.tgyuu.domain.model.sync.TodoScheduleForSync
import com.tgyuu.network.toDate
import com.tgyuu.network.toLocalDate
import com.tgyuu.network.toLocalDateTime
import java.util.Date

data class TodoScheduleDto(
    val id: Int,
    val infoId: Int,
    val date: Date,
    val memo: String,
    val priority: Int,
    @PropertyName("done") val isDone: Boolean,
    val createdAt: Date,
    @PropertyName("deleted") val isDeleted: Boolean,
    val updatedAt: Date,
) {
    fun toDomain(): TodoScheduleForSync = TodoScheduleForSync(
        id = id,
        infoId = infoId,
        date = date.toLocalDate(),
        memo = memo,
        priority = priority,
        isDone = isDone,
        createdAt = createdAt.toLocalDate(),
        isDeleted = isDeleted,
        updatedAt = updatedAt.toLocalDateTime(),
    )
}

fun TodoScheduleForSync.toDto(): TodoScheduleDto = TodoScheduleDto(
    id = id,
    infoId = infoId,
    date = date.toDate(),
    memo = memo,
    priority = priority,
    isDone = isDone,
    createdAt = createdAt.toDate(),
    isDeleted = isDeleted,
    updatedAt = updatedAt.toDate(),
)
