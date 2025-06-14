package com.tgyuu.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tgyuu.domain.model.RepeatCycle

@Entity(tableName = "repeat_cycle")
data class RepeatCycleEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val intervals: List<Int> = emptyList(),
    val isDeleted: Boolean = false,
    val isSynced: Boolean = false,
) {
    fun toDomain() = RepeatCycle(
        id = id,
        intervals = intervals,
    )
}

fun RepeatCycle.toEntity() = RepeatCycleEntity(
    id = id,
    intervals = intervals,
)
