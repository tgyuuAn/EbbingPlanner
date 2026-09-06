package com.tgyuu.shared.domain.repository

import com.tgyuu.shared.domain.model.RepeatCycle
import com.tgyuu.shared.domain.model.TodoInfo
import com.tgyuu.shared.domain.model.TodoSchedule
import com.tgyuu.shared.domain.model.TodoTag
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate

interface TodoRepository {
    val recentAddedTagId: Long?
    val recentAddedRepeatCycleId: Long?

    suspend fun loadTodoSchedulesByDateRange(
        startDate: LocalDate,
        endDate: LocalDate
    ): List<TodoSchedule>

    suspend fun loadSchedulesByTodoInfo(id: Int): List<TodoSchedule>
    suspend fun loadSchedulesByDate(date: LocalDate): List<TodoSchedule>
    suspend fun loadUpcomingSchedules(date: LocalDate): List<TodoSchedule>
    suspend fun loadAllSchedules(): List<TodoSchedule>
    suspend fun loadTags(): List<TodoTag>
    suspend fun loadRepeatCycle(id: Int): RepeatCycle
    suspend fun loadRepeatCycles(): List<RepeatCycle>
    fun subscribeSchedulesByDate(date: LocalDate): Flow<List<TodoSchedule>>

    suspend fun addDefaultTag()
    suspend fun addTag(
        name: String,
        color: Int,
    ): Long

    suspend fun addTodo(
        title: String,
        tagId: Int,
        dates: List<LocalDate>,
        isPinned: Boolean,
        restDays: Set<DayOfWeek> = emptySet(),
    )

    suspend fun addTodo(
        title: String,
        tagId: Int,
        dates: List<LocalDate>,
        isDoneSchedules: List<Boolean>,
        isPinned: Boolean,
        restDays: Set<DayOfWeek> = emptySet(),
    )

    suspend fun addRepeatCycle(intervals: List<Int>): Long

    suspend fun updateRepeatCycle(repeatCycle: RepeatCycle)
    suspend fun deleteRepeatCycle(repeatCycle: RepeatCycle)

    suspend fun loadSchedule(id: Int): TodoSchedule?
    suspend fun loadTag(id: Int): TodoTag?
    suspend fun loadTodoInfosByTagId(tagId: Int): List<TodoInfo>
    suspend fun loadTodoInfoById(infoId: Int): TodoInfo

    suspend fun updateTodoInfo(todoSchedule: TodoSchedule, restDays: Set<DayOfWeek> = emptySet())
    suspend fun updateTodo(todoSchedule: TodoSchedule)
    suspend fun updateTodos(schedules: List<TodoSchedule>)
    suspend fun deleteTodo(todoSchedule: TodoSchedule)
    suspend fun deleteTodoByTodoInfo(id: Int)

    suspend fun updateTag(todoTag: TodoTag)
    suspend fun deleteTag(todoTag: TodoTag)

    suspend fun clearData()
}
