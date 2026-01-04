package com.tgyuu.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.tgyuu.common.now
import com.tgyuu.database.model.TodoInfoEntity
import com.tgyuu.database.model.TodoScheduleEntity
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

@Dao
interface TodoWithSchedulesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInfo(entity: TodoInfoEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedules(schedules: List<TodoScheduleEntity>)

    @Transaction
    suspend fun insertTodoWithSchedules(
        title: String,
        tagId: Int,
        dates: List<LocalDate>,
        priority: Int?,
        restDays: Set<kotlinx.datetime.DayOfWeek> = emptySet(),
    ) {
        val infoId = insertInfo(
            TodoInfoEntity(
                title = title,
                tagId = tagId,
                restDays = restDays.joinToString(",") { (it.ordinal + 1).toString() },
            )
        ).toInt()

        insertSchedules(
            dates.map { date ->
                TodoScheduleEntity(
                    infoId = infoId,
                    date = date,
                    memo = "",
                    priority = priority ?: 0,
                )
            }
        )
    }

    @Transaction
    suspend fun insertTodoWithSchedules(
        title: String,
        tagId: Int,
        dates: List<LocalDate>,
        isDoneSchedules: List<Boolean>,
        priority: Int?,
        restDays: Set<kotlinx.datetime.DayOfWeek> = emptySet(),
    ) {
        val infoId = insertInfo(
            TodoInfoEntity(
                title = title,
                tagId = tagId,
                restDays = restDays.joinToString(",") { (it.ordinal + 1).toString() },
            )
        ).toInt()

        insertSchedules(
            dates.zip(isDoneSchedules) { date, isDone ->
                TodoScheduleEntity(
                    infoId = infoId,
                    date = date,
                    isDone = isDone,
                    memo = "",
                    priority = priority ?: 0,
                )
            }
        )
    }

    @Query(
        """
        UPDATE todo_info
        SET title = :title, tagId = :tagId, rest_days = :restDays, updatedAt = :updatedAt
        WHERE id = :id
        """
    )
    suspend fun updateInfo(
        id: Int,
        title: String,
        tagId: Int,
        restDays: String = "",
        updatedAt: LocalDateTime = LocalDateTime.now(),
    )

    @Query(
        """
        UPDATE schedule
        SET date = :date, memo = :memo, priority = :priority, isDone = :isDone, updatedAt = :updatedAt
        WHERE id = :id AND isDeleted = 0
        """
    )
    suspend fun updateSchedule(
        id: Int,
        date: LocalDate,
        memo: String,
        priority: Int,
        isDone: Boolean,
        updatedAt: LocalDateTime = LocalDateTime.now(),
    )

    @Transaction
    suspend fun updateSchedules(schedules: List<ScheduleUpdateParams>) {
        val now = LocalDateTime.now()
        schedules.forEach { params ->
            updateSchedule(
                id = params.id,
                date = params.date,
                memo = params.memo,
                priority = params.priority,
                isDone = params.isDone,
                updatedAt = now,
            )
        }
    }
}

data class ScheduleUpdateParams(
    val id: Int,
    val date: LocalDate,
    val memo: String,
    val priority: Int,
    val isDone: Boolean,
)
