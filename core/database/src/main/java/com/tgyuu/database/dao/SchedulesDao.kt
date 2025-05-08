package com.tgyuu.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import com.tgyuu.database.model.ScheduleEntity
import com.tgyuu.domain.model.TodoSchedule
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

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
        ORDER BY s.date               
        """
    )
    suspend fun loadUpcomingSchedules(date: LocalDate): List<TodoSchedule>

    @Delete
    suspend fun deleteSchedules(schedule: ScheduleEntity)

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
        ORDER BY s.date
        """
    )
    fun subscribeScheduleWithInfoAndTagByDate(date: LocalDate): Flow<List<TodoSchedule>>
}
