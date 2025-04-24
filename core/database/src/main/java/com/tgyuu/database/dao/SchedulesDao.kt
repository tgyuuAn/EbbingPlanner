package com.tgyuu.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.tgyuu.database.model.TodoTagEntity

@Dao
interface SchedulesDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTag(tag: TodoTagEntity)
}
