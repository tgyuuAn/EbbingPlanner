package com.tgyuu.shared.database

import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL

object DatabaseMigrations {
    /**
     * Android core/database의 MIGRATION_4_TO_5 미러링.
     * priority(Int 순위) → isPinned(Boolean 상단 고정)으로 의미 전환.
     * 컬럼명/타입(INTEGER)은 유지하고 값만 0/1로 정규화한다 (0 초과 = 고정).
     */
    val MIGRATION_4_TO_5 = object : Migration(4, 5) {
        override fun migrate(connection: SQLiteConnection) {
            connection.execSQL(
                "UPDATE schedule SET priority = CASE WHEN priority > 0 THEN 1 ELSE 0 END"
            )
        }
    }
}
