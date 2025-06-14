package com.tgyuu.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tgyuu.domain.model.TodoTag
import java.time.LocalDate

@Entity(tableName = "todo_tag")
data class TodoTagEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val color: Int,
    val createdAt: LocalDate = LocalDate.now(),
    val isDeleted: Boolean = false,
    val isSynced: Boolean = false,
) {
    fun toDomain() = TodoTag(
        id = id,
        name = name,
        color = color,
        createdAt = createdAt,
    )
}

fun TodoTag.toEntity() = TodoTagEntity(
    id = this.id,
    name = this.name,
    color = this.color,
    createdAt = this.createdAt,
)
