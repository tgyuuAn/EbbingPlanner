package com.tgyuu.network.model.sync

import com.google.firebase.firestore.PropertyName
import com.tgyuu.domain.model.sync.RepeatCycleForSync
import com.tgyuu.network.toDate
import com.tgyuu.network.toLocalDateTime
import java.util.Date

data class RepeatCycleDto(
    val id: Int,
    val intervals: List<Int>,
    @PropertyName("deleted") val isDeleted: Boolean,
    val updatedAt: Date,
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
