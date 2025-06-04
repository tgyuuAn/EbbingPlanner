package com.tgyuu.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.tgyuu.database.model.RepeatCycleEntity

@Dao
interface RepeatCyclesDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertRepeatCycle(repeatCycle: RepeatCycleEntity): Long

    @Delete
    suspend fun deleteRepeatCycle(repeatCycle: RepeatCycleEntity)

    @Update
    suspend fun updateRepeatCycle(repeatCycle: RepeatCycleEntity)

    @Query(value = "SELECT * FROM repeat_cycle")
    suspend fun getRepeatCycles(): List<RepeatCycleEntity>

    @Query(value = "SELECT * FROM repeat_cycle WHERE id = :id")
    suspend fun getRepeatCycle(id: Int): RepeatCycleEntity
}
