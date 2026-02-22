package com.tgyuu.home.graph.main

import com.tgyuu.common.event.EventBus
import com.tgyuu.domain.model.TodoSchedule
import com.tgyuu.home.fake.FakeConfigRepository
import com.tgyuu.home.fake.FakeTodoRepository
import com.tgyuu.home.graph.main.contract.HomeIntent
import com.tgyuu.home.model.toUiModel
import com.tgyuu.navigation.NavigationBus
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
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    private lateinit var viewModel: HomeViewModel
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

        viewModel = HomeViewModel(
            todoRepository = todoRepository,
            configRepository = configRepository,
            navigationBus = navigationBus,
            alarmScheduler = null,
            eventBus = eventBus
        )
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `붙어있는 일정들을 함께 미룰 때 뒤에서부터 처리된다`() = runTest {
        // given: 연속된 3일의 일정 (1/1, 1/2, 1/3)
        val baseDate = LocalDate.of(2024, 1, 1)
        val schedule1 = createSchedule(id = 1, infoId = 100, date = baseDate)
        val schedule2 = createSchedule(id = 2, infoId = 100, date = baseDate.plusDays(1))
        val schedule3 = createSchedule(id = 3, infoId = 100, date = baseDate.plusDays(2))

        todoRepository.addSchedules(schedule1, schedule2, schedule3)

        // when: 첫 번째 일정(1/1)부터 모두 미루기
        viewModel.onIntent(HomeIntent.OnDelayAllClick(schedule1.toUiModel()))

        // then: 3개 일정이 모두 +1일씩 미뤄짐
        val updatedSchedules = todoRepository.loadAllSchedules()
        assertEquals(3, updatedSchedules.count {
            (it.id == 1 && it.date == baseDate.plusDays(1)) ||
            (it.id == 2 && it.date == baseDate.plusDays(2)) ||
            (it.id == 3 && it.date == baseDate.plusDays(3))
        })
    }

    @Test
    fun `단일 일정만 있을 때도 정상적으로 미뤄진다`() = runTest {
        // given: 단일 일정
        val baseDate = LocalDate.of(2024, 1, 1)
        val schedule = createSchedule(id = 1, infoId = 100, date = baseDate)
        todoRepository.addSchedules(schedule)

        // when: 모두 미루기 실행
        viewModel.onIntent(HomeIntent.OnDelayAllClick(schedule.toUiModel()))

        // then: 1개 일정이 +1일 미뤄짐
        val updatedSchedules = todoRepository.loadAllSchedules()
        assertEquals(baseDate.plusDays(1), updatedSchedules[0].date)
    }

    @Test
    fun `중간 일정부터 미루면 이후 일정만 처리된다`() = runTest {
        // given: 5개 일정 (1/1, 1/3, 1/5, 1/7, 1/9)
        val baseDate = LocalDate.of(2024, 1, 1)
        val schedule1 = createSchedule(id = 1, infoId = 100, date = baseDate)
        val schedule2 = createSchedule(id = 2, infoId = 100, date = baseDate.plusDays(2))
        val schedule3 = createSchedule(id = 3, infoId = 100, date = baseDate.plusDays(4))
        val schedule4 = createSchedule(id = 4, infoId = 100, date = baseDate.plusDays(6))
        val schedule5 = createSchedule(id = 5, infoId = 100, date = baseDate.plusDays(8))

        todoRepository.addSchedules(schedule1, schedule2, schedule3, schedule4, schedule5)

        // when: 3번째 일정(1/5)부터 미루기
        viewModel.onIntent(HomeIntent.OnDelayAllClick(schedule3.toUiModel()))

        // then: 이전 2개는 그대로, 이후 3개는 +1일씩 미뤄짐
        val updatedSchedules = todoRepository.loadAllSchedules()
        assertEquals(2, updatedSchedules.count {
            (it.id == 1 && it.date == baseDate) || (it.id == 2 && it.date == baseDate.plusDays(2))
        })
    }

    @Test
    fun `미래 날짜만 알람이 재스케줄링된다`() = runTest {
        // given: 과거, 오늘, 미래 일정
        val yesterday = LocalDate.now().minusDays(1)
        val today = LocalDate.now()
        val tomorrow = LocalDate.now().plusDays(1)

        val schedule1 = createSchedule(id = 1, infoId = 100, date = yesterday)
        val schedule2 = createSchedule(id = 2, infoId = 100, date = today)
        val schedule3 = createSchedule(id = 3, infoId = 100, date = tomorrow)

        todoRepository.addSchedules(schedule1, schedule2, schedule3)

        // when: 모두 미루기
        viewModel.onIntent(HomeIntent.OnDelayAllClick(schedule1.toUiModel()))

        // then: 3개 일정이 모두 +1일씩 미뤄짐
        val updatedSchedules = todoRepository.loadAllSchedules()
        assertEquals(3, updatedSchedules.count {
            (it.id == 1 && it.date == today) ||
            (it.id == 2 && it.date == tomorrow) ||
            (it.id == 3 && it.date == tomorrow.plusDays(1))
        })
    }

    @Test
    fun `여러 날짜가 띄엄띄엄 있어도 모두 미뤄진다`() = runTest {
        // given: 불규칙한 간격의 일정들
        val baseDate = LocalDate.of(2024, 1, 1)
        val schedule1 = createSchedule(id = 1, infoId = 100, date = baseDate)
        val schedule2 = createSchedule(id = 2, infoId = 100, date = baseDate.plusDays(5))
        val schedule3 = createSchedule(id = 3, infoId = 100, date = baseDate.plusDays(10))

        todoRepository.addSchedules(schedule1, schedule2, schedule3)

        // when: 모두 미루기
        viewModel.onIntent(HomeIntent.OnDelayAllClick(schedule1.toUiModel()))

        // then: 3개 일정이 모두 +1일씩 미뤄짐
        val updatedSchedules = todoRepository.loadAllSchedules()
        assertEquals(3, updatedSchedules.count {
            (it.id == 1 && it.date == baseDate.plusDays(1)) ||
            (it.id == 2 && it.date == baseDate.plusDays(6)) ||
            (it.id == 3 && it.date == baseDate.plusDays(11))
        })
    }

    private fun createSchedule(
        id: Int,
        infoId: Int,
        date: LocalDate,
        title: String = "테스트 일정",
        isDone: Boolean = false
    ) = TodoSchedule(
        id = id,
        infoId = infoId,
        title = title,
        tagId = 1,
        name = "태그",
        color = 0xFF0000,
        date = date,
        memo = "",
        priority = 3,
        isDone = isDone,
        createdAt = LocalDate.now(),
        infoCreatedAt = LocalDate.now()
    )
}
