package com.tgyuu.database.source.todo

import com.tgyuu.database.dao.SchedulesDao
import com.tgyuu.database.dao.TodoWithSchedulesDao
import com.tgyuu.database.model.toEntity
import com.tgyuu.domain.model.TodoSchedule
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject

class LocalTodoDataSourceImpl @Inject constructor(
    private val schedulesDao: SchedulesDao,
    private val todoWithSchedulesDao: TodoWithSchedulesDao,
) : LocalTodoDataSource {
    override suspend fun getSchedules(): List<TodoSchedule> =
        schedulesDao.loadAllSchedulesWithInfoAndTag()

    override suspend fun getSchedule(id: Int): TodoSchedule =
        schedulesDao.loadScheduleWithInfoAndTag(id)

    override suspend fun getScheduleByTodoInfo(id: Int): List<TodoSchedule> =
        schedulesDao.loadScheduleWithInfoAndTagByInfoId(id)

    override suspend fun getSchedulesByDate(date: LocalDate): List<TodoSchedule> =
        schedulesDao.loadScheduleWithInfoAndTagByDate(date)

    override suspend fun getUpcomingSchedules(date: LocalDate): List<TodoSchedule> =
        schedulesDao.loadUpcomingSchedules(date)

    override fun subscribeSchedulesByDate(date: LocalDate): Flow<List<TodoSchedule>> =
        schedulesDao.subscribeScheduleWithInfoAndTagByDate(date)

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
        todoWithSchedulesDao.updateTodoSchedules(todoSchedule)

    override suspend fun deleteTodo(todoSchedule: TodoSchedule) =
        schedulesDao.deleteSchedules(todoSchedule.toEntity())
}
