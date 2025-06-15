package com.tgyuu.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import com.tgyuu.database.model.ScheduleEntity
import com.tgyuu.domain.model.TodoSchedule
import com.tgyuu.domain.model.sync.TodoInfoForSync
import com.tgyuu.domain.model.sync.TodoScheduleForSync
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalDateTime

@Dao
interface SchedulesDao {
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
    suspend fun loadAllSchedulesWithInfoAndTag(): List<TodoSchedule>

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
    suspend fun loadScheduleWithInfoAndTag(id: Int): TodoSchedule

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
    suspend fun loadScheduleWithInfoAndTagByInfoId(id: Int): List<TodoSchedule>

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
    suspend fun loadScheduleWithInfoAndTagByDate(date: LocalDate): List<TodoSchedule>

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
    suspend fun loadUpcomingSchedules(date: LocalDate): List<TodoSchedule>

    @Query("UPDATE schedule SET isDeleted = 1, updatedAt = :updatedAt WHERE id = :id")
    suspend fun softDeleteSchedule(id: Int, updatedAt: LocalDateTime = LocalDateTime.now())

    @Delete
    suspend fun hardDeleteSchedule(schedule: ScheduleEntity)

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
    fun subscribeScheduleWithInfoAndTagByDate(date: LocalDate): Flow<List<TodoSchedule>>

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
}
