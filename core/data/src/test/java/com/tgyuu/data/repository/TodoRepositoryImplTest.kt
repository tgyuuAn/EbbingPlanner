package com.tgyuu.data.repository

import com.tgyuu.database.source.repeatcycle.LocalRepeatCycleDataSource
import com.tgyuu.database.source.tag.LocalTagDataSource
import com.tgyuu.database.source.todo.LocalTodoDataSource
import com.tgyuu.datastore.datasource.user.LocalUserConfigDataSource
import com.tgyuu.domain.model.RepeatCycle
import com.tgyuu.domain.model.TodoTag
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TodoRepositoryImplTest {
    private val localTagDataSource = mockk<LocalTagDataSource>(relaxed = true)
    private val localTodoDataSource = mockk<LocalTodoDataSource>(relaxed = true)
    private val localRepeatCycleDataSource = mockk<LocalRepeatCycleDataSource>(relaxed = true)
    private val localUserConfigDataSource = mockk<LocalUserConfigDataSource>(relaxed = true)

    private lateinit var repository: TodoRepositoryImpl

    @BeforeEach
    fun setUp() {
        repository = TodoRepositoryImpl(
            localTagDataSource = localTagDataSource,
            localTodoDataSource = localTodoDataSource,
            localRepeatCycleDataSource = localRepeatCycleDataSource,
            localUserConfigDataSource = localUserConfigDataSource,
        )
    }

    @Test
    fun `전체 초기화 시 최근 사용 이력도 함께 비운다`() = runTest {
        repository.clearData()

        coVerify(exactly = 1) { localUserConfigDataSource.clearUsageOrder() }
    }

    @Test
    fun `태그 삭제 시 해당 태그를 최근 사용 이력에서 제거한다`() = runTest {
        val tag = TodoTag(id = 5, name = "T", color = 0, createdAt = LocalDate(2026, 1, 1))

        repository.deleteTag(tag)

        coVerify(exactly = 1) { localTagDataSource.softDeleteTag(tag) }
        coVerify(exactly = 1) { localUserConfigDataSource.removeTagUsage(5) }
    }

    @Test
    fun `반복 주기 삭제 시 해당 주기를 최근 사용 이력에서 제거한다`() = runTest {
        val repeatCycle = RepeatCycle(id = -2, intervals = listOf(0, 1, 7))

        repository.deleteRepeatCycle(repeatCycle)

        coVerify(exactly = 1) { localRepeatCycleDataSource.softDeleteRepeatCycle(repeatCycle) }
        coVerify(exactly = 1) { localUserConfigDataSource.removeRepeatCycleUsage(-2) }
    }
}
