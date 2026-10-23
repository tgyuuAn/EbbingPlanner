package com.tgyuu.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.tgyuu.common.now
import com.tgyuu.domain.model.sync.TodoInfoForSync
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

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
    val createdAt: LocalDate = LocalDate.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    @ColumnInfo(name = "rest_days") val restDays: String = "",
)

fun TodoInfoForSync.toEntity() = TodoInfoEntity(
    id = this.id,
    title = this.title,
    tagId = this.tagId,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt,
    restDays = this.restDays,
)
