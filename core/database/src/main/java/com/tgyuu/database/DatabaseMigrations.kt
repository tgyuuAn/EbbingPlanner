package com.tgyuu.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class DatabaseMigrations {
    companion object {
        val MIGRATION_1_TO_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
            CREATE TABLE IF NOT EXISTS repeat_cycle (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                intervals TEXT NOT NULL
            )
            """.trimIndent()
                )
            }
        }

        val MIGRATION_2_TO_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE schedule ADD COLUMN isDeleted INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE schedule ADD COLUMN isSynced INTEGER NOT NULL DEFAULT 0")

                database.execSQL("ALTER TABLE todo_info ADD COLUMN isDeleted INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE todo_info ADD COLUMN isSynced INTEGER NOT NULL DEFAULT 0")

                database.execSQL("ALTER TABLE repeat_cycle ADD COLUMN isDeleted INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE repeat_cycle ADD COLUMN isSynced INTEGER NOT NULL DEFAULT 0")

                database.execSQL("ALTER TABLE todo_tag ADD COLUMN isDeleted INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE todo_tag ADD COLUMN isSynced INTEGER NOT NULL DEFAULT 0")
            }
        }
    }
}
