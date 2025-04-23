package com.tgyuu.database.source.tag

import com.tgyuu.database.dao.TodoTagsDao
import com.tgyuu.database.model.tag.TodoTagEntity
import com.tgyuu.database.model.tag.toEntity
import com.tgyuu.domain.model.TodoTag
import javax.inject.Inject

class LocalTagDataSourceImpl @Inject constructor(
    private val todoTagsDao: TodoTagsDao,
) : LocalTagDataSource {
    override suspend fun insertTags(vararg tags: TodoTag) =
        todoTagsDao.insertTags(tags.map(TodoTag::toEntity))

    override suspend fun deleteTags(vararg tags: TodoTag) =
        todoTagsDao.deleteTags(tags.map(TodoTag::toEntity))

    override suspend fun getTags(): List<TodoTagEntity> = todoTagsDao.getTags()
    override suspend fun getTag(id: Int): TodoTagEntity = todoTagsDao.getTag(id)
}
