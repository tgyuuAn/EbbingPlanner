package com.tgyuu.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tgyuu.domain.model.RepeatCycle
import com.tgyuu.domain.model.sync.RepeatCycleForSync
import java.time.LocalDateTime

@Entity(tableName = "repeat_cycle")
data class RepeatCycleEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val intervals: List<Int> = emptyList(),
    val isDeleted: Boolean = false,
    val updatedAt: LocalDateTime = LocalDateTime.now(),
) {
    fun toDomain() = RepeatCycle(
        id = this.id,
        intervals = this.intervals,
    )
}

fun RepeatCycle.toEntity() = RepeatCycleEntity(
    id = this.id,
    intervals = this.intervals,
)

fun RepeatCycleEntity.toSyncModel() = RepeatCycleForSync(
    id = this.id,
    intervals = this.intervals,
    isDeleted = isDeleted,
    updatedAt = this.updatedAt,
)
