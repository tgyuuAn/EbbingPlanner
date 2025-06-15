package com.tgyuu.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.tgyuu.database.model.TodoTagEntity
import com.tgyuu.domain.model.sync.TodoTagForSync
import java.time.LocalDateTime

@Dao
interface TodoTagsDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTag(tag: TodoTagEntity): Long

    @Query("UPDATE todo_info SET tagId = 1, updatedAt = :updatedAt WHERE tagId = :tagId")
    suspend fun resetTagId(tagId: Int, updatedAt: LocalDateTime)

    @Query("UPDATE todo_tag SET isDeleted = 1, updatedAt = :updatedAt WHERE id = :tagId")
    suspend fun softDeleteTag(tagId: Int, updatedAt: LocalDateTime)

    @Query("DELETE FROM todo_tag WHERE id = :id")
    suspend fun hardDeleteTag(id: Int)

    @Query("DELETE FROM todo_tag WHERE isDeleted = 1")
    suspend fun hardDeleteAllTags()

    @Transaction
    suspend fun softDeleteTagWithReset(todoTag: TodoTagEntity) {
        val now = LocalDateTime.now()
        resetTagId(todoTag.id, now)
        softDeleteTag(todoTag.id, now)
    }

    @Query(
        """
        UPDATE todo_tag
        SET name = :name, color = :color, updatedAt = :updatedAt
        WHERE id = :id AND isDeleted = 0
        """
    )
    suspend fun updateTag(
        id: Int,
        name: String,
        color: Int,
        updatedAt: LocalDateTime,
    )

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateTag(tagEntity: TodoTagEntity)

    @Query(value = "SELECT * FROM todo_tag WHERE isDeleted = 0")
    suspend fun getTags(): List<TodoTagEntity>

    @Query(value = "SELECT * FROM todo_tag WHERE id = :id AND isDeleted = 0")
    suspend fun getTag(id: Int): TodoTagEntity?

    @Query(value = "SELECT * FROM todo_tag WHERE updatedAt > :lastSyncTime")
    suspend fun getTagsForSync(lastSyncTime: LocalDateTime): List<TodoTagForSync>
}
