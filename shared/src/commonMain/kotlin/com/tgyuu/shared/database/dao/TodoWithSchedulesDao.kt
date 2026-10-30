package com.tgyuu.shared.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.tgyuu.shared.common.now
import com.tgyuu.shared.database.model.TodoInfoEntity
import com.tgyuu.shared.database.model.TodoScheduleEntity
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

@Dao
interface TodoWithSchedulesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInfo(entity: TodoInfoEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedules(schedules: List<TodoScheduleEntity>)

    @Transaction
    suspend fun insertTodoWithSchedules(
        title: String,
        tagId: Int,
        dates: List<LocalDate>,
        isPinned: Boolean,
        restDays: Set<DayOfWeek> = emptySet(),
    ) {
        val infoId = insertInfo(
            TodoInfoEntity(
                title = title,
                tagId = tagId,
                restDays = restDays.joinToString(",") { (it.ordinal + 1).toString() },
            )
        ).toInt()

        insertSchedules(
            dates.map { date ->
                TodoScheduleEntity(
                    infoId = infoId,
                    date = date,
                    memo = "",
                    isPinned = isPinned,
                )
            }
        )
    }

    @Transaction
    suspend fun insertTodoWithSchedules(
        title: String,
        tagId: Int,
        dates: List<LocalDate>,
        isDoneSchedules: List<Boolean>,
        isPinned: Boolean,
        restDays: Set<DayOfWeek> = emptySet(),
    ) {
        val infoId = insertInfo(
            TodoInfoEntity(
                title = title,
                tagId = tagId,
                restDays = restDays.joinToString(",") { (it.ordinal + 1).toString() },
            )
        ).toInt()

        insertSchedules(
            dates.zip(isDoneSchedules) { date, isDone ->
                TodoScheduleEntity(
                    infoId = infoId,
                    date = date,
                    isDone = isDone,
                    memo = "",
                    isPinned = isPinned,
                )
            }
        )
    }

    @Query(
        """
        UPDATE todo_info
        SET title = :title, tagId = :tagId, rest_days = :restDays, updatedAt = :updatedAt
        WHERE id = :id
        """
    )
    suspend fun updateInfo(
        id: Int,
        title: String,
        tagId: Int,
        restDays: String = "",
        updatedAt: LocalDateTime = LocalDateTime.now(),
    )

    @Query(
        """
        UPDATE schedule
        SET date = :date, memo = :memo, priority = :isPinned, isDone = :isDone, updatedAt = :updatedAt
        WHERE id = :id AND isDeleted = 0
        """
    )
    suspend fun updateSchedule(
        id: Int,
        date: LocalDate,
        memo: String,
        isPinned: Boolean,
        isDone: Boolean,
        updatedAt: LocalDateTime = LocalDateTime.now(),
    )

    @Transaction
    suspend fun updateSchedules(schedules: List<ScheduleUpdateParams>) {
        // Android TodoWithSchedulesDao.updateSchedules와 동일: 한 트랜잭션 + 동일 updatedAt
        val now = LocalDateTime.now()
        schedules.forEach { params ->
            updateSchedule(
                id = params.id,
                date = params.date,
                memo = params.memo,
                isPinned = params.isPinned,
                isDone = params.isDone,
                updatedAt = now,
            )
        }
    }

    @Query(
        "UPDATE schedule SET isDeleted = 1, updatedAt = :updatedAt " +
            "WHERE infoId = :infoId AND isDeleted = 0"
    )
    suspend fun softDeleteSchedulesByInfoId(
        infoId: Int,
        updatedAt: LocalDateTime = LocalDateTime.now(),
    )

    /**
     * Android TodoWithSchedulesDao.replaceSchedules와 동일:
     * 같은 infoId를 유지한 채 기존 일정을 soft delete(updatedAt 갱신)하고 새 일정을 한 트랜잭션으로 삽입한다.
     */
    @Transaction
    suspend fun replaceSchedules(
        infoId: Int,
        title: String,
        tagId: Int,
        dates: List<LocalDate>,
        isDoneSchedules: List<Boolean>,
        isPinned: Boolean,
        restDays: Set<DayOfWeek> = emptySet(),
    ) {
        require(dates.size == isDoneSchedules.size) {
            "dates.size(${dates.size}) != isDoneSchedules.size(${isDoneSchedules.size})"
        }

        softDeleteSchedulesByInfoId(infoId)

        updateInfo(
            id = infoId,
            title = title,
            tagId = tagId,
            restDays = restDays.joinToString(",") { (it.ordinal + 1).toString() },
        )

        insertSchedules(
            dates.zip(isDoneSchedules) { date, isDone ->
                TodoScheduleEntity(
                    infoId = infoId,
                    date = date,
                    isDone = isDone,
                    memo = "",
                    isPinned = isPinned,
                )
            }
        )
    }
}

data class ScheduleUpdateParams(
    val id: Int,
    val date: LocalDate,
    val memo: String,
    val isPinned: Boolean,
    val isDone: Boolean,
)
