package com.tgyuu.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tgyuu.database.converter.EbbingConverters
import com.tgyuu.database.dao.LinkedDevicesDao
import com.tgyuu.database.dao.RepeatCyclesDao
import com.tgyuu.database.dao.TodoSchedulesDao
import com.tgyuu.database.dao.TodoTagsDao
import com.tgyuu.database.dao.TodoWithSchedulesDao
import com.tgyuu.database.model.LinkedDeviceEntity
import com.tgyuu.database.model.RepeatCycleEntity
import com.tgyuu.database.model.TodoScheduleEntity
import com.tgyuu.database.model.TodoInfoEntity
import com.tgyuu.database.model.TodoTagEntity

@Database(
    entities = [
        TodoTagEntity::class,
        TodoScheduleEntity::class,
        TodoInfoEntity::class,
        RepeatCycleEntity::class,
        LinkedDeviceEntity::class,
    ],
    version = 3,
)
@TypeConverters(EbbingConverters::class)
internal abstract class EbbingDatabase : RoomDatabase() {
    abstract fun todoTagsDao(): TodoTagsDao
    abstract fun schedulesDao(): TodoSchedulesDao
    abstract fun todoWithSchedulesDao(): TodoWithSchedulesDao
    abstract fun repeatCyclesDao(): RepeatCyclesDao
    abstract fun linkedDevicesDao(): LinkedDevicesDao

    companion object {
        internal const val NAME = "ebbing-database"
    }
}
