package com.tgyuu.home.graph.addtodo

import androidx.lifecycle.SavedStateHandle
import com.tgyuu.analytics.AnalyticsEvent
import com.tgyuu.analytics.AnalyticsHelper
import com.tgyuu.common.event.EventBus
import com.tgyuu.common.ui.resource.ResourceProvider
import com.tgyuu.domain.model.TodoSchedule
import com.tgyuu.experiment.domain.model.Experiment
import com.tgyuu.home.fake.FakeConfigRepository
import com.tgyuu.home.fake.FakeExperimentRepository
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
    private val loggedEvents = mutableListOf<AnalyticsEvent>()

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

    private fun createViewModel(variant: String = "TREATMENT"): AddTodoViewModel = AddTodoViewModel(
        todoRepository = todoRepository,
        configRepository = FakeConfigRepository(),
        eventBus = EventBus(),
        navigationBus = NavigationBus(),
        alarmScheduler = mockk(relaxed = true),
        analyticsHelper = object : AnalyticsHelper() {
            override fun logEvent(event: AnalyticsEvent) {
                loggedEvents += event
            }

            override fun setUserId(userId: String?) = Unit
        },
        resourceProvider = object : ResourceProvider {
            override fun getString(resId: Int): String = ""
            override fun getString(resId: Int, vararg formatArgs: Any): String = ""
        },
        experimentRepository = FakeExperimentRepository(
            mapOf(Experiment.NextProgressSuggestion.key to variant)
        ),
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

        assertEquals(listOf("민법총론 3강", "경제원론 51-"), suggestions.map { it.title })
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
            assertEquals("경제원론 51-", title)
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
    fun `같은 과목은 최신 일정 하나만 추천하고 직전 일정과의 진도 간격을 따른다`() = runTest {
        todoRepository.addTag(name = "일본어", color = 0)
        addTodo(1, "JLPT N3 단어 1-50", tagId = 1, LocalDate.of(2024, 1, 1), listOf(0))
        addTodo(2, "JLPT N3 단어 51-100", tagId = 1, LocalDate.of(2024, 1, 2), listOf(0))

        val suggestions = createViewModel().state.value.suggestions

        assertEquals(listOf("JLPT N3 단어 101-"), suggestions.map { it.title })
    }

    @Test
    fun `칩을 누르면 어떤 칩을 눌렀는지 클릭 로그를 남긴다`() = runTest {
        todoRepository.addTag(name = "태그", color = 0)
        addTodo(1, "경제원론 1-50", tagId = 1, LocalDate.of(2024, 1, 1), listOf(0, 1, 7, 15))
        addTodo(2, "민법총론 2강", tagId = 1, LocalDate.of(2024, 1, 2), listOf(0))
        val viewModel = createViewModel()
        viewModel.onIntent(AddTodoIntent.OnTitleChange("경"))

        viewModel.onIntent(AddTodoIntent.OnSuggestionClick(viewModel.state.value.visibleSuggestions.first()))

        val click = loggedEvents.filterIsInstance<AnalyticsEvent.Click>()
            .single { it.buttonName == "NextProgressSuggestion" }
        assertEquals("NextProgressSuggestion", click.buttonName)
        assertEquals(
            mapOf(
                "variant" to "TREATMENT",
                "suggestion_title" to "경제원론 51-",
                "suggestion_index" to 0,
                "suggestion_count" to 1,
                "suggestion_basis" to "range",
                "has_repeat_cycle" to true,
                "typed_title_length" to 1,
            ),
            click.properties,
        )
    }

    @Test
    fun `추천 칩이 뜨면 노출 로그를 남긴다`() = runTest {
        todoRepository.addTag(name = "태그", color = 0)
        addTodo(1, "경제원론 1-50", tagId = 1, LocalDate.of(2024, 1, 1), listOf(0))
        addTodo(2, "영단어", tagId = 1, LocalDate.of(2024, 1, 2), listOf(0))

        createViewModel()

        val show = loggedEvents.filterIsInstance<AnalyticsEvent.Action>()
            .single { it.actionName == "show_next_progress_suggestion" }
        assertEquals(
            mapOf("variant" to "TREATMENT", "suggestion_count" to 2, "suggestion_bases" to "none,range"),
            show.properties,
        )
    }

    @Test
    fun `추천 칩이 없으면 노출 로그를 남기지 않는다`() = runTest {
        createViewModel()

        assertTrue(
            loggedEvents.filterIsInstance<AnalyticsEvent.Action>()
                .none { it.actionName == "show_next_progress_suggestion" }
        )
    }

    @Test
    fun `칩으로 만든 일정을 저장하면 칩 사용 여부와 제목 수정 여부를 남긴다`() = runTest {
        todoRepository.addTag(name = "태그", color = 0)
        addTodo(1, "경제원론 1-50", tagId = 1, LocalDate.of(2024, 1, 1), listOf(0, 1, 7, 15))
        val viewModel = createViewModel()
        viewModel.onIntent(AddTodoIntent.OnSuggestionClick(viewModel.state.value.suggestions.first()))
        viewModel.onIntent(AddTodoIntent.OnTitleChange("경제원론 51-80"))

        viewModel.onIntent(AddTodoIntent.OnSaveClick)

        val save = loggedEvents.filterIsInstance<AnalyticsEvent.Action>()
            .single { it.actionName == "save_todo" }
        assertEquals(
            mapOf(
                "variant" to "TREATMENT",
                "had_suggestions" to true,
                "from_suggestion" to true,
                "suggestion_basis" to "range",
                "title_edited" to true,
                "tag_from_last" to true,
                "repeat_cycle_from_last" to true,
            ),
            save.properties,
        )
    }

    @Test
    fun `CONTROL 실험군은 진도 없이 과목명만 추천하고 로그에 실험군을 남긴다`() = runTest {
        todoRepository.addTag(name = "태그", color = 0)
        addTodo(1, "경제원론 1-50", tagId = 1, LocalDate.of(2024, 1, 1), listOf(0))
        addTodo(2, "Statistics Ch.2", tagId = 1, LocalDate.of(2024, 1, 2), listOf(0))
        addTodo(3, "영단어", tagId = 1, LocalDate.of(2024, 1, 3), listOf(0))
        val viewModel = createViewModel(variant = "control")

        assertEquals(
            listOf("영단어", "Statistics Ch.", "경제원론"),
            viewModel.state.value.suggestions.map { it.title },
        )

        viewModel.onIntent(AddTodoIntent.OnSuggestionClick(viewModel.state.value.suggestions.last()))
        viewModel.onIntent(AddTodoIntent.OnSaveClick)

        val save = loggedEvents.filterIsInstance<AnalyticsEvent.Action>()
            .single { it.actionName == "save_todo" }
        assertEquals("CONTROL", save.properties?.get("variant"))
        assertEquals(false, save.properties?.get("title_edited"))
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
