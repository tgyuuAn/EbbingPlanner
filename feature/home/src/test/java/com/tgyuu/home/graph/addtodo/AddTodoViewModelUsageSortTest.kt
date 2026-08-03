package com.tgyuu.home.graph.addtodo

import androidx.lifecycle.SavedStateHandle
import com.tgyuu.analytics.NoOpAnalyticsHelper
import com.tgyuu.common.event.EventBus
import com.tgyuu.common.ui.resource.ResourceProvider
import com.tgyuu.home.fake.FakeConfigRepository
import com.tgyuu.home.fake.FakeTodoRepository
import com.tgyuu.navigation.NavigationBus
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AddTodoViewModelUsageSortTest {
    private lateinit var todoRepository: FakeTodoRepository
    private lateinit var configRepository: FakeConfigRepository
    private lateinit var eventBus: EventBus
    private lateinit var navigationBus: NavigationBus

    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        todoRepository = FakeTodoRepository()
        configRepository = FakeConfigRepository()
        eventBus = EventBus()
        navigationBus = NavigationBus()
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): AddTodoViewModel = AddTodoViewModel(
        todoRepository = todoRepository,
        configRepository = configRepository,
        eventBus = eventBus,
        navigationBus = navigationBus,
        alarmScheduler = mockk(relaxed = true),
        analyticsHelper = NoOpAnalyticsHelper(),
        resourceProvider = object : ResourceProvider {
            override fun getString(resId: Int): String = ""
            override fun getString(resId: Int, vararg formatArgs: Any): String = ""
        },
        savedStateHandle = SavedStateHandle(mapOf("selectedDate" to "2024-01-01")),
    )

    @Test
    fun `반복 주기 목록은 최근 사용 순으로 정렬된다`() = runTest {
        // given: -4, -2 순으로 사용 (최근 사용이 -2)
        configRepository.recordRepeatCycleUsage(-4)
        configRepository.recordRepeatCycleUsage(-2)

        val viewModel = createViewModel()

        // when
        viewModel.loadRepeatCycles().join()

        // then: 최근 사용(-2, -4)이 앞으로, 나머지는 기본 순서 유지
        val ids = viewModel.state.value.repeatCycleList.map { it.id }
        assertEquals(listOf(-2, -4, -1, -5, -3), ids)
    }

    @Test
    fun `사용 이력이 없으면 반복 주기 기본 순서를 유지한다`() = runTest {
        val viewModel = createViewModel()

        viewModel.loadRepeatCycles().join()

        val ids = viewModel.state.value.repeatCycleList.map { it.id }
        assertEquals(listOf(-1, -5, -2, -3, -4), ids)
    }

    @Test
    fun `태그 목록은 최근 사용 순으로 정렬된다`() = runTest {
        // given: 태그 3개(id 1,2,3), 사용 순서 3 -> 1 (최근 사용이 1)
        todoRepository.addTag(name = "T1", color = 0)
        todoRepository.addTag(name = "T2", color = 0)
        todoRepository.addTag(name = "T3", color = 0)
        configRepository.recordTagUsage(3)
        configRepository.recordTagUsage(1)

        val viewModel = createViewModel()

        // when
        viewModel.loadTags().join()

        // then: 최근 사용(1, 3)이 앞으로, 나머지(2)는 뒤로
        val ids = viewModel.state.value.tagList.map { it.id }
        assertEquals(listOf(1, 3, 2), ids)
    }
}
