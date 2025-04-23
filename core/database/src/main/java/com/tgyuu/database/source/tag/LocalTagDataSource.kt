package com.tgyuu.database.source.tag

import com.tgyuu.database.model.tag.TodoTagEntity
import com.tgyuu.domain.model.TodoTag

interface LocalTagDataSource {
    suspend fun insertTags(vararg tags: TodoTag)
    suspend fun deleteTags(vararg tags: TodoTag)
    suspend fun getTags(): List<TodoTagEntity>
    suspend fun getTag(id: Int): TodoTagEntity
}
