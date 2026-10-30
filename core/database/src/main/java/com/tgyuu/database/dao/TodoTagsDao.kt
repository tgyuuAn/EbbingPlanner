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

    @Query("UPDATE todo_tag SET isDeleted = 1, updatedAt = :updatedAt WHERE id != 1")
    suspend fun softDeleteAllTags(updatedAt: LocalDateTime)

    @Query("DELETE FROM todo_tag WHERE id = :id AND id != 1")
    suspend fun hardDeleteTagInternal(id: Int)

    @Transaction
    suspend fun hardDeleteTag(id: Int) {
        resetTagId(id, LocalDateTime.now())
        hardDeleteTagInternal(id)
    }

    @Query(
        """
        UPDATE todo_info SET tagId = 1, updatedAt = :updatedAt
        WHERE tagId IN (SELECT id FROM todo_tag WHERE isDeleted = 1 AND id != 1)
        """
    )
    suspend fun resetDeletedTagIds(updatedAt: LocalDateTime)

    @Query("DELETE FROM todo_tag WHERE isDeleted = 1 AND id != 1")
    suspend fun hardDeleteAllTagsInternal()

    // 삭제된 태그를 아직 참조하는 todo_info가 있으면 FK 위반으로 삭제가 실패하므로,
    // hardDeleteTag와 같이 기본 태그로 옮긴 뒤 지운다.
    @Transaction
    suspend fun hardDeleteAllTags() {
        resetDeletedTagIds(LocalDateTime.now())
        hardDeleteAllTagsInternal()
    }

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

    @Query("SELECT * FROM todo_tag WHERE isDeleted = 0")
    suspend fun getTags(): List<TodoTagEntity>

    @Query("SELECT * FROM todo_tag WHERE id = :id AND isDeleted = 0")
    suspend fun getTag(id: Int): TodoTagEntity?

    @Query("SELECT * FROM todo_tag WHERE updatedAt > :lastSyncTime")
    suspend fun getTagsForSync(lastSyncTime: LocalDateTime): List<TodoTagForSync>
}
