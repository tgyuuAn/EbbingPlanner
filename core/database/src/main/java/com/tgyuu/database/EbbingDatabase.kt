package com.tgyuu.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tgyuu.database.converter.EbbingConverters
import com.tgyuu.database.dao.RepeatCyclesDao
import com.tgyuu.database.dao.SchedulesDao
import com.tgyuu.database.dao.TodoTagsDao
import com.tgyuu.database.dao.TodoWithSchedulesDao
import com.tgyuu.database.model.RepeatCycleEntity
import com.tgyuu.database.model.ScheduleEntity
import com.tgyuu.database.model.TodoInfoEntity
import com.tgyuu.database.model.TodoTagEntity

@Database(
    entities = [
        TodoTagEntity::class,
        ScheduleEntity::class,
        TodoInfoEntity::class,
        RepeatCycleEntity::class,
    ],
    version = 3,
)
@TypeConverters(EbbingConverters::class)
internal abstract class EbbingDatabase : RoomDatabase() {
    abstract fun todoTagsDao(): TodoTagsDao
    abstract fun schedulesDao(): SchedulesDao
    abstract fun todoWithSchedulesDao(): TodoWithSchedulesDao
    abstract fun repeatCyclesDao(): RepeatCyclesDao

    companion object {
        internal const val NAME = "ebbing-database"
    }
}
