package com.tgyuu.home.fake

import com.tgyuu.domain.model.RepeatCycle
import com.tgyuu.domain.model.TodoInfo
import com.tgyuu.domain.model.TodoSchedule
import com.tgyuu.domain.model.TodoTag
import com.tgyuu.domain.repository.TodoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate

class FakeTodoRepository : TodoRepository {
    private val schedules = mutableListOf<TodoSchedule>()
    private val tags = mutableListOf<TodoTag>()
    private val repeatCycles = mutableListOf<RepeatCycle>()
    private val todoInfos = mutableMapOf<Int, TodoInfo>()

    override var recentAddedTagId: Long? = null
    override var recentAddedRepeatCycleId: Long? = null

    fun addSchedules(vararg schedule: TodoSchedule) {
        schedules.addAll(schedule)
    }

    fun clearSchedules() {
        schedules.clear()
    }

    override suspend fun loadTodoSchedulesByDateRange(
        startDate: LocalDate,
        endDate: LocalDate
    ): List<TodoSchedule> {
        return schedules.filter { it.date in startDate..endDate }
    }

    override suspend fun loadSchedulesByTodoInfo(id: Int): List<TodoSchedule> {
        return schedules.filter { it.infoId == id }
    }

    override suspend fun loadSchedulesByDate(date: LocalDate): List<TodoSchedule> {
        return schedules.filter { it.date == date }
    }

    override suspend fun loadUpcomingSchedules(date: LocalDate): List<TodoSchedule> {
        return schedules.filter { it.date >= date }
    }

    override suspend fun loadAllSchedules(): List<TodoSchedule> {
        return schedules.toList()
    }

    override suspend fun loadTags(): List<TodoTag> {
        return tags.toList()
    }

    override suspend fun loadRepeatCycle(id: Int): RepeatCycle {
        return repeatCycles.first { it.id == id }
    }

    override suspend fun loadRepeatCycles(): List<RepeatCycle> {
        return repeatCycles.toList()
    }

    override fun subscribeSchedulesByDate(date: LocalDate): Flow<List<TodoSchedule>> {
        return flowOf(schedules.filter { it.date == date })
    }

    override suspend fun addDefaultTag() {
        // No-op for testing
    }

    override suspend fun addTag(name: String, color: Int): Long {
        val id = (tags.maxOfOrNull { it.id } ?: 0) + 1
        tags.add(TodoTag(id = id, name = name, color = color, createdAt = LocalDate.now()))
        return id.toLong()
    }

    override suspend fun addTodo(
        title: String,
        tagId: Int,
        dates: List<LocalDate>,
        priority: Int?,
        restDays: Set<DayOfWeek>
    ) {
        // No-op for testing
    }

    override suspend fun addTodo(
        title: String,
        tagId: Int,
        dates: List<LocalDate>,
        isDoneSchedules: List<Boolean>,
        priority: Int?,
        restDays: Set<DayOfWeek>
    ) {
        // No-op for testing
    }

    override suspend fun addRepeatCycle(intervals: List<Int>): Long {
        val id = (repeatCycles.maxOfOrNull { it.id } ?: 0) + 1
        repeatCycles.add(RepeatCycle(id = id, intervals = intervals))
        return id.toLong()
    }

    override suspend fun updateRepeatCycle(repeatCycle: RepeatCycle) {
        val index = repeatCycles.indexOfFirst { it.id == repeatCycle.id }
        if (index != -1) {
            repeatCycles[index] = repeatCycle
        }
    }

    override suspend fun deleteRepeatCycle(repeatCycle: RepeatCycle) {
        repeatCycles.removeIf { it.id == repeatCycle.id }
    }

    override suspend fun loadSchedule(id: Int): TodoSchedule? {
        return schedules.firstOrNull { it.id == id }
    }

    override suspend fun loadTag(id: Int): TodoTag? {
        return tags.firstOrNull { it.id == id }
    }

    override suspend fun loadTodoInfosByTagId(tagId: Int): List<TodoInfo> {
        return emptyList() // Not needed for these tests
    }

    override suspend fun loadTodoInfoById(infoId: Int): TodoInfo {
        return todoInfos[infoId] ?: TodoInfo(
            id = infoId,
            title = "Test Todo",
            tagId = 1,
            restDays = emptySet()
        )
    }

    fun setRestDays(infoId: Int, restDays: Set<DayOfWeek>) {
        val existing = todoInfos[infoId] ?: TodoInfo(
            id = infoId,
            title = "Test Todo",
            tagId = 1,
            restDays = restDays
        )
        todoInfos[infoId] = existing.copy(restDays = restDays)
    }

    override suspend fun updateTodoInfo(todoSchedule: TodoSchedule, restDays: Set<DayOfWeek>) {
        val existing = todoInfos[todoSchedule.infoId]
        if (existing != null) {
            todoInfos[todoSchedule.infoId] = existing.copy(
                title = todoSchedule.title,
                tagId = todoSchedule.tagId,
                restDays = restDays
            )
        }
    }

    override suspend fun updateTodo(todoSchedule: TodoSchedule) {
        val index = schedules.indexOfFirst { it.id == todoSchedule.id }
        if (index != -1) {
            schedules[index] = todoSchedule
        }
    }

    override suspend fun updateTodos(todoSchedules: List<TodoSchedule>) {
        todoSchedules.forEach { updateTodo(it) }
    }

    override suspend fun deleteTodo(todoSchedule: TodoSchedule) {
        schedules.removeIf { it.id == todoSchedule.id }
    }

    override suspend fun deleteTodoByTodoInfo(id: Int) {
        schedules.removeIf { it.infoId == id }
    }

    override suspend fun updateTag(todoTag: TodoTag) {
        val index = tags.indexOfFirst { it.id == todoTag.id }
        if (index != -1) {
            tags[index] = todoTag
        }
    }

    override suspend fun deleteTag(todoTag: TodoTag) {
        tags.removeIf { it.id == todoTag.id }
    }

    override suspend fun clearData() {
        schedules.clear()
        tags.clear()
        repeatCycles.clear()
    }
}
