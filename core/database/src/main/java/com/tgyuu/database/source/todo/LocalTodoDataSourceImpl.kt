package com.tgyuu.database.source.todo

import com.tgyuu.database.dao.SchedulesDao
import java.time.LocalDate
import javax.inject.Inject

class LocalTodoDataSourceImpl @Inject constructor(
    private val schedulesDao: SchedulesDao,
) : LocalTodoDataSource {
    override suspend fun addTodo(
        title: String,
        tagId: Int,
        schedules: List<LocalDate>,
    ) = schedulesDao
}
