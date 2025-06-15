package com.tgyuu.network.model.sync

import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp
import com.tgyuu.domain.model.sync.RepeatCycleForSync
import com.tgyuu.network.toDate
import com.tgyuu.network.toLocalDateTime
import java.util.Date

data class RepeatCycleDto(
    val id: Int,
    val intervals: List<Int>,
    @PropertyName("deleted") val isDeleted: Boolean,
    val updatedAt: Date,
    @ServerTimestamp var uploadedAt: Date? = null,
) {
    fun toDomain() = RepeatCycleForSync(
        id = id,
        intervals = intervals,
        isDeleted = isDeleted,
        updatedAt = updatedAt.toLocalDateTime(),
    )
}

fun RepeatCycleForSync.toDto() = RepeatCycleDto(
    id = id,
    intervals = intervals,
    isDeleted = isDeleted,
    updatedAt = updatedAt.toDate(),
)
