package com.tgyuu.database.source.tag

import com.tgyuu.database.model.TodoTagEntity
import com.tgyuu.domain.model.TodoTag

interface LocalTagDataSource {
    suspend fun insertTag(todoTag: TodoTag)
    suspend fun insertTag(
        name: String,
        color: Int,
    )

    suspend fun deleteTags(vararg tags: TodoTag)
    suspend fun getTags(): List<TodoTagEntity>
    suspend fun getTag(id: Int): TodoTagEntity
}
