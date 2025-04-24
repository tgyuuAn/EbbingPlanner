package com.tgyuu.database.source.todo

import java.time.LocalDate

interface LocalTodoDataSource {
    suspend fun addTodo(
        title: String,
        tagId: Int,
        schedules: List<LocalDate>,
    )
}
