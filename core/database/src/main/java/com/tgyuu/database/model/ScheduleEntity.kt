package com.tgyuu.database.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.tgyuu.domain.model.TodoSchedule
import com.tgyuu.domain.model.sync.TodoScheduleForSync
import java.time.LocalDate
import java.time.LocalDateTime

@Entity(
    tableName = "schedule",
    foreignKeys = [
        ForeignKey(
            entity = TodoInfoEntity::class,
            parentColumns = ["id"],
            childColumns = ["infoId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["infoId", "date"])],
)
data class ScheduleEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val infoId: Int,
    val date: LocalDate,
    val memo: String,
    val priority: Int,
    val isDone: Boolean = false,
    val createdAt: LocalDate = LocalDate.now(),
    val isDeleted: Boolean = false,
    val updatedAt: LocalDateTime = LocalDateTime.now(),
)

fun TodoSchedule.toEntity() = ScheduleEntity(
    id = this.id,
    infoId = this.infoId,
    date = this.date,
    memo = this.memo,
    priority = this.priority,
    isDone = this.isDone,
    createdAt = this.createdAt,
)

fun ScheduleEntity.toSyncModel(): TodoScheduleForSync = TodoScheduleForSync(
    id = this.id,
    infoId = this.infoId,
    date = this.date,
    memo = this.memo,
    priority = this.priority,
    isDone = this.isDone,
    createdAt = this.createdAt,
    isDeleted = this.isDeleted,
    updatedAt = this.updatedAt,
)
