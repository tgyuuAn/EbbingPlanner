package com.tgyuu.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.tgyuu.database.model.TodoTagEntity

@Dao
interface TodoInfosDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTodoInfo(tag: TodoTagEntity)
}
