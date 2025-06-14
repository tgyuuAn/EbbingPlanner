package com.tgyuu.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.tgyuu.database.model.ScheduleEntity
import com.tgyuu.database.model.TodoInfoEntity
import com.tgyuu.domain.model.TodoSchedule
import java.time.LocalDate

@Dao
interface TodoWithSchedulesDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertInfo(entity: TodoInfoEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSchedules(schedules: List<ScheduleEntity>)

    @Transaction
    suspend fun insertTodoWithSchedules(
        title: String,
        tagId: Int,
        dates: List<LocalDate>,
        priority: Int?,
        isSynced: Boolean = false,
    ) {
        val infoId = insertInfo(
            TodoInfoEntity(
                title = title,
                tagId = tagId,
                priority = priority ?: 0,
                isSynced = isSynced,
            )
        ).toInt()

        insertSchedules(
            dates.map { date ->
                ScheduleEntity(
                    infoId = infoId,
                    date = date,
                    memo = "",
                    priority = priority ?: 0,
                    isSynced = isSynced,
                )
            }
        )
    }

    @Query(
        """
        UPDATE todo_info
        SET title = :title, tagId = :tagId, priority = :priority, isSynced = 0
        WHERE id = :id AND isDeleted = 0
        """
    )
    suspend fun updateInfo(id: Int, title: String, tagId: Int, priority: Int)

    @Query(
        """ 
        UPDATE schedule
        SET date = :date, memo = :memo, priority = :priority, isDone = :isDone, isSynced = 0
        WHERE id = :id AND isDeleted = 0
        """
    )
    suspend fun updateSchedule(
        id: Int,
        date: LocalDate,
        memo: String,
        priority: Int,
        isDone: Boolean
    )

    @Transaction
    suspend fun updateTodoSchedules(todo: TodoSchedule) {
        updateInfo(
            id = todo.infoId,
            title = todo.title,
            tagId = todo.tagId,
            priority = todo.priority
        )
        updateSchedule(
            id = todo.id,
            date = todo.date,
            memo = todo.memo,
            priority = todo.priority,
            isDone = todo.isDone
        )
    }
}
