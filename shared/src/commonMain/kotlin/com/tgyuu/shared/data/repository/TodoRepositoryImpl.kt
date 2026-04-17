package com.tgyuu.shared.data.repository

import com.tgyuu.shared.common.now
import com.tgyuu.shared.database.dao.RepeatCyclesDao
import com.tgyuu.shared.database.dao.TodoSchedulesDao
import com.tgyuu.shared.database.dao.TodoTagsDao
import com.tgyuu.shared.database.dao.TodoWithSchedulesDao
import com.tgyuu.shared.database.model.RepeatCycleEntity
import com.tgyuu.shared.database.model.TodoTagEntity
import com.tgyuu.shared.database.model.toEntity
import com.tgyuu.shared.domain.model.DefaultTodoTag
import com.tgyuu.shared.domain.model.RepeatCycle
import com.tgyuu.shared.domain.model.TodoInfo
import com.tgyuu.shared.domain.model.TodoSchedule
import com.tgyuu.shared.domain.model.TodoTag
import com.tgyuu.shared.domain.repository.TodoRepository
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

class TodoRepositoryImpl(
    private val todoTagsDao: TodoTagsDao,
    private val todoSchedulesDao: TodoSchedulesDao,
    private val todoWithSchedulesDao: TodoWithSchedulesDao,
    private val repeatCyclesDao: RepeatCyclesDao,
) : TodoRepository {

    private var _recentAddedTagId: Long? = null
    override val recentAddedTagId: Long?
        get() = _recentAddedTagId.also { _recentAddedTagId = null }

    private var _recentAddedRepeatCycleId: Long? = null
    override val recentAddedRepeatCycleId: Long?
        get() = _recentAddedRepeatCycleId.also { _recentAddedRepeatCycleId = null }

    override suspend fun loadTodoSchedulesByDateRange(
        startDate: LocalDate,
        endDate: LocalDate
    ): List<TodoSchedule> = todoSchedulesDao.loadTodoSchedulesByDateRange(startDate, endDate)

    override suspend fun loadSchedulesByTodoInfo(id: Int): List<TodoSchedule> =
        todoSchedulesDao.loadTodoScheduleWithInfoAndTagByInfoId(id)

    override suspend fun loadSchedulesByDate(date: LocalDate): List<TodoSchedule> =
        todoSchedulesDao.loadTodoScheduleWithInfoAndTagByDate(date)

    override suspend fun loadUpcomingSchedules(date: LocalDate): List<TodoSchedule> =
        todoSchedulesDao.loadUpcomingTodoSchedules(date)

    override suspend fun loadAllSchedules(): List<TodoSchedule> =
        todoSchedulesDao.loadAllTodoSchedules()

    override suspend fun loadTags(): List<TodoTag> =
        todoTagsDao.getTags().map { it.toDomain() }

    override suspend fun loadRepeatCycle(id: Int): RepeatCycle =
        repeatCyclesDao.getRepeatCycle(id)!!.toDomain()

    override suspend fun loadRepeatCycles(): List<RepeatCycle> =
        repeatCyclesDao.getRepeatCycles().map { it.toDomain() }

    override fun subscribeSchedulesByDate(date: LocalDate): Flow<List<TodoSchedule>> =
        todoSchedulesDao.subscribeTodoScheduleWithInfoAndTagByDate(date)

    override suspend fun addDefaultTag() {
        todoTagsDao.insertTag(DefaultTodoTag.toEntity())
    }

    override suspend fun addTag(name: String, color: Int): Long {
        val newId = todoTagsDao.insertTag(
            TodoTagEntity(
                name = name,
                color = color,
            )
        )
        _recentAddedTagId = newId
        return newId
    }

    override suspend fun addTodo(
        title: String,
        tagId: Int,
        dates: List<LocalDate>,
        priority: Int?,
        restDays: Set<DayOfWeek>,
    ) {
        todoWithSchedulesDao.insertTodoWithSchedules(
            title = title,
            tagId = tagId,
            dates = dates,
            priority = priority,
            restDays = restDays,
        )
    }

    override suspend fun addTodo(
        title: String,
        tagId: Int,
        dates: List<LocalDate>,
        isDoneSchedules: List<Boolean>,
        priority: Int?,
        restDays: Set<DayOfWeek>,
    ) {
        todoWithSchedulesDao.insertTodoWithSchedules(
            title = title,
            tagId = tagId,
            dates = dates,
            isDoneSchedules = isDoneSchedules,
            priority = priority,
            restDays = restDays,
        )
    }

    override suspend fun addRepeatCycle(intervals: List<Int>): Long {
        val newId = repeatCyclesDao.insertRepeatCycle(RepeatCycleEntity(intervals = intervals))
        _recentAddedRepeatCycleId = newId
        return newId
    }

    override suspend fun updateRepeatCycle(repeatCycle: RepeatCycle) {
        repeatCyclesDao.updateRepeatCycle(repeatCycle.toEntity())
    }

    override suspend fun deleteRepeatCycle(repeatCycle: RepeatCycle) {
        repeatCyclesDao.softDeleteRepeatCycle(repeatCycle.id, LocalDateTime.now())
    }

    override suspend fun loadSchedule(id: Int): TodoSchedule? =
        todoSchedulesDao.loadTodoScheduleWithInfoAndTag(id)

    override suspend fun loadTag(id: Int): TodoTag? =
        todoTagsDao.getTag(id)?.toDomain()

    override suspend fun loadTodoInfosByTagId(tagId: Int): List<TodoInfo> =
        todoSchedulesDao.loadTodoInfoByTagId(tagId)

    override suspend fun loadTodoInfoById(infoId: Int): TodoInfo =
        todoSchedulesDao.getTodoInfoById(infoId)

    override suspend fun updateTodoInfo(todoSchedule: TodoSchedule, restDays: Set<DayOfWeek>) {
        todoWithSchedulesDao.updateInfo(
            id = todoSchedule.infoId,
            title = todoSchedule.title,
            tagId = todoSchedule.tagId,
            restDays = restDays.joinToString(",") { (it.ordinal + 1).toString() },
            updatedAt = LocalDateTime.now(),
        )
    }

    override suspend fun updateTodo(todoSchedule: TodoSchedule) {
        todoWithSchedulesDao.updateSchedule(
            id = todoSchedule.id,
            date = todoSchedule.date,
            memo = todoSchedule.memo,
            priority = todoSchedule.priority,
            isDone = todoSchedule.isDone,
            updatedAt = LocalDateTime.now(),
        )
    }

    override suspend fun deleteTodo(todoSchedule: TodoSchedule) {
        todoSchedulesDao.softDeleteSchedule(todoSchedule.id, LocalDateTime.now())
    }

    override suspend fun deleteTodoByTodoInfo(id: Int) {
        todoSchedulesDao.softDeleteScheduleByTodoInfo(id, LocalDateTime.now())
    }

    override suspend fun updateTag(todoTag: TodoTag) {
        todoTagsDao.updateTag(
            id = todoTag.id,
            name = todoTag.name,
            color = todoTag.color,
            updatedAt = LocalDateTime.now(),
        )
    }

    override suspend fun deleteTag(todoTag: TodoTag) {
        todoTagsDao.softDeleteTagWithReset(todoTag.toEntity())
    }

    override suspend fun clearData() {
        coroutineScope {
            launch { todoTagsDao.softDeleteAllTags(LocalDateTime.now()) }
            launch { todoSchedulesDao.softDeleteAllSchedules(LocalDateTime.now()) }
            launch { repeatCyclesDao.softDeleteAllRepeatCycles(LocalDateTime.now()) }
        }
    }
}
