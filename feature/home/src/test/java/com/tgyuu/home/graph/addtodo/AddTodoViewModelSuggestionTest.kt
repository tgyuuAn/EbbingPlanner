package com.tgyuu.home.graph.addtodo

import androidx.lifecycle.SavedStateHandle
import com.tgyuu.analytics.NoOpAnalyticsHelper
import com.tgyuu.common.event.EventBus
import com.tgyuu.common.ui.resource.ResourceProvider
import com.tgyuu.domain.model.TodoSchedule
import com.tgyuu.home.fake.FakeConfigRepository
import com.tgyuu.home.fake.FakeTodoRepository
import com.tgyuu.home.graph.addtodo.contract.AddTodoIntent
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
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class AddTodoViewModelSuggestionTest {
    private lateinit var todoRepository: FakeTodoRepository

    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        todoRepository = FakeTodoRepository()
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): AddTodoViewModel = AddTodoViewModel(
        todoRepository = todoRepository,
        configRepository = FakeConfigRepository(),
        eventBus = EventBus(),
        navigationBus = NavigationBus(),
        alarmScheduler = mockk(relaxed = true),
        analyticsHelper = NoOpAnalyticsHelper(),
        resourceProvider = object : ResourceProvider {
            override fun getString(resId: Int): String = ""
            override fun getString(resId: Int, vararg formatArgs: Any): String = ""
        },
        savedStateHandle = SavedStateHandle(mapOf("selectedDate" to "2024-01-01")),
    )

    private fun addTodo(
        infoId: Int,
        title: String,
        tagId: Int,
        baseDate: LocalDate,
        intervals: List<Int>,
    ) {
        todoRepository.addSchedules(
            *intervals.map { interval ->
                TodoSchedule(
                    id = infoId * 100 + interval,
                    infoId = infoId,
                    title = title,
                    tagId = tagId,
                    name = "",
                    color = 0,
                    date = baseDate.plusDays(interval.toLong()),
                    memo = "",
                    isPinned = false,
                    isDone = false,
                    createdAt = baseDate,
                    infoCreatedAt = baseDate,
                )
            }.toTypedArray()
        )
    }

    @Test
    fun `최근 일정 순으로 다음 진도를 추천하고 반복 주기를 회차 날짜로 추정한다`() = runTest {
        todoRepository.addTag(name = "경제", color = 0)
        todoRepository.addTag(name = "민법", color = 0)
        addTodo(1, "경제원론 1-50", tagId = 1, LocalDate.of(2024, 1, 1), listOf(0, 1, 7, 15))
        addTodo(2, "민법총론 2강", tagId = 2, LocalDate.of(2024, 1, 2), listOf(0, 3, 9))

        val suggestions = createViewModel().state.value.suggestions

        assertEquals(listOf("민법총론 3강", "경제원론 51~"), suggestions.map { it.title })
        assertNull(suggestions[0].repeatCycle)
        assertEquals(-2, suggestions[1].repeatCycle?.id)
        assertEquals(1, suggestions[1].tag.id)
    }

    @Test
    fun `칩을 고르면 제목과 지난 태그·반복 주기를 채우고, 필드를 바꾸면 출처 표시를 지운다`() = runTest {
        todoRepository.addTag(name = "경제", color = 0)
        todoRepository.addTag(name = "민법", color = 0)
        addTodo(1, "경제원론 1-50", tagId = 2, LocalDate.of(2024, 1, 1), listOf(0, 1, 7, 15))
        val viewModel = createViewModel()

        viewModel.onIntent(AddTodoIntent.OnSuggestionClick(viewModel.state.value.suggestions.first()))

        with(viewModel.state.value) {
            assertEquals("경제원론 51~", title)
            assertEquals(2, tag?.id)
            assertEquals(-2, repeatCycle?.id)
            assertTrue(isTagFromLast)
            assertTrue(isRepeatCycleFromLast)
            assertTrue(visibleSuggestions.isEmpty())
        }

        viewModel.onIntent(AddTodoIntent.OnTagChange(viewModel.state.value.suggestions.first().tag.copy(id = 1)))

        assertFalse(viewModel.state.value.isTagFromLast)
        assertTrue(viewModel.state.value.isRepeatCycleFromLast)
    }

    @Test
    fun `입력한 제목과 겹치는 칩만 남긴다`() = runTest {
        todoRepository.addTag(name = "태그", color = 0)
        addTodo(1, "경제원론 1-50", tagId = 1, LocalDate.of(2024, 1, 1), listOf(0))
        addTodo(2, "민법총론 2강", tagId = 1, LocalDate.of(2024, 1, 2), listOf(0))
        val viewModel = createViewModel()

        viewModel.onIntent(AddTodoIntent.OnTitleChange("민"))
        assertEquals(listOf("민법총론 3강"), viewModel.state.value.visibleSuggestions.map { it.title })

        viewModel.onIntent(AddTodoIntent.OnTitleChange("영"))
        assertTrue(viewModel.state.value.visibleSuggestions.isEmpty())
    }
}
