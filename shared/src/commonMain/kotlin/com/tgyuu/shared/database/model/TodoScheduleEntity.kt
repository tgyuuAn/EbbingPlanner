package com.tgyuu.shared.database.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.tgyuu.shared.common.now
import com.tgyuu.shared.domain.model.TodoSchedule
import com.tgyuu.shared.domain.model.sync.TodoScheduleForSync
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

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
data class TodoScheduleEntity(
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

fun TodoSchedule.toEntity() = TodoScheduleEntity(
    id = this.id,
    infoId = this.infoId,
    date = this.date,
    memo = this.memo,
    priority = this.priority,
    isDone = this.isDone,
    createdAt = this.createdAt,
)

fun TodoScheduleForSync.toEntity(): TodoScheduleEntity = TodoScheduleEntity(
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
