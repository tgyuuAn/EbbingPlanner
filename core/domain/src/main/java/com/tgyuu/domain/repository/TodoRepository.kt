package com.tgyuu.domain.repository

import com.tgyuu.domain.model.TodoTag

interface TodoRepository {
    suspend fun loadTagList(): List<TodoTag>
    suspend fun addTag(todoTag: TodoTag)
}
