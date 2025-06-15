package com.tgyuu.database.source.tag

import com.tgyuu.database.model.TodoTagEntity
import com.tgyuu.domain.model.TodoTag
import com.tgyuu.domain.model.sync.TodoTagForSync
import java.time.LocalDateTime

interface LocalTagDataSource {
    suspend fun insertTag(todoTag: TodoTag): Long
    suspend fun insertTag(
        name: String,
        color: Int,
    ): Long

    suspend fun updateTag(tag: TodoTag)
    suspend fun softDeleteTag(tag: TodoTag)
    suspend fun hardDeleteTag(tag: TodoTag)
    suspend fun hardDeleteAllTags()

    suspend fun getTags(): List<TodoTagEntity>
    suspend fun getTag(id: Int): TodoTagEntity

    suspend fun getTagsForSync(lastSyncTime: LocalDateTime): List<TodoTagForSync>
}
