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
    ) {
        val infoId = insertInfo(
            TodoInfoEntity(
                title = title,
                tagId = tagId,
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
    ) {
        val infoId = insertInfo(
            TodoInfoEntity(
                title = title,
                tagId = tagId,
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
        SET title = :title, tagId = :tagId, updatedAt = :updatedAt
        WHERE id = :id
        """
    )
    suspend fun updateInfo(
        id: Int,
        title: String,
        tagId: Int,
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
}
