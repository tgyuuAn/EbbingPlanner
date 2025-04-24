package com.tgyuu.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import com.tgyuu.database.model.ScheduleEntity
import com.tgyuu.domain.model.TodoSchedule

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

    @Delete
    suspend fun deleteSchedules(schedule: ScheduleEntity)
}
