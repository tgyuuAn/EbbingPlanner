package com.tgyuu.shared.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import com.tgyuu.shared.database.converter.EbbingConverters
import com.tgyuu.shared.database.dao.RepeatCyclesDao
import com.tgyuu.shared.database.dao.SyncDao
import com.tgyuu.shared.database.dao.TodoSchedulesDao
import com.tgyuu.shared.database.dao.TodoTagsDao
import com.tgyuu.shared.database.dao.TodoWithSchedulesDao
import com.tgyuu.shared.database.model.RepeatCycleEntity
import com.tgyuu.shared.database.model.TodoInfoEntity
import com.tgyuu.shared.database.model.TodoScheduleEntity
import com.tgyuu.shared.database.model.TodoTagEntity

@Database(
    entities = [
        TodoTagEntity::class,
        TodoScheduleEntity::class,
        TodoInfoEntity::class,
        RepeatCycleEntity::class,
    ],
    version = 4,
)
@TypeConverters(EbbingConverters::class)
@ConstructedBy(EbbingDatabaseConstructor::class)
abstract class EbbingDatabase : RoomDatabase() {
    abstract fun todoTagsDao(): TodoTagsDao
    abstract fun schedulesDao(): TodoSchedulesDao
    abstract fun todoWithSchedulesDao(): TodoWithSchedulesDao
    abstract fun repeatCyclesDao(): RepeatCyclesDao
    abstract fun syncDao(): SyncDao

    companion object {
        const val NAME = "ebbing-database"
    }
}

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object EbbingDatabaseConstructor : RoomDatabaseConstructor<EbbingDatabase> {
    override fun initialize(): EbbingDatabase
}
