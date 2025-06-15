package com.tgyuu.database.source.tag

import com.tgyuu.database.dao.TodoTagsDao
import com.tgyuu.database.model.TodoTagEntity
import com.tgyuu.database.model.toEntity
import com.tgyuu.domain.model.TodoTag
import com.tgyuu.domain.model.sync.TodoTagForSync
import java.time.LocalDateTime
import javax.inject.Inject

class LocalTagDataSourceImpl @Inject constructor(
    private val todoTagsDao: TodoTagsDao,
) : LocalTagDataSource {
    override suspend fun insertTag(tag: TodoTag) = todoTagsDao.insertTag(tag.toEntity())
    override suspend fun insertTag(tag: TodoTagForSync): Long =
        todoTagsDao.insertTag(tag.toEntity())

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

    override suspend fun updateTag(tag: TodoTagForSync) = todoTagsDao.updateTag(tag.toEntity())

    override suspend fun softDeleteTag(tag: TodoTag) =
        todoTagsDao.softDeleteTagWithReset(tag.toEntity())

    override suspend fun hardDeleteTag(id: Int) = todoTagsDao.hardDeleteTag(id)
    override suspend fun hardDeleteAllTags() = todoTagsDao.hardDeleteAllTags()

    override suspend fun getTags(): List<TodoTagEntity> = todoTagsDao.getTags()
    override suspend fun getTag(id: Int): TodoTagEntity? = todoTagsDao.getTag(id)

    override suspend fun getTagsForSync(lastSyncTime: LocalDateTime): List<TodoTagForSync> =
        todoTagsDao.getTagsForSync(lastSyncTime)
}
