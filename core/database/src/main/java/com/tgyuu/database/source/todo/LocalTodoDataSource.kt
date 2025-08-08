package com.tgyuu.database.source.todo

import com.tgyuu.database.model.TodoScheduleEntity
import com.tgyuu.domain.model.TodoInfo
import com.tgyuu.domain.model.TodoSchedule
import com.tgyuu.domain.model.sync.TodoInfoForSync
import com.tgyuu.domain.model.sync.TodoScheduleForSync
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalDateTime

interface LocalTodoDataSource {
    suspend fun getTodoSchedule(id: Int): TodoSchedule?
    suspend fun getTodoScheduleByTodoInfo(id: Int): List<TodoSchedule>
    suspend fun getTodoSchedulesByDate(date: LocalDate): List<TodoSchedule>
    suspend fun getUpcomingTodoSchedules(date: LocalDate): List<TodoSchedule>
    suspend fun getAllTodoSchedules(): List<TodoSchedule>
    suspend fun getTodoSchedulesByDateRange(
        startDate: LocalDate,
        endDate: LocalDate
    ): List<TodoSchedule>

    fun subscribeTodoSchedulesByDate(date: LocalDate): Flow<List<TodoSchedule>>

    suspend fun getTodoScheduleEntity(id: Int): TodoScheduleEntity?

    suspend fun insertSchedule(scheduleForSync: TodoScheduleForSync)
    suspend fun insertTodoInfo(todoInfoForSync: TodoInfoForSync)
    suspend fun insertTodos(
        title: String,
        tagId: Int,
        dates: List<LocalDate>,
        priority: Int?,
    )

    suspend fun insertTodos(
        title: String,
        tagId: Int,
        dates: List<LocalDate>,
        isDoneSchedules: List<Boolean>,
        priority: Int?,
    )

    suspend fun updateTodoInfo(todoSchedule: TodoSchedule)
    suspend fun updateTodoInfo(todoInfoForSync: TodoInfoForSync)
    suspend fun updateSchedule(todoSchedule: TodoSchedule)
    suspend fun updateSchedule(todoScheduleForSync: TodoScheduleForSync)

    suspend fun softDeleteTodo(todoSchedule: TodoSchedule)
    suspend fun softDeleteTodoByTodoInfo(id: Int)
    suspend fun softDeleteAllTodos()

    suspend fun hardDeleteTodo(id: Int)
    suspend fun hardDeleteAllTodos()

    suspend fun getSchedulesForSync(lastSyncTime: LocalDateTime): List<TodoScheduleForSync>
    suspend fun getTodoInfosForSync(lastSyncTime: LocalDateTime): List<TodoInfoForSync>
    suspend fun getTodoInfosByTagId(tagId: Int): List<TodoInfo>
    suspend fun deleteAllTodoInfos()
}
