package com.tgyuu.domain.repository

import com.tgyuu.domain.model.TodoSchedule
import com.tgyuu.domain.model.TodoTag
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface TodoRepository {
    val recentAddedTagId: Long?

    suspend fun loadSchedules(): List<TodoSchedule>
    suspend fun loadSchedulesByTodoInfo(id: Int): List<TodoSchedule>
    suspend fun loadSchedulesByDate(date: LocalDate): List<TodoSchedule>
    suspend fun loadUpcomingSchedules(date: LocalDate): List<TodoSchedule>
    suspend fun loadTagList(): List<TodoTag>
    fun subscribeSchedulesByDate(date: LocalDate): Flow<List<TodoSchedule>>

    suspend fun addDefaultTag(): Long
    suspend fun addTag(
        name: String,
        color: Int,
    ): Long

    suspend fun addTodo(
        title: String,
        tagId: Int,
        dates: List<LocalDate>,
        priority: Int?,
    )

    suspend fun loadSchedule(id: Int): TodoSchedule
    suspend fun loadTag(id: Int): TodoTag

    suspend fun updateTodo(todoSchedule: TodoSchedule)
    suspend fun deleteTodo(todoSchedule: TodoSchedule)

    suspend fun updateTag(todoTag: TodoTag)
    suspend fun deleteTag(todoTag: TodoTag)
}
