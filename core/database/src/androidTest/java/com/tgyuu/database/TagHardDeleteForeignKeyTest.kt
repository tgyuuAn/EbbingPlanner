package com.tgyuu.database

import android.database.sqlite.SQLiteConstraintException
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.tgyuu.database.model.TodoInfoEntity
import com.tgyuu.database.model.TodoTagEntity
import com.tgyuu.database.source.sync.LocalSyncTransactionDataSourceImpl
import com.tgyuu.domain.model.sync.TodoInfoForSync
import com.tgyuu.domain.model.sync.TodoTagForSync
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import java.time.LocalDateTime

@RunWith(AndroidJUnit4::class)
class TagHardDeleteForeignKeyTest {
    private lateinit var database: EbbingDatabase

    @Before
    fun setUp() = runTest {
        database = Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getInstrumentation().targetContext,
            EbbingDatabase::class.java,
        ).build()

        database.todoTagsDao().insertTag(TodoTagEntity(id = DEFAULT_TAG_ID, name = "기본", color = 0))
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun 삭제된_태그를_참조하는_info가_있으면_기존_삭제_쿼리는_FK_위반이_난다() = runTest {
        insertDeletedTagReferencedByInfo()

        assertThrows(SQLiteConstraintException::class.java) {
            runBlocking { database.todoTagsDao().hardDeleteAllTagsInternal() }
        }
    }

    @Test
    fun hardDeleteAllTags는_참조하는_info를_기본_태그로_옮긴_뒤_삭제한다() = runTest {
        insertDeletedTagReferencedByInfo()

        database.todoTagsDao().hardDeleteAllTags()

        assertEquals(DEFAULT_TAG_ID, database.schedulesDao().loadTodoInfoEntity(INFO_ID)?.tagId)
        assertNull(database.todoTagsDao().getTag(DELETED_TAG_ID))
    }

    @Test
    fun replaceAllData는_삭제된_태그를_참조하는_info를_기본_태그로_옮겨_이후_삭제가_성공한다() = runTest {
        val now = LocalDateTime.now()
        val dataSource = LocalSyncTransactionDataSourceImpl(database.syncDao())

        dataSource.replaceAllData(
            infos = listOf(
                TodoInfoForSync(
                    id = INFO_ID,
                    title = "복원된 할 일",
                    tagId = DELETED_TAG_ID,
                    createdAt = LocalDate.now(),
                    updatedAt = now,
                ),
            ),
            tags = listOf(
                TodoTagForSync(
                    id = DELETED_TAG_ID,
                    name = "삭제된 태그",
                    color = 0,
                    createdAt = LocalDate.now(),
                    isDeleted = true,
                    updatedAt = now,
                ),
            ),
            schedules = emptyList(),
            repeatCycles = emptyList(),
        )

        assertEquals(DEFAULT_TAG_ID, database.schedulesDao().loadTodoInfoEntity(INFO_ID)?.tagId)

        database.todoTagsDao().hardDeleteAllTags()
    }

    private suspend fun insertDeletedTagReferencedByInfo() {
        database.todoTagsDao().insertTag(
            TodoTagEntity(id = DELETED_TAG_ID, name = "삭제된 태그", color = 0, isDeleted = true),
        )
        database.schedulesDao().insertTodoInfo(
            TodoInfoEntity(id = INFO_ID, title = "할 일", tagId = DELETED_TAG_ID),
        )
    }

    private companion object {
        const val DEFAULT_TAG_ID = 1
        const val DELETED_TAG_ID = 5
        const val INFO_ID = 10
    }
}
