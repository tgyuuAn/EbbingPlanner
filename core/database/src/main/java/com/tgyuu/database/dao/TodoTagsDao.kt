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

    @Query("UPDATE todo_info SET tagId = 1 WHERE tagId = :tagId")
    suspend fun resetTagId(tagId: Int)

    @Query("UPDATE todo_tag SET isDeleted = 1, isSynced = 0 WHERE id = :tagId")
    suspend fun softDeleteTag(tagId: Int)

    @Delete
    suspend fun hardDeleteTag(tag: TodoTagEntity)

    @Transaction
    suspend fun softDeleteTagWithReset(todoTag: TodoTagEntity) {
        resetTagId(todoTag.id)
        softDeleteTag(todoTag.id)
    }

    @Update
    suspend fun updateTag(tag: TodoTagEntity)

    @Query(value = "SELECT * FROM todo_tag WHERE isDeleted = 0")
    suspend fun getTags(): List<TodoTagEntity>

    @Query(value = "SELECT * FROM todo_tag WHERE id = :id AND isDeleted = 0")
    suspend fun getTag(id: Int): TodoTagEntity
}
