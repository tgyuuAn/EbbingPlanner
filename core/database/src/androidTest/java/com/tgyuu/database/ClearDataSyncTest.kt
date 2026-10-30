package com.tgyuu.database

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.tgyuu.database.model.RepeatCycleEntity
import com.tgyuu.database.model.TodoInfoEntity
import com.tgyuu.database.model.TodoScheduleEntity
import com.tgyuu.database.model.TodoTagEntity
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import java.time.LocalDateTime

@RunWith(AndroidJUnit4::class)
class ClearDataSyncTest {
    private lateinit var database: EbbingDatabase
    private val lastSyncTime: LocalDateTime = LocalDateTime.now().minusMinutes(1)
    private val beforeSync: LocalDateTime = lastSyncTime.minusMinutes(1)

    @Before
    fun setUp() = runTest {
        database = Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getInstrumentation().targetContext,
            EbbingDatabase::class.java,
        ).build()

        // 모두 마지막 동기화 이전에 만들어져 이미 서버에 올라간 데이터
        database.todoTagsDao().insertTag(
            TodoTagEntity(id = DEFAULT_TAG_ID, name = "기본", color = 0, updatedAt = beforeSync),
        )
        database.todoTagsDao().insertTag(
            TodoTagEntity(id = TAG_ID, name = "태그", color = 0, updatedAt = beforeSync),
        )
        database.schedulesDao().insertTodoInfo(
            TodoInfoEntity(id = INFO_ID, title = "할 일", tagId = TAG_ID, updatedAt = beforeSync),
        )
        database.schedulesDao().insertTodoSchedule(
            TodoScheduleEntity(
                id = SCHEDULE_ID,
                infoId = INFO_ID,
                date = LocalDate.now(),
                memo = "",
                updatedAt = beforeSync,
            ),
        )
        database.repeatCyclesDao().insertRepeatCycle(
            RepeatCycleEntity(id = REPEAT_CYCLE_ID, intervals = listOf(0, 1), updatedAt = beforeSync),
        )
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun 초기화하면_화면에서는_사라지고_삭제가_다음_업로드_대상에_포함된다() = runTest {
        database.todoWithSchedulesDao().softDeleteAllData()

        assertTrue(database.schedulesDao().loadAllTodoSchedules().isEmpty())

        val schedules = database.schedulesDao().loadAllSchedulesForSync(lastSyncTime)
        assertEquals(listOf(SCHEDULE_ID to true), schedules.map { it.id to it.isDeleted })

        val tags = database.todoTagsDao().getTagsForSync(lastSyncTime)
        assertEquals(listOf(TAG_ID to true), tags.map { it.id to it.isDeleted })

        val repeatCycles = database.repeatCyclesDao().getRepeatCyclesForSync(lastSyncTime)
        assertEquals(listOf(REPEAT_CYCLE_ID to true), repeatCycles.map { it.id to it.isDeleted })

        // info는 지우지 않고, 삭제된 태그 대신 기본 태그를 가리키도록 갱신되어 함께 업로드된다
        val infos = database.schedulesDao().loadAllTodoInfosForSync(lastSyncTime)
        assertEquals(listOf(INFO_ID to DEFAULT_TAG_ID), infos.map { it.id to it.tagId })
    }

    @Test
    fun 초기화_후_업로드_정리_단계가_FK_위반_없이_동작한다() = runTest {
        database.todoWithSchedulesDao().softDeleteAllData()

        database.schedulesDao().hardDeleteAllSchedule()
        database.todoTagsDao().hardDeleteAllTags()
        database.repeatCyclesDao().hardDeleteAllRepeatCycles()

        assertTrue(database.schedulesDao().loadAllSchedulesForSync(beforeSync.minusDays(1)).isEmpty())
        assertTrue(database.repeatCyclesDao().getRepeatCyclesForSync(beforeSync.minusDays(1)).isEmpty())
        assertEquals(
            listOf(DEFAULT_TAG_ID),
            database.todoTagsDao().getTagsForSync(beforeSync.minusDays(1)).map { it.id },
        )
    }

    private companion object {
        const val DEFAULT_TAG_ID = 1
        const val TAG_ID = 5
        const val INFO_ID = 10
        const val SCHEDULE_ID = 100
        const val REPEAT_CYCLE_ID = 3
    }
}
