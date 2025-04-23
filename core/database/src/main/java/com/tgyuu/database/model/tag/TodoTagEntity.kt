package com.tgyuu.database.model.tag

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tgyuu.domain.TodoTag
import java.time.LocalDate

@Entity(tableName = "todo_tag")
data class TodoTagEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val color: Int,
    val startDate: LocalDate,
    val createdAt: LocalDate,
    val updatedAt: LocalDate,
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
