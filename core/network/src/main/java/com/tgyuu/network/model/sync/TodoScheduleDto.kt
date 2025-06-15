package com.tgyuu.network.model.sync

import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp
import com.tgyuu.domain.model.sync.TodoScheduleForSync
import com.tgyuu.network.defaultDate
import com.tgyuu.network.toDate
import com.tgyuu.network.toLocalDate
import com.tgyuu.network.toLocalDateTime
import java.util.Date

data class TodoScheduleDto(
    val id: Int = -1,
    val infoId: Int = -1,
    val date: Date = defaultDate,
    val memo: String = "",
    val priority: Int = 0,
    @PropertyName("done") val isDone: Boolean = false,
    val createdAt: Date = defaultDate,
    @PropertyName("deleted") val isDeleted: Boolean = false,
    val updatedAt: Date = defaultDate,
    @ServerTimestamp var uploadedAt: Date? = null,
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
