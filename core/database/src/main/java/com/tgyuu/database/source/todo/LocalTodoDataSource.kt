package com.tgyuu.database.source.todo

import com.tgyuu.domain.model.TodoSchedule
import java.time.LocalDate

interface LocalTodoDataSource {
    suspend fun getSchedules(): List<TodoSchedule>

    suspend fun addTodo(
        title: String,
        tagId: Int,
        dates: List<LocalDate>,
        priority: Int?,
    )
}
