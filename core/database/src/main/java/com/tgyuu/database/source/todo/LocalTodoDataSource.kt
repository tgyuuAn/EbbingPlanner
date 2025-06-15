package com.tgyuu.database.source.todo

import com.tgyuu.domain.model.TodoSchedule
import com.tgyuu.domain.model.sync.TodoInfoForSync
import com.tgyuu.domain.model.sync.TodoScheduleForSync
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalDateTime

interface LocalTodoDataSource {
    suspend fun getSchedules(): List<TodoSchedule>
    suspend fun getSchedule(id: Int): TodoSchedule
    suspend fun getScheduleByTodoInfo(id: Int): List<TodoSchedule>
    suspend fun getSchedulesByDate(date: LocalDate): List<TodoSchedule>
    suspend fun getUpcomingSchedules(date: LocalDate): List<TodoSchedule>
    fun subscribeSchedulesByDate(date: LocalDate): Flow<List<TodoSchedule>>

    suspend fun addTodo(
        title: String,
        tagId: Int,
        dates: List<LocalDate>,
        priority: Int?,
    )

    suspend fun updateTodoInfo(todoSchedule: TodoSchedule)
    suspend fun updateTodo(todoSchedule: TodoSchedule)
    suspend fun softDeleteTodo(todoSchedule: TodoSchedule)
    suspend fun hardDeleteTodo(todoSchedule: TodoSchedule)
    suspend fun hardDeleteAllTodos()

    suspend fun getSchedulesForSync(lastSyncTime: LocalDateTime): List<TodoScheduleForSync>
    suspend fun getTodoInfosForSync(lastSyncTime: LocalDateTime): List<TodoInfoForSync>
}
