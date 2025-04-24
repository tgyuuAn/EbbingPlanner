package com.tgyuu.database.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

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
    val priority: Int = 0,
    val isDone: Boolean = false,
    val createdAt: LocalDate = LocalDate.now(),
)
