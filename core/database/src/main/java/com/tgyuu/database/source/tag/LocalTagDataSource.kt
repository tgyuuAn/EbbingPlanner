package com.tgyuu.database.source.tag

import com.tgyuu.database.model.TodoTagEntity
import com.tgyuu.domain.model.TodoTag

interface LocalTagDataSource {
    suspend fun insertTag(todoTag: TodoTag): Long
    suspend fun insertTag(
        name: String,
        color: Int,
    ): Long

    suspend fun updateTag(tag: TodoTag)
    suspend fun deleteTag(tag: TodoTag)
    suspend fun getTags(): List<TodoTagEntity>
    suspend fun getTag(id: Int): TodoTagEntity
}
