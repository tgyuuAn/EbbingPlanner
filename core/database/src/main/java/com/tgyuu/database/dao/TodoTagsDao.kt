package com.tgyuu.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.tgyuu.database.model.TodoTagEntity

@Dao
interface TodoTagsDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTag(tag: TodoTagEntity): Long

    @Delete
    suspend fun deleteTag(tag: TodoTagEntity)

    @Query("UPDATE todo_info SET tagId = 1 WHERE tagId = :tagId")
    suspend fun resetTagId(tagId: Int)

    @Transaction
    suspend fun deleteTagWithReset(todoTag: TodoTagEntity) {
        resetTagId(todoTag.id)
        deleteTag(todoTag)
    }

    @Update
    suspend fun updateTag(tag: TodoTagEntity)

    @Query(value = "SELECT * FROM todo_tag")
    suspend fun getTags(): List<TodoTagEntity>

    @Query(value = "SELECT * FROM todo_tag WHERE id = :id")
    suspend fun getTag(id: Int): TodoTagEntity
}
