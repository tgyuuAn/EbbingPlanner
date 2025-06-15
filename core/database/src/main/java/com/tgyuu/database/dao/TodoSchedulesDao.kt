package com.tgyuu.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.tgyuu.database.model.TodoInfoEntity
import com.tgyuu.database.model.TodoScheduleEntity
import com.tgyuu.domain.model.TodoSchedule
import com.tgyuu.domain.model.sync.TodoInfoForSync
import com.tgyuu.domain.model.sync.TodoScheduleForSync
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalDateTime

@Dao
interface TodoSchedulesDao {
    @Query(
        """
        SELECT 
            s.id          AS id,
            s.infoId      AS infoId,
            i.title       AS title, 
            i.tagId       AS tagId,
            i.createdAt   AS infoCreatedAt,
            t.name        AS name,
            t.color       AS color,
            s.date        AS date,
            s.memo        AS memo,
            s.priority    AS priority,
            s.isDone      AS isDone,
            s.createdAt   AS createdAt
        FROM schedule s
        JOIN todo_info  i ON s.infoId = i.id
        JOIN todo_tag   t ON i.tagId  = t.id
        WHERE s.isDeleted = 0 AND t.isDeleted = 0
        ORDER BY s.date
        """
    )
    suspend fun loadAllTodoSchedulesWithInfoAndTag(): List<TodoSchedule>

    @Query(
        """
        SELECT 
            s.id          AS id,
            s.infoId      AS infoId,
            i.title       AS title, 
            i.tagId       AS tagId,
            i.createdAt   AS infoCreatedAt,
            t.name        AS name,
            t.color       AS color,
            s.date        AS date,
            s.memo        AS memo,
            s.priority    AS priority,
            s.isDone      AS isDone,
            s.createdAt   AS createdAt
        FROM schedule s
        JOIN todo_info  i ON s.infoId = i.id
        JOIN todo_tag   t ON i.tagId  = t.id
        WHERE s.id = :id
          AND s.isDeleted = 0 AND t.isDeleted = 0
        """
    )
    suspend fun loadTodoScheduleWithInfoAndTag(id: Int): TodoSchedule?

    @Query(
        """
        SELECT 
            s.id          AS id,
            s.infoId      AS infoId,
            i.title       AS title, 
            i.tagId       AS tagId,
            i.createdAt   AS infoCreatedAt,
            t.name        AS name,
            t.color       AS color,
            s.date        AS date,
            s.memo        AS memo,
            s.priority    AS priority,
            s.isDone      AS isDone,
            s.createdAt   AS createdAt
        FROM schedule s
        JOIN todo_info  i ON s.infoId = i.id
        JOIN todo_tag   t ON i.tagId  = t.id
        WHERE s.infoId = :id
          AND s.isDeleted = 0 AND t.isDeleted = 0
        ORDER BY s.date
        """
    )
    suspend fun loadTodoScheduleWithInfoAndTagByInfoId(id: Int): List<TodoSchedule>

    @Query(
        """
        SELECT 
            s.id          AS id,
            s.infoId      AS infoId,
            i.title       AS title, 
            i.tagId       AS tagId,
            i.createdAt   AS infoCreatedAt,
            t.name        AS name,
            t.color       AS color,
            s.date        AS date,
            s.memo        AS memo,
            s.priority    AS priority,
            s.isDone      AS isDone,
            s.createdAt   AS createdAt
        FROM schedule s
        JOIN todo_info  i ON s.infoId = i.id
        JOIN todo_tag   t ON i.tagId  = t.id
        WHERE s.date = :date
          AND s.isDeleted = 0 AND t.isDeleted = 0
        ORDER BY s.date
        """
    )
    suspend fun loadTodoScheduleWithInfoAndTagByDate(date: LocalDate): List<TodoSchedule>

    @Query(
        """
        SELECT 
            s.id          AS id,
            s.infoId      AS infoId,
            i.title       AS title, 
            i.tagId       AS tagId,
            i.createdAt   AS infoCreatedAt,
            t.name        AS name,
            t.color       AS color,
            s.date        AS date,
            s.memo        AS memo,
            s.priority    AS priority,
            s.isDone      AS isDone,
            s.createdAt   AS createdAt
        FROM schedule s
        JOIN todo_info  i ON s.infoId = i.id
        JOIN todo_tag   t ON i.tagId  = t.id
        WHERE s.date >= :date
          AND s.isDeleted = 0 AND t.isDeleted = 0
        ORDER BY s.date               
        """
    )
    suspend fun loadUpcomingTodoSchedules(date: LocalDate): List<TodoSchedule>

    @Query("SELECT * FROM schedule WHERE id = :id")
    suspend fun loadTodoScheduleEntity(id: Int): TodoScheduleEntity?

    @Query("UPDATE schedule SET isDeleted = 1, updatedAt = :updatedAt WHERE id = :id")
    suspend fun softDeleteSchedule(id: Int, updatedAt: LocalDateTime = LocalDateTime.now())

    @Query("DELETE FROM schedule WHERE id = :id")
    suspend fun hardDeleteSchedule(id: Int)

    @Query("DELETE FROM schedule WHERE isDeleted = 1")
    suspend fun hardDeleteAllSchedule()

    @Query(
        """
        SELECT 
            s.id          AS id,
            s.infoId      AS infoId,
            i.title       AS title, 
            i.tagId       AS tagId,
            i.createdAt   AS infoCreatedAt,
            t.name        AS name,
            t.color       AS color,
            s.date        AS date,
            s.memo        AS memo,
            s.priority    AS priority,
            s.isDone      AS isDone,
            s.createdAt   AS createdAt
        FROM schedule s
        JOIN todo_info  i ON s.infoId = i.id
        JOIN todo_tag   t ON i.tagId  = t.id
        WHERE s.date = :date
          AND s.isDeleted = 0 AND t.isDeleted = 0
        ORDER BY s.date
        """
    )
    fun subscribeTodoScheduleWithInfoAndTagByDate(date: LocalDate): Flow<List<TodoSchedule>>

    @Query(
        """
        SELECT 
            s.id          AS id,
            s.infoId      AS infoId,
            i.createdAt   AS infoCreatedAt,
            s.date        AS date,
            s.memo        AS memo,
            s.priority    AS priority,
            s.isDone      AS isDone,
            s.createdAt   AS createdAt,
            s.updatedAt   AS updatedAt,
            s.isDeleted   AS isDeleted
        FROM schedule s
        JOIN todo_info  i ON s.infoId = i.id
        JOIN todo_tag   t ON i.tagId  = t.id
        WHERE s.updatedAt > :lastSyncTime
        """
    )
    suspend fun loadAllSchedulesForSync(lastSyncTime: LocalDateTime): List<TodoScheduleForSync>

    @Query(
        """
        SELECT 
            i.id        AS id,
            i.title     AS title,
            i.tagId     AS tagId,
            i.createdAt AS createdAt,
            i.updatedAt AS updatedAt
        FROM todo_info i
        WHERE i.updatedAt > :lastSyncTime
        """
    )
    suspend fun loadAllTodoInfosForSync(lastSyncTime: LocalDateTime): List<TodoInfoForSync>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTodoSchedule(todoScheduleEntity: TodoScheduleEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTodoInfo(todoInfoEntity: TodoInfoEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateTodoSchedule(todoScheduleEntity: TodoScheduleEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateTodoInfo(todoInfoEntity: TodoInfoEntity)

}
