package com.tgyuu.database.source.tag

import com.tgyuu.database.dao.TodoTagsDao
import com.tgyuu.database.model.tag.TodoTagEntity
import javax.inject.Inject

class LocalTagDataSourceImpl @Inject constructor(
    private val todoTagsDao: TodoTagsDao,
) : LocalTagDataSource {
    override suspend fun insertTags(vararg tags: TodoTagEntity) = todoTagsDao.insertTags(*tags)
    override suspend fun deleteTags(vararg tags: TodoTagEntity) = todoTagsDao.deleteTags(*tags)
    override suspend fun getTags(): List<TodoTagEntity> = todoTagsDao.getTags()
    override suspend fun getTag(id: Int): TodoTagEntity = todoTagsDao.getTag(id)
}
