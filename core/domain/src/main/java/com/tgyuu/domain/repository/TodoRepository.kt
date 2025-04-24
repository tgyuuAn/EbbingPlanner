package com.tgyuu.domain.repository

import com.tgyuu.domain.model.TodoSchedule
import com.tgyuu.domain.model.TodoTag
import java.time.LocalDate

interface TodoRepository {
    suspend fun loadSchedules(): List<TodoSchedule>
    suspend fun loadTagList(): List<TodoTag>
    suspend fun addDefaultTag()
    suspend fun addTag(
        name: String,
        color: Int,
    )

    suspend fun addTodo(
        title: String,
        tagId: Int,
        schedules: List<LocalDate>,
        priority: Int?,
    )
}
