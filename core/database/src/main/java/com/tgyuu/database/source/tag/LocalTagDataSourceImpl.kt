package com.tgyuu.database.source.tag

import com.tgyuu.database.dao.TodoTagsDao
import com.tgyuu.database.model.TodoTagEntity
import com.tgyuu.database.model.toEntity
import com.tgyuu.domain.model.TodoTag
import java.time.LocalDateTime
import javax.inject.Inject

class LocalTagDataSourceImpl @Inject constructor(
    private val todoTagsDao: TodoTagsDao,
) : LocalTagDataSource {
    override suspend fun insertTag(todoTag: TodoTag) = todoTagsDao.insertTag(todoTag.toEntity())
    override suspend fun insertTag(
        name: String,
        color: Int,
    ) = todoTagsDao.insertTag(TodoTagEntity(name = name, color = color))

    override suspend fun updateTag(tag: TodoTag) = todoTagsDao.updateTag(
        id = tag.id,
        name = tag.name,
        color = tag.color,
        updatedAt = LocalDateTime.now(),
    )

    override suspend fun softDeleteTag(tag: TodoTag) {
        todoTagsDao.softDeleteTagWithReset(tag.toEntity())
    }

    override suspend fun hardDeleteTag(tag: TodoTag) {
        todoTagsDao.hardDeleteTag(tag.toEntity())
    }

    override suspend fun getTags(): List<TodoTagEntity> = todoTagsDao.getTags()
    override suspend fun getTag(id: Int): TodoTagEntity = todoTagsDao.getTag(id)
}
