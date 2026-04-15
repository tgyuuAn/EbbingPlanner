package com.tgyuu.home.graph.main

import androidx.lifecycle.viewModelScope
import com.tgyuu.alarm.AlarmScheduler
import com.tgyuu.analytics.AnalyticsEvent
import com.tgyuu.analytics.AnalyticsHelper
import com.tgyuu.common.base.BaseViewModel
import com.tgyuu.common.event.EbbingEvent
import com.tgyuu.common.event.EbbingEvent.ShowBottomSheet
import com.tgyuu.common.event.BottomSheetContent
import com.tgyuu.common.event.EventBus
import com.tgyuu.common.toFormattedString
import com.tgyuu.designsystem.model.TodoScheduleUiModel
import com.tgyuu.domain.model.SortType
import com.tgyuu.domain.model.TodoSchedule
import com.tgyuu.domain.repository.ConfigRepository
import com.tgyuu.domain.repository.TodoRepository
import com.tgyuu.home.graph.main.contract.HomeIntent
import com.tgyuu.home.graph.main.contract.HomeState
import com.tgyuu.home.model.toDomainModel
import com.tgyuu.home.model.toUiModel
import com.tgyuu.home.model.toUiModels
import com.tgyuu.navigation.HomeGraph
import com.tgyuu.navigation.HomeGraph.AddTodoRoute
import com.tgyuu.navigation.HomeGraph.EditTodoRoute
import com.tgyuu.navigation.MemoGraph
import com.tgyuu.navigation.NavigationBus
import com.tgyuu.navigation.NavigationEvent.To
import com.tgyuu.inappreview.InAppReviewManager
import com.tgyuu.navigation.SyncGraph
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val todoRepository: TodoRepository,
    private val configRepository: ConfigRepository,
    private val navigationBus: NavigationBus,
    private val alarmScheduler: AlarmScheduler?,
    private val analyticsHelper: AnalyticsHelper,
    internal val eventBus: EventBus,
    internal val inAppReviewManager: InAppReviewManager,
) : BaseViewModel<HomeState, HomeIntent>(HomeState()) {
    private var currentMonthSchedules: List<TodoSchedule> = emptyList()
    private var cachedSchedules: List<TodoSchedule> = emptyList()

    init {
        viewModelScope.launch {
            val sortType = configRepository.getSortType()
            setState { copy(sortType = sortType) }
        }

        viewModelScope.launch {
            configRepository.getMondayStart()
                .collect { setState { copy(mondayStart = it) } }
        }

        viewModelScope.launch {
            configRepository.getTodoRegisteredCount().first { it >= 3 }
            setState { copy(showInAppReviewDialog = true) }
        }
    }

    fun dismissInAppReviewDialog() {
        setState { copy(showInAppReviewDialog = false) }
    }

    suspend fun initCurrentMonthSchedules() {
        val today = LocalDate.now()
        val start = today.withDayOfMonth(1)
        val end = today.withDayOfMonth(today.lengthOfMonth())

        currentMonthSchedules = todoRepository.loadTodoSchedulesByDateRange(start, end)
    }

    fun showWidgetNudgeDialog() {
        setState { copy(showWidgetNudgeDialog = true) }
    }

    override suspend fun processIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.OnAddTodoClick -> onAddTodoClick(intent.selectedDate)
            is HomeIntent.OnCheckChanged -> onCheckedChange(intent.schedule.toDomainModel())
            is HomeIntent.OnEditScheduleClick -> onEditScheduleClick(intent.content)

            is HomeIntent.OnDelayScheduleClick -> onDelayScheduleClick(intent.content)
            is HomeIntent.OnDelaySingleClick -> onDelaySchedule(
                intent.schedule.toDomainModel(),
                intent.includeRestDays
            )

            is HomeIntent.OnDelayAllClick -> onDelayAllSchedules(
                intent.schedule.toDomainModel(),
                intent.includeRestDays
            )

            is HomeIntent.OnMemoClick -> onMemoClick(intent.schedule.toDomainModel())
            is HomeIntent.OnSortTypeClick -> onSortTypeClick(intent.content)
            is HomeIntent.OnUpdateSortType -> onUpdateSortType(intent.sortType)
            is HomeIntent.OnUpdateScheduleClick -> onUpdateScheduleClick(intent.content)
            is HomeIntent.OnUpdateInfoClick -> onUpdateInfoClick(intent.schedule.id)
            is HomeIntent.OnUpdateDateClick -> onUpdateDateClick(intent.schedule.infoId)
            is HomeIntent.OnDeleteMemoClick -> deleteMemo(intent.schedule.toDomainModel())
            is HomeIntent.OnDeleteScheduleClick -> onDeleteScheduleClick(intent.content)
            is HomeIntent.OnDeleteSingleClick -> onDeleteSingleSchedule(intent.schedule.toDomainModel())
            is HomeIntent.OnDeleteRemainingClick -> onDeleteRemainingSchedule(intent.schedule.toDomainModel())
            HomeIntent.OnSyncClick -> onSyncClick()
            is HomeIntent.OnCurrentDateChanged -> loadSchedules(intent.currentDate)
            HomeIntent.OnWidgetNudgeDismiss -> onWidgetNudgeDismiss()
        }
    }

    private suspend fun onSyncClick() {
        analyticsHelper.logEvent(
            AnalyticsEvent.Click(screenName = "Home", buttonName = "Sync")
        )
        navigationBus.navigate(To(SyncGraph.SyncMainRoute))
    }

    private fun onWidgetNudgeDismiss() {
        setState { copy(showWidgetNudgeDialog = false) }
    }

    private suspend fun onAddTodoClick(selectedDate: LocalDate) {
        analyticsHelper.logEvent(
            AnalyticsEvent.Click(screenName = "Home", buttonName = "AddSchedule")
        )
        navigationBus.navigate(To(AddTodoRoute(selectedDate.toFormattedString())))
    }

    private suspend fun onEditScheduleClick(content: BottomSheetContent) {
        analyticsHelper.logEvent(
            AnalyticsEvent.Click(screenName = "Home", buttonName = "OptionSchedule")
        )
        eventBus.sendEvent(ShowBottomSheet(content))
    }

    private suspend fun onUpdateScheduleClick(content: BottomSheetContent) {
        analyticsHelper.logEvent(
            AnalyticsEvent.Click(screenName = "Home", buttonName = "EditSchedule")
        )
        eventBus.sendEvent(ShowBottomSheet(content))
    }

    private suspend fun onDeleteScheduleClick(content: BottomSheetContent) {
        analyticsHelper.logEvent(
            AnalyticsEvent.Click(screenName = "Home", buttonName = "DeleteSchedule")
        )
        eventBus.sendEvent(ShowBottomSheet(content))
    }

    private suspend fun onDelayScheduleClick(content: BottomSheetContent) {
        analyticsHelper.logEvent(
            AnalyticsEvent.Click(screenName = "Home", buttonName = "DelaySchedule")
        )
        eventBus.sendEvent(ShowBottomSheet(content))
    }

    private suspend fun onMemoClick(schedule: TodoSchedule) {
        analyticsHelper.logEvent(
            AnalyticsEvent.Click(screenName = "Home", buttonName = "AddMemo")
        )

        val destination = if (schedule.memo.isEmpty()) MemoGraph.AddMemoRoute(schedule.id)
        else MemoGraph.EditMemoRoute(schedule.id)

        eventBus.sendEvent(EbbingEvent.HideBottomSheet)
        navigationBus.navigate(To(destination))
    }

    private suspend fun onUpdateDateClick(infoId: Int) {
        analyticsHelper.logEvent(
            AnalyticsEvent.Click(screenName = "Home", buttonName = "EditAllDate")
        )
        eventBus.sendEvent(EbbingEvent.HideBottomSheet)
        navigationBus.navigate(To(HomeGraph.EditDateRoute(infoId)))
    }

    private suspend fun onUpdateInfoClick(infoId: Int) {
        analyticsHelper.logEvent(
            AnalyticsEvent.Click(screenName = "Home", buttonName = "EditSingleDate")
        )
        eventBus.sendEvent(EbbingEvent.HideBottomSheet)
        navigationBus.navigate(To(EditTodoRoute(infoId)))
    }

    private fun loadSchedules(currentDate: LocalDate) = viewModelScope.launch {
        val start = currentDate.withDayOfMonth(1).minusMonths(1)
        val end = currentDate.withDayOfMonth(1).plusMonths(2).minusDays(1)

        val rangeSchedules = todoRepository.loadTodoSchedulesByDateRange(start, end)
        cachedSchedules = (currentMonthSchedules + rangeSchedules).distinctBy { it.id }

        val byDate = buildByDateMap(cachedSchedules, currentState.sortType)
        val byInfo = buildByInfoMap(cachedSchedules)
        setState {
            copy(
                isLoading = false,
                schedulesByDateMap = byDate,
                schedulesByTodoInfo = byInfo
            )
        }
    }

    private suspend fun onCheckedChange(schedule: TodoSchedule) {
        val newSchedule = schedule.copy(isDone = !schedule.isDone)
        analyticsHelper.logEvent(
            AnalyticsEvent.Click(
                screenName = "Home",
                buttonName = "CheckTodo",
                properties = mapOf("is_done" to newSchedule.isDone),
            )
        )
        todoRepository.updateTodo(newSchedule)

        currentMonthSchedules = currentMonthSchedules.map {
            if (it.id == schedule.id) newSchedule else it
        }
        cachedSchedules = cachedSchedules.map { if (it.id == schedule.id) newSchedule else it }
        updateCacheAfterChange()

        val updatedByInfo = buildByInfoMap(cachedSchedules)
        val updatedByDate = buildByDateMap(cachedSchedules, currentState.sortType)

        setState {
            copy(
                schedulesByTodoInfo = updatedByInfo,
                schedulesByDateMap = updatedByDate
            )
        }
    }

    private suspend fun onDeleteSingleSchedule(schedule: TodoSchedule) {
        analyticsHelper.logEvent(
            AnalyticsEvent.Click(screenName = "Home", buttonName = "DeleteSingleSchedule")
        )
        todoRepository.deleteTodo(schedule)

        currentMonthSchedules = currentMonthSchedules.filterNot { it.id == schedule.id }
        cachedSchedules = cachedSchedules.filterNot { it.id == schedule.id }
        updateCacheAfterChange()

        val updatedByInfo = buildByInfoMap(cachedSchedules)
        val updatedByDate = buildByDateMap(cachedSchedules, currentState.sortType)

        setState {
            copy(
                schedulesByTodoInfo = updatedByInfo,
                schedulesByDateMap = updatedByDate
            )
        }

        eventBus.sendEvent(EbbingEvent.HideBottomSheet)
        eventBus.sendEvent(EbbingEvent.ShowSnackBar("해당 일정을 지웠습니다."))
    }

    private suspend fun onDeleteRemainingSchedule(schedule: TodoSchedule) {
        analyticsHelper.logEvent(
            AnalyticsEvent.Click(screenName = "Home", buttonName = "DeleteAllSchedule")
        )
        val futureSchedulesToDelete = todoRepository
            .loadSchedulesByTodoInfo(schedule.infoId)
            .filter { it.date >= schedule.date }

        for (item in futureSchedulesToDelete) {
            todoRepository.deleteTodo(item)
            currentMonthSchedules = currentMonthSchedules.filterNot { it.id == item.id }
            cachedSchedules = cachedSchedules.filterNot { it.id == item.id }
        }

        updateCacheAfterChange()

        val updatedByInfo = buildByInfoMap(cachedSchedules)
        val updatedByDate = buildByDateMap(cachedSchedules, currentState.sortType)

        setState {
            copy(
                schedulesByTodoInfo = updatedByInfo,
                schedulesByDateMap = updatedByDate
            )
        }

        eventBus.sendEvent(EbbingEvent.HideBottomSheet)
        eventBus.sendEvent(EbbingEvent.ShowSnackBar("해당 일정 이후 연계된 일정들을 모두 지웠습니다."))
    }

    private suspend fun onDelaySchedule(schedule: TodoSchedule, includeRestDays: Boolean = false) {
        analyticsHelper.logEvent(
            AnalyticsEvent.Click(screenName = "Home", buttonName = "SaveDelaySingle")
        )
        val todoInfo = todoRepository.loadTodoInfoById(schedule.infoId)
        val restDays = if (includeRestDays) emptySet() else todoInfo.restDays

        var nextDate = schedule.date.plusDays(1).nextValidDate(restDays)

        while (cachedSchedules.any { it.infoId == schedule.infoId && it.date == nextDate && it.id != schedule.id }) {
            nextDate = nextDate.plusDays(1).nextValidDate(restDays)
        }

        val delayed = schedule.copy(date = nextDate)
        todoRepository.updateTodo(delayed)
        val (hour, minute) = configRepository.getAlarmTime()

        alarmScheduler?.cancelDailyExact(schedule.date)

        if (nextDate.isAfter(LocalDate.now())) {
            val triggerAtMillis = nextDate
                .atTime(LocalTime.of(hour, minute))
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()

            alarmScheduler?.scheduleDailyExact(
                date = nextDate,
                triggerAtMillis = triggerAtMillis
            )
        }

        currentMonthSchedules =
            currentMonthSchedules.map { if (it.id == schedule.id) delayed else it }
        cachedSchedules = cachedSchedules.map { if (it.id == schedule.id) delayed else it }
        updateCacheAfterChange()

        val newByDate = buildByDateMap(cachedSchedules, currentState.sortType)
        val newByInfo = buildByInfoMap(cachedSchedules)
        setState {
            copy(
                schedulesByDateMap = newByDate,
                schedulesByTodoInfo = newByInfo
            )
        }

        analyticsHelper.logEvent(
            AnalyticsEvent.Action(
                screenName = "Home",
                actionName = "delay_single_schedule",
                actionResult = "success",
                properties = mapOf(
                    "schedule_id" to schedule.id,
                    "original_date" to schedule.date.toString(),
                    "next_date" to nextDate.toString(),
                    "include_rest_days" to includeRestDays,
                    "rest_days_count" to restDays.size,
                ),
            )
        )

        eventBus.sendEvent(EbbingEvent.HideBottomSheet)
        eventBus.sendEvent(EbbingEvent.ShowSnackBar("해당 일정을 다음 날로 미뤘습니다."))
    }

    private suspend fun onDelayAllSchedules(
        schedule: TodoSchedule,
        includeRestDays: Boolean = false
    ) {
        analyticsHelper.logEvent(
            AnalyticsEvent.Click(screenName = "Home", buttonName = "SaveDelayAll")
        )
        val todoInfo = todoRepository.loadTodoInfoById(schedule.infoId)
        val restDays = if (includeRestDays) emptySet() else todoInfo.restDays

        val futureSchedules = todoRepository
            .loadSchedulesByTodoInfo(schedule.infoId)
            .filter { it.date >= schedule.date }
            .sortedByDescending { it.date }

        if (futureSchedules.isEmpty()) {
            analyticsHelper.logEvent(
                AnalyticsEvent.Action(
                    screenName = "Home",
                    actionName = "delay_all_schedules",
                    actionResult = "no_schedules",
                    properties = mapOf("schedule_id" to schedule.id),
                )
            )
            eventBus.sendEvent(EbbingEvent.ShowSnackBar("미룰 일정이 없습니다."))
            eventBus.sendEvent(EbbingEvent.HideBottomSheet)
            return
        }

        val updatedDates = mutableMapOf<Int, LocalDate>()
        val (hour, minute) = configRepository.getAlarmTime()
        for (scheduleToDelay in futureSchedules) {
            var nextDate = scheduleToDelay.date.plusDays(1).nextValidDate(restDays)

            while (updatedDates.values.contains(nextDate) ||
                cachedSchedules.any { it.infoId == schedule.infoId && it.date == nextDate && it.id != scheduleToDelay.id }
            ) {
                nextDate = nextDate.plusDays(1).nextValidDate(restDays)
            }

            val delayed = scheduleToDelay.copy(date = nextDate)
            updatedDates[scheduleToDelay.id] = nextDate

            todoRepository.updateTodo(delayed)

            alarmScheduler?.cancelDailyExact(scheduleToDelay.date)

            if (nextDate.isAfter(LocalDate.now())) {
                val triggerAtMillis = nextDate
                    .atTime(LocalTime.of(hour, minute))
                    .atZone(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli()

                alarmScheduler?.scheduleDailyExact(
                    date = nextDate,
                    triggerAtMillis = triggerAtMillis
                )
            }

            currentMonthSchedules = currentMonthSchedules.map {
                if (it.id == scheduleToDelay.id) delayed else it
            }
            cachedSchedules = cachedSchedules.map {
                if (it.id == scheduleToDelay.id) delayed else it
            }
        }

        updateCacheAfterChange()
        val newByDate = buildByDateMap(cachedSchedules, currentState.sortType)
        val newByInfo = buildByInfoMap(cachedSchedules)
        setState {
            copy(
                schedulesByDateMap = newByDate,
                schedulesByTodoInfo = newByInfo
            )
        }

        analyticsHelper.logEvent(
            AnalyticsEvent.Action(
                screenName = "Home",
                actionName = "delay_all_schedules",
                actionResult = "success",
                properties = mapOf(
                    "schedule_id" to schedule.id,
                    "schedules_count" to futureSchedules.size,
                    "include_rest_days" to includeRestDays,
                    "rest_days_count" to restDays.size,
                ),
            )
        )

        eventBus.sendEvent(EbbingEvent.HideBottomSheet)
        eventBus.sendEvent(EbbingEvent.ShowSnackBar("${futureSchedules.size}개 일정을 미뤘습니다."))
    }



    private suspend fun deleteMemo(schedule: TodoSchedule) {
        analyticsHelper.logEvent(
            AnalyticsEvent.Click(screenName = "Home", buttonName = "DeleteMemo")
        )
        val updated = schedule.copy(memo = "")
        todoRepository.updateTodo(updated)

        currentMonthSchedules = currentMonthSchedules.map {
            if (it.id == schedule.id) updated else it
        }
        cachedSchedules = cachedSchedules.map {
            if (it.id == schedule.id) updated else it
        }
        updateCacheAfterChange()

        val updatedByDate = buildByDateMap(cachedSchedules, currentState.sortType)
        val updatedByInfo = buildByInfoMap(cachedSchedules)

        setState {
            copy(
                schedulesByDateMap = updatedByDate,
                schedulesByTodoInfo = updatedByInfo
            )
        }

        eventBus.sendEvent(EbbingEvent.HideBottomSheet)
        eventBus.sendEvent(EbbingEvent.ShowSnackBar("메모를 제거하였습니다"))
    }

    private suspend fun onSortTypeClick(content: BottomSheetContent) {
        analyticsHelper.logEvent(
            AnalyticsEvent.Click(screenName = "Home", buttonName = "OrderSchedule")
        )
        eventBus.sendEvent(ShowBottomSheet(content))
    }

    private suspend fun onUpdateSortType(sortType: SortType) {
        analyticsHelper.logEvent(
            AnalyticsEvent.Click(
                screenName = "Home",
                buttonName = "SaveOrderSchedule",
                properties = mapOf("sort_type" to sortType.name),
            )
        )
        configRepository.setSortType(sortType)

        val byDate = buildByDateMap(cachedSchedules, sortType)
        setState {
            copy(
                sortType = sortType,
                schedulesByDateMap = byDate,
            )
        }
        eventBus.sendEvent(EbbingEvent.HideBottomSheet)
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
                SortType.PRIORITY -> list.sortedWith(compareBy({ it.isDone }, { it.priority }))
            }
            sorted.toUiModels()
        }.toImmutableMap()
    }

    private fun buildByInfoMap(
        schedules: List<TodoSchedule>,
    ): ImmutableMap<Int, ImmutableList<TodoScheduleUiModel>> {
        return schedules.groupBy { it.infoId }.mapValues { (_, list) ->
            list.toUiModels()
        }.toImmutableMap()
    }

    private fun updateCacheAfterChange() {
        cachedSchedules = (currentMonthSchedules + cachedSchedules)
            .distinctBy { it.id }
    }

    suspend fun calculateDelayInfo(
        infoId: Int,
        currentDate: LocalDate
    ): Pair<Set<DayOfWeek>, LocalDate> {
        val todoInfo = todoRepository.loadTodoInfoById(infoId)
        val restDays = todoInfo.restDays

        var nextDate = currentDate.plusDays(1).nextValidDate(restDays)

        while (cachedSchedules.any { it.infoId == infoId && it.date == nextDate }) {
            nextDate = nextDate.plusDays(1).nextValidDate(restDays)
        }

        return restDays to nextDate
    }
}

private fun LocalDate.nextValidDate(restDays: Set<DayOfWeek>): LocalDate {
    if (restDays.size >= 7) {
        throw IllegalStateException("모든 요일을 휴식할 수는 없습니다")
    }

    var candidate = this
    var attempts = 0
    while (restDays.contains(candidate.dayOfWeek)) {
        candidate = candidate.plusDays(1)
        attempts++
        if (attempts > 7) {
            throw IllegalStateException("유효한 날짜를 찾을 수 없습니다")
        }
    }
    return candidate
}
