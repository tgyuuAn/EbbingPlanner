package com.tgyuu.database.source.tag

import com.tgyuu.database.model.tag.TodoTagEntity

interface LocalTagDataSource {
    suspend fun insertTags(vararg tags: TodoTagEntity)
    suspend fun deleteTags(vararg tags: TodoTagEntity)
    suspend fun getTags(): List<TodoTagEntity>
    suspend fun getTag(id: Int): TodoTagEntity
}
