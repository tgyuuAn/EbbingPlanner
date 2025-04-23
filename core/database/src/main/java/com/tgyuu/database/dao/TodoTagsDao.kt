package com.tgyuu.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tgyuu.database.model.tag.TodoTagEntity

@Dao
interface TodoTagsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTags(vararg tags: TodoTagEntity)

    @Delete
    suspend fun deleteTags(vararg tags: TodoTagEntity)

    @Query(value = "SELECT * FROM todo_tag")
    suspend fun getTags(): List<TodoTagEntity>

    @Query(value = "SELECT * FROM todo_tag WHERE id = :id")
    suspend fun getTag(id: Int): TodoTagEntity
}
