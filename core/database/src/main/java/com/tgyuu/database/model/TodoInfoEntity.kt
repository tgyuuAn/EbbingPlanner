package com.tgyuu.database.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.tgyuu.domain.model.TodoSchedule
import java.time.LocalDate

@Entity(
    tableName = "todo_info",
    foreignKeys = [
        ForeignKey(
            entity = TodoTagEntity::class,
            parentColumns = ["id"],
            childColumns = ["tagId"],
        )
    ],
    indices = [Index(value = ["tagId"])]
)
data class TodoInfoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val tagId: Int,
    val priority: Int,
    val createdAt: LocalDate = LocalDate.now(),
    val isDeleted: Boolean = false,
    val isSynced: Boolean = false,
)

fun TodoSchedule.toInfoEntity() = TodoInfoEntity(
    id = this.infoId,
    title = this.title,
    tagId = this.tagId,
    priority = this.priority,
    createdAt = this.infoCreatedAt,
)
