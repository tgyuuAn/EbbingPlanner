package com.tgyuu.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tgyuu.database.model.RepeatCycleEntity

@Dao
interface RepeatCyclesDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertRepeatCycle(repeatCycle: RepeatCycleEntity): Long

    @Query("UPDATE repeat_cycle SET isDeleted = 1, isSynced = 0 WHERE id = :id")
    suspend fun softDeleteRepeatCycle(id: Int)

    @Delete
    suspend fun hardDeleteRepeatCycle(repeatCycle: RepeatCycleEntity)

    @Query(
        """
        UPDATE repeat_cycle 
        SET intervals = :intervalsJson, isSynced = 0 
        WHERE id = :id AND isDeleted = 0
    """
    )
    suspend fun updateRepeatCycle(id: Int, intervalsJson: String)

    @Query(value = "SELECT * FROM repeat_cycle WHERE isDeleted = 0")
    suspend fun getRepeatCycles(): List<RepeatCycleEntity>

    @Query(value = "SELECT * FROM repeat_cycle WHERE id = :id AND isDeleted = 0")
    suspend fun getRepeatCycle(id: Int): RepeatCycleEntity
}
