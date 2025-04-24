package com.tgyuu.database.model.tag

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tgyuu.domain.model.TodoTag
import java.time.LocalDate

@Entity(tableName = "todo_tag")
data class TodoTagEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val color: Int,
    val startDate: LocalDate = LocalDate.now(),
    val createdAt: LocalDate = LocalDate.now(),
    val updatedAt: LocalDate = LocalDate.now(),
) {
    fun toDomain() = TodoTag(
        id = id,
        name = name,
        color = color,
        startDate = startDate,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
}

fun TodoTag.toEntity() = TodoTagEntity(
    id = this.id,
    name = this.name,
    color = this.color,
    startDate = this.startDate,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt,
)
