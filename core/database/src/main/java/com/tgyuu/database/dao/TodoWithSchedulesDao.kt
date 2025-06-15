package com.tgyuu.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.tgyuu.database.model.TodoScheduleEntity
import com.tgyuu.database.model.TodoInfoEntity
import java.time.LocalDate
import java.time.LocalDateTime

@Dao
interface TodoWithSchedulesDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertInfo(entity: TodoInfoEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
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
