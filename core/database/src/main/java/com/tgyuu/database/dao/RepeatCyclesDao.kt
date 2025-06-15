package com.tgyuu.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.tgyuu.database.model.RepeatCycleEntity
import java.time.LocalDateTime

@Dao
interface RepeatCyclesDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertRepeatCycle(repeatCycle: RepeatCycleEntity): Long

    @Query("UPDATE repeat_cycle SET isDeleted = 1, updatedAt = :updatedAt WHERE id = :id")
    suspend fun softDeleteRepeatCycle(id: Int, updatedAt: LocalDateTime = LocalDateTime.now())

    @Query("DELETE FROM repeat_cycle WHERE id = :id")
    suspend fun hardDeleteRepeatCycle(id: Int)

    @Query("DELETE FROM repeat_cycle WHERE isDeleted = 1")
    suspend fun hardDeleteAllRepeatCycles()

    @Query(
        """
        UPDATE repeat_cycle 
        SET intervals = :intervalsJson, updatedAt = :updatedAt
        WHERE id = :id AND isDeleted = 0
    """
    )
    suspend fun updateRepeatCycle(
        id: Int,
        intervalsJson: String,
        updatedAt: LocalDateTime = LocalDateTime.now()
    )

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateRepeatCycle(repeatCycle: RepeatCycleEntity)

    @Query(value = "SELECT * FROM repeat_cycle WHERE isDeleted = 0")
    suspend fun getRepeatCycles(): List<RepeatCycleEntity>

    @Query(value = "SELECT * FROM repeat_cycle WHERE id = :id AND isDeleted = 0")
    suspend fun getRepeatCycle(id: Int): RepeatCycleEntity?

    @Query(value = "SELECT * FROM repeat_cycle WHERE updatedAt > :lastSyncTime")
    suspend fun getRepeatCyclesForSync(lastSyncTime: LocalDateTime): List<RepeatCycleEntity>
}
