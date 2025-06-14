package com.tgyuu.database

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class MigrationTest {

    private val testDbName = "test-ebbing-db"

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        EbbingDatabase::class.java.canonicalName!!,
        FrameworkSQLiteOpenHelperFactory()
    )

    @After
    fun tearDown() {
        InstrumentationRegistry.getInstrumentation()
            .targetContext.deleteDatabase(testDbName)
    }

    @Test
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

        // then
        helper.runMigrationsAndValidate(
            name = testDbName,
            version = 2,
            validateDroppedTables = true,
            DatabaseMigrations.MIGRATION_1_TO_2
        )
    }

    @Test
    fun `마이그레이션_2에서3`() {
        helper.createDatabase(testDbName, 2).apply {
            execSQL(
                """
                INSERT INTO schedule (id, infoId, date, memo, priority, isDone, createdAt)
                VALUES (1, 100, '2024-06-01', '진료 예약', 1, 0, '2024-06-01')
                """.trimIndent()
            )
            close()
        }

        helper.runMigrationsAndValidate(
            name = testDbName,
            version = 3,
            validateDroppedTables = true,
            DatabaseMigrations.MIGRATION_2_TO_3
        )
    }

    @Test
    fun `마이그레이션_1에서3_전체`() {
        helper.createDatabase(testDbName, 1).close()

        helper.runMigrationsAndValidate(
            name = testDbName,
            version = 3,
            validateDroppedTables = true,
            DatabaseMigrations.MIGRATION_1_TO_2,
            DatabaseMigrations.MIGRATION_2_TO_3
        )
    }
}
