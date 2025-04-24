package com.tgyuu.data.repository

import com.tgyuu.database.model.TodoTagEntity
import com.tgyuu.database.source.tag.LocalTagDataSource
import com.tgyuu.database.source.todo.LocalTodoDataSource
import com.tgyuu.domain.model.DefaultTodoTag
import com.tgyuu.domain.model.TodoSchedule
import com.tgyuu.domain.model.TodoTag
import com.tgyuu.domain.repository.TodoRepository
import java.time.LocalDate
import javax.inject.Inject

class TodoRepositoryImpl @Inject constructor(
    private val localTagDataSource: LocalTagDataSource,
    private val localTodoDataSource: LocalTodoDataSource,
) : TodoRepository {
    override suspend fun loadSchedules(): List<TodoSchedule> = localTodoDataSource.getSchedules()

    override suspend fun loadTagList(): List<TodoTag> = localTagDataSource.getTags()
        .map(TodoTagEntity::toDomain)

    override suspend fun addDefaultTag() = localTagDataSource.insertTag(DefaultTodoTag)

    override suspend fun addTag(
        name: String,
        color: Int,
    ) = localTagDataSource.insertTag(
        name = name,
        color = color,
    )

    override suspend fun addTodo(
        title: String,
        tagId: Int,
        dates: List<LocalDate>,
        priority: Int?,
    ) = localTodoDataSource.addTodo(
        title = title,
        tagId = tagId,
        dates = dates,
        priority = priority,
    )
}
