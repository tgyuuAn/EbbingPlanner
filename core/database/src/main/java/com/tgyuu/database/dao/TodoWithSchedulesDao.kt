package com.tgyuu.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Transaction
import androidx.room.Update
import com.tgyuu.database.model.ScheduleEntity
import com.tgyuu.database.model.TodoInfoEntity
import com.tgyuu.database.model.toEntity
import com.tgyuu.database.model.toInfoEntity
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
    ) {
        val infoId = insertInfo(
            TodoInfoEntity(
                title = title,
                tagId = tagId,
                priority = priority ?: 0,
            )
        ).toInt()

        insertSchedules(
            dates.map { date ->
                ScheduleEntity(
                    infoId = infoId,
                    date = date,
                    memo = "",
                    priority = priority ?: 0,
                )
            }
        )
    }

    @Update
    fun updateInfo(info: TodoInfoEntity)

    @Update
    fun updateSchedule(schedule: ScheduleEntity)

    @Transaction
    suspend fun updateTodoSchedules(todo: TodoSchedule) {
        updateInfo(todo.toInfoEntity())
        updateSchedule(todo.toEntity())
    }
}
