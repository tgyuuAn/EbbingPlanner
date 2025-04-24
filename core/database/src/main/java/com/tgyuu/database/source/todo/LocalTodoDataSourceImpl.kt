package com.tgyuu.database.source.todo

import com.tgyuu.database.dao.TodoWithSchedulesDao
import java.time.LocalDate
import javax.inject.Inject

class LocalTodoDataSourceImpl @Inject constructor(
    private val todoWithSchedulesDao: TodoWithSchedulesDao,
) : LocalTodoDataSource {
    override suspend fun addTodo(
        title: String,
        tagId: Int,
        dates: List<LocalDate>,
        priority: Int?,
    ) = todoWithSchedulesDao.insertTodoWithSchedules(
        title = title,
        tagId = tagId,
        dates = dates,
        priority = priority,
    )
}
