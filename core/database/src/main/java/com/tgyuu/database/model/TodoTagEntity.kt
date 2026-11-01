package com.tgyuu.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tgyuu.domain.model.TodoTag
import com.tgyuu.domain.model.sync.TodoTagForSync
import java.time.LocalDate
import java.time.LocalDateTime

@Entity(tableName = "todo_tag")
data class TodoTagEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val color: Int,
    val createdAt: LocalDate = LocalDate.now(),
    val isDeleted: Boolean = false,
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    // PoC: 기기 로컬 전용 보관 상태. 동기화 wire(TodoTagForSync)에는 아직 포함하지 않는다.
    @ColumnInfo(defaultValue = "0") val isArchived: Boolean = false,
) {
    fun toDomain() = TodoTag(
        id = id,
        name = name,
        color = color,
        createdAt = createdAt,
        isArchived = isArchived,
    )
}

fun TodoTag.toEntity() = TodoTagEntity(
    id = this.id,
    name = this.name,
    color = this.color,
    createdAt = this.createdAt,
)

fun TodoTagForSync.toEntity() = TodoTagEntity(
    id = this.id,
    name = this.name,
    color = this.color,
    createdAt = this.createdAt,
    isDeleted = this.isDeleted,
    updatedAt = this.updatedAt,
)
