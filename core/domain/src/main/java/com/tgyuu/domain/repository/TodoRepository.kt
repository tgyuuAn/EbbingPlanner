package com.tgyuu.domain.repository

import com.tgyuu.domain.model.TodoSchedule
import com.tgyuu.domain.model.TodoTag
import java.time.LocalDate

interface TodoRepository {
    val recentAddedTagId: Long?

    suspend fun loadSchedules(): List<TodoSchedule>
    suspend fun loadTagList(): List<TodoTag>
    suspend fun addDefaultTag(): Long
    suspend fun addTag(
        name: String,
        color: Int,
    ): Long

    suspend fun addTodo(
        title: String,
        tagId: Int,
        schedules: List<LocalDate>,
        priority: Int?,
    )

    suspend fun loadSchedule(id: Int): TodoSchedule
    suspend fun loadTag(id: Int): TodoTag

    suspend fun updateTodo(todoSchedule: TodoSchedule)
    suspend fun deleteTodo(todoSchedule: TodoSchedule)
}
