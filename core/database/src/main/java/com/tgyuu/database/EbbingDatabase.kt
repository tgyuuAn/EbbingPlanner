package com.tgyuu.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tgyuu.database.converter.EbbingConverters
import com.tgyuu.database.dao.TodoTagsDao
import com.tgyuu.database.model.tag.TodoTagEntity

@Database(
    entities = [TodoTagEntity::class, ],
    version = 1,
)
@TypeConverters(EbbingConverters::class)
internal abstract class EbbingDatabase : RoomDatabase() {
    abstract fun todoTagsDao(): TodoTagsDao

    companion object {
        internal const val NAME = "ebbing-database"
    }
}
