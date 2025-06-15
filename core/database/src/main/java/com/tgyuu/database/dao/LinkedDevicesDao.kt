package com.tgyuu.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.tgyuu.database.model.LinkedDeviceEntity

@Dao
interface LinkedDevicesDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertLinkedDevice(linkedDevice: LinkedDeviceEntity): Long

    @Delete
    suspend fun deleteLinkedDevice(linkedDevice: LinkedDeviceEntity)
}
