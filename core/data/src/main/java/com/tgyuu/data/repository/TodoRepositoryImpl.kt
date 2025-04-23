package com.tgyuu.data.repository

import com.tgyuu.database.model.tag.TodoTagEntity
import com.tgyuu.database.source.tag.LocalTagDataSource
import com.tgyuu.domain.model.TodoTag
import com.tgyuu.domain.repository.TodoRepository
import javax.inject.Inject

class TodoRepositoryImpl @Inject constructor(
    private val localTokenDataSource: LocalTagDataSource,
) : TodoRepository {
    override suspend fun loadTagList(): List<TodoTag> = localTokenDataSource.getTags()
        .map(TodoTagEntity::toDomain)
}
