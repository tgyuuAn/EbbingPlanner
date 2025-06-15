package com.tgyuu.database.source.todo

import com.tgyuu.database.dao.TodoSchedulesDao
import com.tgyuu.database.dao.TodoWithSchedulesDao
import com.tgyuu.database.model.TodoScheduleEntity
import com.tgyuu.database.model.toEntity
import com.tgyuu.domain.model.TodoSchedule
import com.tgyuu.domain.model.sync.TodoInfoForSync
import com.tgyuu.domain.model.sync.TodoScheduleForSync
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

class LocalTodoDataSourceImpl @Inject constructor(
    private val todoSchedulesDao: TodoSchedulesDao,
    private val todoWithSchedulesDao: TodoWithSchedulesDao,
) : LocalTodoDataSource {
    override suspend fun getTodoSchedules(): List<TodoSchedule> =
        todoSchedulesDao.loadAllTodoSchedulesWithInfoAndTag()

    override suspend fun getTodoSchedule(id: Int): TodoSchedule? =
        todoSchedulesDao.loadTodoScheduleWithInfoAndTag(id)

    override suspend fun getTodoScheduleByTodoInfo(id: Int): List<TodoSchedule> =
        todoSchedulesDao.loadTodoScheduleWithInfoAndTagByInfoId(id)

    override suspend fun getTodoSchedulesByDate(date: LocalDate): List<TodoSchedule> =
        todoSchedulesDao.loadTodoScheduleWithInfoAndTagByDate(date)

    override suspend fun getUpcomingTodoSchedules(date: LocalDate): List<TodoSchedule> =
        todoSchedulesDao.loadUpcomingTodoSchedules(date)

    override fun subscribeTodoSchedulesByDate(date: LocalDate): Flow<List<TodoSchedule>> =
        todoSchedulesDao.subscribeTodoScheduleWithInfoAndTagByDate(date)

    override suspend fun getTodoScheduleEntity(id: Int): TodoScheduleEntity? =
        todoSchedulesDao.loadTodoScheduleEntity(id)

    override suspend fun insertSchedule(scheduleForSync: TodoScheduleForSync) =
        todoSchedulesDao.insertTodoSchedule(scheduleForSync.toEntity())

    override suspend fun insertTodoInfo(todoInfoForSync: TodoInfoForSync) =
        todoSchedulesDao.insertTodoInfo(todoInfoForSync.toEntity())

    override suspend fun insertTodos(
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

    override suspend fun updateSchedule(todoSchedule: TodoSchedule) =
        todoWithSchedulesDao.updateSchedule(
            id = todoSchedule.id,
            date = todoSchedule.date,
            memo = todoSchedule.memo,
            priority = todoSchedule.priority,
            isDone = todoSchedule.isDone,
        )

    override suspend fun updateSchedule(todoScheduleForSync: TodoScheduleForSync) =
        todoSchedulesDao.updateTodoSchedule(todoScheduleForSync.toEntity())

    override suspend fun updateTodoInfo(todoSchedule: TodoSchedule) =
        todoWithSchedulesDao.updateInfo(
            id = todoSchedule.infoId,
            title = todoSchedule.title,
            tagId = todoSchedule.tagId,
        )

    override suspend fun updateTodoInfo(todoInfoForSync: TodoInfoForSync) =
        todoSchedulesDao.updateTodoInfo(todoInfoForSync.toEntity())

    override suspend fun softDeleteTodo(todoSchedule: TodoSchedule) =
        todoSchedulesDao.softDeleteSchedule(todoSchedule.toEntity().id)

    override suspend fun hardDeleteTodo(id: Int) = todoSchedulesDao.hardDeleteSchedule(id)

    override suspend fun hardDeleteAllTodos() = todoSchedulesDao.hardDeleteAllSchedule()

    override suspend fun getSchedulesForSync(lastSyncTime: LocalDateTime): List<TodoScheduleForSync> =
        todoSchedulesDao.loadAllSchedulesForSync(lastSyncTime)

    override suspend fun getTodoInfosForSync(lastSyncTime: LocalDateTime): List<TodoInfoForSync> =
        todoSchedulesDao.loadAllTodoInfosForSync(lastSyncTime)
}
