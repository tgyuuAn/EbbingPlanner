package com.tgyuu.database.source.tag

import com.tgyuu.database.dao.TodoTagsDao
import com.tgyuu.database.model.TodoTagEntity
import com.tgyuu.database.model.toEntity
import com.tgyuu.domain.model.TodoTag
import javax.inject.Inject

class LocalTagDataSourceImpl @Inject constructor(
    private val todoTagsDao: TodoTagsDao,
) : LocalTagDataSource {
    override suspend fun insertTag(todoTag: TodoTag) = todoTagsDao.insertTag(todoTag.toEntity())
    override suspend fun insertTag(
        name: String,
        color: Int,
    ) = todoTagsDao.insertTag(TodoTagEntity(name = name, color = color))

    override suspend fun updateTag(tag: TodoTag) = todoTagsDao.updateTag(tag.toEntity())

    override suspend fun deleteTag(tag: TodoTag) {
        todoTagsDao.deleteTagWithReset(tag.toEntity())
    }

    override suspend fun getTags(): List<TodoTagEntity> = todoTagsDao.getTags()
    override suspend fun getTag(id: Int): TodoTagEntity = todoTagsDao.getTag(id)
}
