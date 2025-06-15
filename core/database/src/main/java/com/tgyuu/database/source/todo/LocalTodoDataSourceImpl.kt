package com.tgyuu.database.source.todo

import com.tgyuu.database.dao.SchedulesDao
import com.tgyuu.database.dao.TodoWithSchedulesDao
import com.tgyuu.database.model.toEntity
import com.tgyuu.domain.model.TodoSchedule
import com.tgyuu.domain.model.sync.TodoInfoForSync
import com.tgyuu.domain.model.sync.TodoScheduleForSync
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalDateTime
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
        todoWithSchedulesDao.updateSchedule(
            id = todoSchedule.id,
            date = todoSchedule.date,
            memo = todoSchedule.memo,
            priority = todoSchedule.priority,
            isDone = todoSchedule.isDone,
        )

    override suspend fun updateTodoInfo(todoSchedule: TodoSchedule) =
        todoWithSchedulesDao.updateInfo(
            id = todoSchedule.infoId,
            title = todoSchedule.title,
            tagId = todoSchedule.tagId,
        )

    override suspend fun softDeleteTodo(todoSchedule: TodoSchedule) =
        schedulesDao.softDeleteSchedule(todoSchedule.toEntity().id)

    override suspend fun hardDeleteTodo(todoSchedule: TodoSchedule) =
        schedulesDao.hardDeleteSchedule(todoSchedule.toEntity())

    override suspend fun hardDeleteAllTodos() = schedulesDao.hardDeleteAllSchedule()

    override suspend fun getSchedulesForSync(lastSyncTime: LocalDateTime): List<TodoScheduleForSync> =
        schedulesDao.loadAllSchedulesForSync(lastSyncTime)

    override suspend fun getTodoInfosForSync(lastSyncTime: LocalDateTime): List<TodoInfoForSync> =
        schedulesDao.loadAllTodoInfosForSync(lastSyncTime)
}
