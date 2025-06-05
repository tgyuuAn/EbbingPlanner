package com.tgyuu.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tgyuu.domain.model.RepeatCycle
import java.time.DayOfWeek

@Entity(tableName = "repeat_cycle")
data class RepeatCycleEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val displayName: String = "",
    val intervals: List<Int> = emptyList(),
    val restDays: List<DayOfWeek> = emptyList(),
) {
    fun toDomain() = RepeatCycle(
        id = id,
        displayName = displayName,
        intervals = intervals,
        restDays = restDays,
    )
}

fun RepeatCycle.toEntity() = RepeatCycleEntity(
    id = id,
    displayName = displayName,
    intervals = intervals,
    restDays = restDays,
)
