package com.tgyuu.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.OnConflictStrategy
import androidx.room.Update
import com.tgyuu.database.model.ScheduleEntity

@Dao
interface SchedulesDao {
    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateSchedules(schedules: List<ScheduleEntity>)

    @Delete
    suspend fun deleteSchedules(schedules: List<ScheduleEntity>)
}
