package com.tgyuu.shared.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.tgyuu.shared.common.now
import com.tgyuu.shared.database.model.TodoInfoEntity
import com.tgyuu.shared.database.model.TodoScheduleEntity
import kotlinx.datetime.DayOfWeek
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
        isPinned: Boolean,
        restDays: Set<DayOfWeek> = emptySet(),
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
                    isPinned = isPinned,
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
        isPinned: Boolean,
        restDays: Set<DayOfWeek> = emptySet(),
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
                    isPinned = isPinned,
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
        SET date = :date, memo = :memo, priority = :isPinned, isDone = :isDone, updatedAt = :updatedAt
        WHERE id = :id AND isDeleted = 0
        """
    )
    suspend fun updateSchedule(
        id: Int,
        date: LocalDate,
        memo: String,
        isPinned: Boolean,
        isDone: Boolean,
        updatedAt: LocalDateTime = LocalDateTime.now(),
    )
}
