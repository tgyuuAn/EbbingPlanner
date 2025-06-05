package com.tgyuu.database

import androidx.room.Room
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class AutoMigrationTest {

    private val testDbName = "test-ebbing-db"

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        EbbingDatabase::class.java.canonicalName!!,
        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    @Throws(IOException::class)
    fun `마이그레이션_1에서2_테스트`() {
        // given
        helper.createDatabase(testDbName, 1).apply {
            execSQL(
                """
                INSERT INTO todo_tag (id, name, color, createdAt) 
                VALUES (1, '운동', 123456, '2024-06-01')
                """.trimIndent()
            )
            close()
        }

        // when
        val migratedDb = Room.databaseBuilder(
            ApplicationProvider.getApplicationContext(),
            EbbingDatabase::class.java,
            testDbName
        ).addMigrations()
            .build()

        // then
        migratedDb.openHelper.writableDatabase.query("SELECT * FROM todo_tag").use { cursor ->
            assertTrue(cursor.moveToFirst())
            assertEquals("운동", cursor.getString(cursor.getColumnIndexOrThrow("name")))
            assertEquals(123456, cursor.getInt(cursor.getColumnIndexOrThrow("color")))
            assertEquals("2024-06-01", cursor.getString(cursor.getColumnIndexOrThrow("createdAt")))
        }
        migratedDb.close()
    }
}
