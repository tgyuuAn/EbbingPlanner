package com.tgyuu.data.repository

import com.tgyuu.database.model.RepeatCycleEntity
import com.tgyuu.database.model.TodoTagEntity
import com.tgyuu.database.source.repeatcycle.LocalRepeatCycleDataSource
import com.tgyuu.database.source.tag.LocalTagDataSource
import com.tgyuu.database.source.todo.LocalTodoDataSource
import com.tgyuu.domain.model.DefaultTodoTag
import com.tgyuu.domain.model.RepeatCycle
import com.tgyuu.domain.model.TodoSchedule
import com.tgyuu.domain.model.TodoTag
import com.tgyuu.domain.repository.TodoRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject

class TodoRepositoryImpl @Inject constructor(
    private val localTagDataSource: LocalTagDataSource,
    private val localTodoDataSource: LocalTodoDataSource,
    private val localRepeatCycleDataSource: LocalRepeatCycleDataSource,
) : TodoRepository {
    private var _recentAddedTagId: Long? = null
    override val recentAddedTagId: Long?
        get() = _recentAddedTagId.also { _recentAddedTagId = null }

    private var _recentAddedRepeatCycleId: Long? = null
    override val recentAddedRepeatCycleId: Long?
        get() = _recentAddedRepeatCycleId.also { _recentAddedRepeatCycleId = null }

    override suspend fun loadSchedules(): List<TodoSchedule> =
        localTodoDataSource.getTodoSchedules()

    override suspend fun loadSchedulesByTodoInfo(id: Int): List<TodoSchedule> =
        localTodoDataSource.getTodoScheduleByTodoInfo(id)

    override suspend fun loadSchedulesByDate(date: LocalDate): List<TodoSchedule> =
        localTodoDataSource.getTodoSchedulesByDate(date)

    override suspend fun loadUpcomingSchedules(date: LocalDate): List<TodoSchedule> =
        localTodoDataSource.getUpcomingTodoSchedules(date)

    override suspend fun loadTags(): List<TodoTag> = localTagDataSource.getTags()
        .map(TodoTagEntity::toDomain)

    override suspend fun loadRepeatCycle(id: Int): RepeatCycle =
        localRepeatCycleDataSource.getRepeatCycle(id)!!.toDomain()

    override suspend fun loadRepeatCycles(): List<RepeatCycle> =
        localRepeatCycleDataSource.getRepeatCycles().map(RepeatCycleEntity::toDomain)

    override fun subscribeSchedulesByDate(date: LocalDate): Flow<List<TodoSchedule>> =
        localTodoDataSource.subscribeTodoSchedulesByDate(date)

    override suspend fun addDefaultTag() {
        localTagDataSource.insertTag(DefaultTodoTag)
    }

    override suspend fun addTag(
        name: String,
        color: Int,
    ): Long {
        val newId = localTagDataSource.insertTag(
            name = name,
            color = color,
        )

        _recentAddedTagId = newId
        return newId
    }

    override suspend fun addTodo(
        title: String,
        tagId: Int,
        dates: List<LocalDate>,
        priority: Int?,
    ) = localTodoDataSource.insertTodos(
        title = title,
        tagId = tagId,
        dates = dates,
        priority = priority,
    )

    override suspend fun addRepeatCycle(intervals: List<Int>): Long {
        val newId = localRepeatCycleDataSource.insertRepeatCycle(intervals = intervals)

        _recentAddedRepeatCycleId = newId
        return newId
    }

    override suspend fun updateRepeatCycle(repeatCycle: RepeatCycle) =
        localRepeatCycleDataSource.updateRepeatCycle(repeatCycle)

    override suspend fun deleteRepeatCycle(repeatCycle: RepeatCycle) =
        localRepeatCycleDataSource.softDeleteRepeatCycle(repeatCycle)

    override suspend fun loadSchedule(id: Int): TodoSchedule =
        localTodoDataSource.getTodoSchedule(id)!!

    override suspend fun loadTag(id: Int): TodoTag = localTagDataSource.getTag(id)!!.toDomain()

    override suspend fun updateTodoInfo(todoSchedule: TodoSchedule) =
        localTodoDataSource.updateTodoInfo(todoSchedule)

    override suspend fun updateTodo(todoSchedule: TodoSchedule) =
        localTodoDataSource.updateSchedule(todoSchedule)

    override suspend fun deleteTodo(todoSchedule: TodoSchedule) =
        localTodoDataSource.softDeleteTodo(todoSchedule)

    override suspend fun updateTag(todoTag: TodoTag) = localTagDataSource.updateTag(todoTag)
    override suspend fun deleteTag(todoTag: TodoTag) = localTagDataSource.softDeleteTag(todoTag)
}
