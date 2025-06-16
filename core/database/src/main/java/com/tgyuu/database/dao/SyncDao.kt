package com.tgyuu.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.tgyuu.database.model.RepeatCycleEntity
import com.tgyuu.database.model.TodoInfoEntity
import com.tgyuu.database.model.TodoScheduleEntity
import com.tgyuu.database.model.TodoTagEntity

@Dao
interface SyncDao {

    @Transaction
    suspend fun replaceAllData(
        infos: List<TodoInfoEntity>,
        repeatCycles: List<RepeatCycleEntity>,
        tags: List<TodoTagEntity>,
        schedules: List<TodoScheduleEntity>
    ) {
        clearTodoInfos()
        clearRepeatCycles()
        clearTags()
        clearSchedules()

        insertTags(tags)
        insertInfos(infos)
        insertRepeatCycles(repeatCycles)
        insertSchedules(schedules)
    }

    @Query("DELETE FROM todo_info")
    suspend fun clearTodoInfos()

    @Query("DELETE FROM repeat_cycle")
    suspend fun clearRepeatCycles()

    @Query("DELETE FROM todo_tag")
    suspend fun clearTags()

    @Query("DELETE FROM schedule")
    suspend fun clearSchedules()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInfos(infos: List<TodoInfoEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRepeatCycles(repeatCycles: List<RepeatCycleEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTags(tags: List<TodoTagEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedules(schedules: List<TodoScheduleEntity>)
}
