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
                // todo_info에서 priority Column 제거
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS todo_info_new (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    title TEXT NOT NULL,
                    tagId INTEGER NOT NULL,
                    createdAt TEXT NOT NULL,
                    updatedAt TEXT NOT NULL,
                    FOREIGN KEY (tagId) REFERENCES todo_tag(id) ON UPDATE NO ACTION ON DELETE NO ACTION
                    )
                    """.trimIndent()
                )

                database.execSQL(
                    """
                    INSERT INTO todo_info_new (id, title, tagId, createdAt, updatedAt)
                    SELECT id, title, tagId, createdAt, '1970-01-01T00:00:00' FROM todo_info
                    """.trimIndent()
                )

                database.execSQL("DROP TABLE todo_info")
                database.execSQL("ALTER TABLE todo_info_new RENAME TO todo_info")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_todo_info_tagId ON todo_info(tagId)")

                // isDelete, updatedAt Column 추가
                database.execSQL("ALTER TABLE schedule ADD COLUMN isDeleted INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE schedule ADD COLUMN updatedAt TEXT NOT NULL DEFAULT '1970-01-01T00:00:00'")

                database.execSQL("ALTER TABLE repeat_cycle ADD COLUMN isDeleted INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE repeat_cycle ADD COLUMN updatedAt TEXT NOT NULL DEFAULT '1970-01-01T00:00:00'")

                database.execSQL("ALTER TABLE todo_tag ADD COLUMN isDeleted INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE todo_tag ADD COLUMN updatedAt TEXT NOT NULL DEFAULT '1970-01-01T00:00:00'")
            }
        }
    }
}
