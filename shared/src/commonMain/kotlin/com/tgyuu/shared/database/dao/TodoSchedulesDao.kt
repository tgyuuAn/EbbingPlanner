package com.tgyuu.shared.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.tgyuu.shared.common.now
import com.tgyuu.shared.database.model.TodoInfoEntity
import com.tgyuu.shared.database.model.TodoScheduleEntity
import com.tgyuu.shared.domain.model.TodoInfo
import com.tgyuu.shared.domain.model.TodoSchedule
import com.tgyuu.shared.domain.model.sync.TodoInfoForSync
import com.tgyuu.shared.domain.model.sync.TodoScheduleForSync
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

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
    suspend fun loadAllTodoSchedules(): List<TodoSchedule>

    @Query("SELECT * FROM schedule WHERE id = :id")
    suspend fun loadTodoScheduleEntity(id: Int): TodoScheduleEntity?

    @Query("UPDATE schedule SET isDeleted = 1, updatedAt = :updatedAt WHERE id = :id")
    suspend fun softDeleteSchedule(id: Int, updatedAt: LocalDateTime = LocalDateTime.now())

    @Query("UPDATE schedule SET isDeleted = 1, updatedAt = :updatedAt WHERE infoId = :id")
    suspend fun softDeleteScheduleByTodoInfo(
        id: Int,
        updatedAt: LocalDateTime = LocalDateTime.now()
    )

    @Query("UPDATE schedule  SET isDeleted = 1, updatedAt = :updatedAt")
    suspend fun softDeleteAllSchedules(updatedAt: LocalDateTime = LocalDateTime.now())

    @Query("DELETE FROM schedule WHERE id = :id")
    suspend fun hardDeleteSchedule(id: Int)

    @Query("DELETE FROM schedule WHERE isDeleted = 1")
    suspend fun hardDeleteAllSchedule()

    @Query("DELETE FROM todo_info")
    suspend fun hardDeleteAllTodoInfos()

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
            i.updatedAt AS updatedAt,
            i.rest_days AS restDays
        FROM todo_info i
        WHERE i.updatedAt > :lastSyncTime
        """
    )
    suspend fun loadAllTodoInfosForSync(lastSyncTime: LocalDateTime): List<TodoInfoForSync>

    @Query(
        """
    SELECT
        id,
        title,
        tagId,
        rest_days AS restDays
    FROM todo_info
    WHERE tagId = :tagId
    """
    )
    suspend fun loadTodoInfoByTagId(tagId: Int): List<TodoInfo>

    @Query(
        """
    SELECT
        id,
        title,
        tagId,
        rest_days AS restDays
    FROM todo_info
    WHERE id = :infoId
    """
    )
    suspend fun getTodoInfoById(infoId: Int): TodoInfo

    @Query("SELECT * FROM todo_info WHERE id = :id")
    suspend fun loadTodoInfoEntity(id: Int): TodoInfoEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTodoSchedule(todoScheduleEntity: TodoScheduleEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTodoInfo(todoInfoEntity: TodoInfoEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateTodoSchedule(todoScheduleEntity: TodoScheduleEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateTodoInfo(todoInfoEntity: TodoInfoEntity)

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
    WHERE s.infoId IN (
        SELECT DISTINCT infoId
        FROM schedule
        WHERE date BETWEEN :startDate AND :endDate
        AND isDeleted = 0
    )
    AND s.isDeleted = 0
    AND t.isDeleted = 0
    ORDER BY s.date
    """
    )
    suspend fun loadTodoSchedulesByDateRange(
        startDate: LocalDate,
        endDate: LocalDate
    ): List<TodoSchedule>
}
