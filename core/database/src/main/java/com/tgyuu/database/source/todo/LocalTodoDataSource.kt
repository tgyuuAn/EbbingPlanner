package com.tgyuu.database.source.todo

import com.tgyuu.domain.model.TodoSchedule
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

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
}
