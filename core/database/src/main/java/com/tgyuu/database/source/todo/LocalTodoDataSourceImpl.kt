package com.tgyuu.database.source.todo

import com.tgyuu.database.dao.SchedulesDao
import com.tgyuu.database.dao.TodoWithSchedulesDao
import com.tgyuu.database.model.toEntity
import com.tgyuu.domain.model.TodoSchedule
import java.time.LocalDate
import javax.inject.Inject

class LocalTodoDataSourceImpl @Inject constructor(
    private val schedulesDao: SchedulesDao,
    private val todoWithSchedulesDao: TodoWithSchedulesDao,
) : LocalTodoDataSource {
    override suspend fun getSchedules(): List<TodoSchedule> =
        schedulesDao.loadAllSchedulesWithInfoAndTag()

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

    override suspend fun updateTodo(todoSchedule: TodoSchedule) =
        schedulesDao.updateSchedules(todoSchedule.toEntity())

    override suspend fun deleteTodo(todoSchedule: TodoSchedule) =
        schedulesDao.deleteSchedules(todoSchedule.toEntity())
}
