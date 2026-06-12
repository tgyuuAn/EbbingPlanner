package com.tgyuu.shared.ui.feature.home

import androidx.lifecycle.viewModelScope
import com.tgyuu.shared.base.BaseViewModel
import com.tgyuu.shared.common.now
import com.tgyuu.shared.designsystem.component.calendar.totalDaysInMonth
import com.tgyuu.shared.domain.model.SortType
import com.tgyuu.shared.domain.model.TodoSchedule
import com.tgyuu.shared.domain.repository.TodoRepository
import com.tgyuu.shared.ui.model.TodoScheduleUiModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.plus

class HomeViewModel(
    private val todoRepository: TodoRepository,
    private val configRepository: com.tgyuu.shared.domain.repository.ConfigRepository? = null,
    private val onNavigateToSetting: () -> Unit = {},
    private val onNavigateToSchedule: () -> Unit = {},
    private val onNavigateToAddTodo: (LocalDate) -> Unit = {},
    private val onNavigateToEditTodo: (Int) -> Unit = {},
    private val onNavigateToEditDate: (Int) -> Unit = {},
    private val onNavigateToMemo: (Int) -> Unit = {},
    private val onNavigateToSync: () -> Unit = {},
    private val onShowSnackBar: (String) -> Unit = {},
) : BaseViewModel<HomeState, HomeIntent>(HomeState()) {

    // Callbacks for UI events that need to show dialogs/bottomsheets
    var onShowSortTypeBottomSheet: (() -> Unit)? = null
    var onShowEditOptionsBottomSheet: ((TodoScheduleUiModel) -> Unit)? = null

    // Cached schedules for faster updates
    private var currentMonthSchedules: List<TodoSchedule> = emptyList()
    private var cachedSchedules: List<TodoSchedule> = emptyList()

    init {
        val today = LocalDate.now()
        setState { copy(currentDate = today, selectedDate = today) }
        loadSortType()
    }

    private fun loadSortType() {
        viewModelScope.launch {
            val sortType = configRepository?.getSortType() ?: SortType.CREATED
            setState { copy(sortType = sortType) }
        }
        viewModelScope.launch {
            configRepository?.getMondayStart()
                ?.collect { setState { copy(mondayStart = it) } }
        }
        viewModelScope.launch {
            todoRepository.loadAllSchedules().let { schedules ->
                if (schedules.size >= 3 && configRepository?.consumeInAppReview() == true) {
                    setState { copy(showInAppReviewDialog = true) }
                }
            }
        }
    }

    fun dismissInAppReviewDialog() {
        setState { copy(showInAppReviewDialog = false) }
    }

    suspend fun initCurrentMonthSchedules() {
        val today = LocalDate.now()
        val start = LocalDate(today.year, today.monthNumber, 1)
        val end = LocalDate(today.year, today.monthNumber, today.totalDaysInMonth())

        currentMonthSchedules = todoRepository.loadTodoSchedulesByDateRange(start, end)
        loadSchedules(today)
    }

    fun showWidgetNudgeDialog() {
        setState { copy(showWidgetNudgeDialog = true) }
    }

    private fun loadSchedules(currentDate: LocalDate) {
        viewModelScope.launch {
            setState { copy(isLoading = true) }

            try {
                val start = currentDate.minus(1, DateTimeUnit.MONTH)
                    .let { LocalDate(it.year, it.monthNumber, 1) }
                val end = currentDate.plus(1, DateTimeUnit.MONTH)
                    .let { LocalDate(it.year, it.monthNumber, it.totalDaysInMonth()) }

                val rangeSchedules = todoRepository.loadTodoSchedulesByDateRange(start, end)
                cachedSchedules = (currentMonthSchedules + rangeSchedules).distinctBy { it.id }

                val byDate = buildByDateMap(cachedSchedules, currentState.sortType)
                val byInfo = buildByInfoMap(cachedSchedules)

                setState {
                    copy(
                        isLoading = false,
                        schedulesByDateMap = byDate,
                        schedulesByTodoInfo = byInfo,
                    )
                }
            } catch (e: Exception) {
                setState { copy(isLoading = false) }
            }
        }
    }

    override suspend fun processIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.OnDateSelected -> {
                setState { copy(selectedDate = intent.date) }
            }
            is HomeIntent.OnCurrentDateChanged -> {
                loadSchedules(intent.currentDate)
            }
            is HomeIntent.OnCheckChanged -> onCheckedChange(intent.schedule)
            HomeIntent.OnSettingClick -> onNavigateToSetting()
            HomeIntent.OnScheduleClick -> onNavigateToSchedule()
            is HomeIntent.OnAddTodoClick -> onNavigateToAddTodo(intent.selectedDate)
            HomeIntent.OnSyncClick -> onNavigateToSync()

            is HomeIntent.OnEditScheduleClick -> {
                onShowEditOptionsBottomSheet?.invoke(intent.schedule)
            }

            HomeIntent.OnSortTypeClick -> {
                onShowSortTypeBottomSheet?.invoke()
            }
            is HomeIntent.OnUpdateSortType -> onUpdateSortType(intent.sortType)

            is HomeIntent.OnDeleteSingleClick -> onDeleteSingleSchedule(intent.schedule)
            is HomeIntent.OnDeleteRemainingClick -> onDeleteRemainingSchedule(intent.schedule)
            is HomeIntent.OnDeleteMemoClick -> deleteMemo(intent.schedule)

            is HomeIntent.OnUpdateInfoClick -> {
                onNavigateToEditTodo(intent.schedule.id)
            }
            is HomeIntent.OnUpdateDateClick -> {
                onNavigateToEditDate(intent.schedule.infoId)
            }

            is HomeIntent.OnDelaySingleClick -> onDelaySchedule(intent.schedule, intent.includeRestDays)
            is HomeIntent.OnDelayAllClick -> onDelayAllSchedules(intent.schedule, intent.includeRestDays)

            is HomeIntent.OnMemoClick -> {
                onNavigateToMemo(intent.schedule.id)
            }

            is HomeIntent.OnCalendarViewChanged -> {
                setState { copy(calendarDefaultView = intent.view) }
            }

            HomeIntent.OnWidgetNudgeDismiss -> {
                setState { copy(showWidgetNudgeDialog = false) }
            }
        }
    }

    private suspend fun onCheckedChange(schedule: TodoScheduleUiModel) {
        val domainSchedule = schedule.toDomainModel()
        val newSchedule = domainSchedule.copy(isDone = !domainSchedule.isDone)
        todoRepository.updateTodo(newSchedule)

        currentMonthSchedules = currentMonthSchedules.map {
            if (it.id == schedule.id) newSchedule else it
        }
        cachedSchedules = cachedSchedules.map {
            if (it.id == schedule.id) newSchedule else it
        }
        updateCacheAfterChange()
        rebuildState()
    }

    private suspend fun onDeleteSingleSchedule(schedule: TodoScheduleUiModel) {
        todoRepository.deleteTodo(schedule.toDomainModel())

        currentMonthSchedules = currentMonthSchedules.filterNot { it.id == schedule.id }
        cachedSchedules = cachedSchedules.filterNot { it.id == schedule.id }
        updateCacheAfterChange()
        rebuildState("해당 일정을 지웠습니다.")
    }

    private suspend fun onDeleteRemainingSchedule(schedule: TodoScheduleUiModel) {
        val futureSchedulesToDelete = todoRepository
            .loadSchedulesByTodoInfo(schedule.infoId)
            .filter { it.date >= schedule.date }

        val deletedIds = futureSchedulesToDelete.map { it.id }.toSet()
        for (item in futureSchedulesToDelete) {
            todoRepository.deleteTodo(item)
        }

        currentMonthSchedules = currentMonthSchedules.filterNot { it.id in deletedIds }
        cachedSchedules = cachedSchedules.filterNot { it.id in deletedIds }

        updateCacheAfterChange()
        rebuildState("해당 일정 이후 연계된 일정들을 모두 지웠습니다.")
    }

    private suspend fun onDelaySchedule(schedule: TodoScheduleUiModel, includeRestDays: Boolean = false) {
        val todoInfo = todoRepository.loadTodoInfoById(schedule.infoId)
        val restDays = if (includeRestDays) emptySet() else todoInfo.restDays

        var nextDate = schedule.date.plus(1, DateTimeUnit.DAY).nextValidDate(restDays)

        // Avoid date collisions with other schedules of the same info
        while (cachedSchedules.any { it.infoId == schedule.infoId && it.date == nextDate && it.id != schedule.id }) {
            nextDate = nextDate.plus(1, DateTimeUnit.DAY).nextValidDate(restDays)
        }

        val delayed = schedule.toDomainModel().copy(date = nextDate)
        todoRepository.updateTodo(delayed)

        currentMonthSchedules = currentMonthSchedules.map {
            if (it.id == schedule.id) delayed else it
        }
        cachedSchedules = cachedSchedules.map {
            if (it.id == schedule.id) delayed else it
        }
        updateCacheAfterChange()
        rebuildState("해당 일정을 다음 날로 미뤘습니다.")
    }

    private suspend fun onDelayAllSchedules(schedule: TodoScheduleUiModel, includeRestDays: Boolean = false) {
        val todoInfo = todoRepository.loadTodoInfoById(schedule.infoId)
        val restDays = if (includeRestDays) emptySet() else todoInfo.restDays

        val futureSchedules = todoRepository
            .loadSchedulesByTodoInfo(schedule.infoId)
            .filter { it.date >= schedule.date }
            .sortedByDescending { it.date }

        if (futureSchedules.isEmpty()) {
            onShowSnackBar("미룰 일정이 없습니다.")
            return
        }

        val updatedDates = mutableMapOf<Int, LocalDate>()

        for (scheduleToDelay in futureSchedules) {
            var nextDate = scheduleToDelay.date.plus(1, DateTimeUnit.DAY).nextValidDate(restDays)

            while (updatedDates.values.contains(nextDate) ||
                cachedSchedules.any { it.infoId == schedule.infoId && it.date == nextDate && it.id != scheduleToDelay.id }) {
                nextDate = nextDate.plus(1, DateTimeUnit.DAY).nextValidDate(restDays)
            }

            val delayed = scheduleToDelay.copy(date = nextDate)
            updatedDates[scheduleToDelay.id] = nextDate

            todoRepository.updateTodo(delayed)

            currentMonthSchedules = currentMonthSchedules.map {
                if (it.id == scheduleToDelay.id) delayed else it
            }
            cachedSchedules = cachedSchedules.map {
                if (it.id == scheduleToDelay.id) delayed else it
            }
        }

        updateCacheAfterChange()
        rebuildState("${futureSchedules.size}개 일정을 미뤘습니다.")
    }

    private suspend fun deleteMemo(schedule: TodoScheduleUiModel) {
        val updated = schedule.toDomainModel().copy(memo = "")
        todoRepository.updateTodo(updated)

        currentMonthSchedules = currentMonthSchedules.map {
            if (it.id == schedule.id) updated else it
        }
        cachedSchedules = cachedSchedules.map {
            if (it.id == schedule.id) updated else it
        }
        updateCacheAfterChange()
        rebuildState("메모를 제거하였습니다")
    }

    private fun onUpdateSortType(sortType: SortType) {
        val byDate = buildByDateMap(cachedSchedules, sortType)
        setState {
            copy(
                sortType = sortType,
                schedulesByDateMap = byDate,
            )
        }
        viewModelScope.launch {
            configRepository?.setSortType(sortType)
        }
    }

    private fun buildByDateMap(
        schedules: List<TodoSchedule>,
        sortType: SortType,
    ): ImmutableMap<LocalDate, ImmutableList<TodoScheduleUiModel>> {
        val grouped = schedules.groupBy { it.date }

        return grouped.mapValues { (_, list) ->
            val sorted = when (sortType) {
                SortType.CREATED -> list.sortedWith(compareBy({ it.isDone }, { it.createdAt }))
                SortType.NAME -> list.sortedWith(compareBy({ it.isDone }, { it.title }))
                SortType.PRIORITY -> list.sortedWith(compareBy({ it.isDone }, { -it.priority }))
            }
            sorted.map { it.toUiModel() }.toImmutableList()
        }.toImmutableMap()
    }

    private fun buildByInfoMap(
        schedules: List<TodoSchedule>,
    ): ImmutableMap<Int, ImmutableList<TodoScheduleUiModel>> {
        return schedules.groupBy { it.infoId }.mapValues { (_, list) ->
            list.map { it.toUiModel() }.toImmutableList()
        }.toImmutableMap()
    }

    private fun updateCacheAfterChange() {
        cachedSchedules = (currentMonthSchedules + cachedSchedules).distinctBy { it.id }
    }

    private fun rebuildState(snackbarMessage: String? = null) {
        val byDate = buildByDateMap(cachedSchedules, currentState.sortType)
        val byInfo = buildByInfoMap(cachedSchedules)
        setState { copy(schedulesByDateMap = byDate, schedulesByTodoInfo = byInfo) }
        if (!snackbarMessage.isNullOrEmpty()) onShowSnackBar(snackbarMessage)
    }

    suspend fun calculateDelayInfo(infoId: Int, currentDate: LocalDate): Pair<Set<DayOfWeek>, LocalDate> {
        val todoInfo = todoRepository.loadTodoInfoById(infoId)
        val restDays = todoInfo.restDays

        var nextDate = currentDate.plus(1, DateTimeUnit.DAY).nextValidDate(restDays)

        while (cachedSchedules.any { it.infoId == infoId && it.date == nextDate }) {
            nextDate = nextDate.plus(1, DateTimeUnit.DAY).nextValidDate(restDays)
        }

        return restDays to nextDate
    }

    private fun TodoSchedule.toUiModel() = TodoScheduleUiModel(
        id = id,
        infoId = infoId,
        title = title,
        tagId = tagId,
        name = name,
        color = color,
        date = date,
        memo = memo,
        priority = priority,
        isDone = isDone,
        createdAt = createdAt,
        infoCreatedAt = infoCreatedAt,
    )

    private fun TodoScheduleUiModel.toDomainModel() = TodoSchedule(
        id = id,
        infoId = infoId,
        title = title,
        tagId = tagId,
        name = name,
        color = color,
        date = date,
        memo = memo,
        priority = priority,
        isDone = isDone,
        createdAt = createdAt,
        infoCreatedAt = infoCreatedAt,
    )
}

private fun LocalDate.nextValidDate(restDays: Set<DayOfWeek>): LocalDate {
    if (restDays.size >= 7) {
        throw IllegalStateException("모든 요일을 휴식할 수는 없습니다")
    }

    var candidate = this
    var attempts = 0
    while (restDays.contains(candidate.dayOfWeek)) {
        candidate = candidate.plus(1, DateTimeUnit.DAY)
        attempts++
        if (attempts > 7) {
            throw IllegalStateException("유효한 날짜를 찾을 수 없습니다")
        }
    }
    return candidate
}
