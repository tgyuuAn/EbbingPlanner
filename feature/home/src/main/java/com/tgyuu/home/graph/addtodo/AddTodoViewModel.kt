package com.tgyuu.home.graph.addtodo

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.tgyuu.alarm.AlarmScheduler
import com.tgyuu.analytics.AnalyticsEvent
import com.tgyuu.analytics.AnalyticsHelper
import com.tgyuu.common.base.BaseViewModel
import com.tgyuu.common.event.EbbingEvent
import com.tgyuu.common.event.EbbingEvent.ShowBottomSheet
import com.tgyuu.common.event.EventBus
import com.tgyuu.common.generateDailySchedules
import com.tgyuu.common.generateValidSchedules
import com.tgyuu.common.toFormattedString
import com.tgyuu.common.toLocalDateOrThrow
import com.tgyuu.common.ui.resource.ResourceProvider
import com.tgyuu.designsystem.R
import com.tgyuu.designsystem.model.RepeatCycleUiModel
import com.tgyuu.designsystem.model.TodoTagUiModel
import com.tgyuu.domain.model.DefaultRepeatCycles
import com.tgyuu.domain.model.DefaultTodoTag
import com.tgyuu.domain.model.RepeatCycle
import com.tgyuu.domain.model.TodoSchedule
import com.tgyuu.domain.repository.ConfigRepository
import com.tgyuu.domain.repository.ConfigRepository.Companion.DEFAULT_ALARM_MESSAGE
import com.tgyuu.domain.repository.TodoRepository
import com.tgyuu.home.graph.addtodo.contract.AddTodoIntent
import com.tgyuu.home.graph.addtodo.contract.AddTodoState
import com.tgyuu.home.graph.addtodo.contract.ProgressSuggestion
import com.tgyuu.home.model.hasFinalConsonant
import com.tgyuu.home.model.sortedByUsageOrder
import com.tgyuu.home.model.suggestNextProgress
import com.tgyuu.home.model.toUiModel
import com.tgyuu.home.model.toUiModels
import com.tgyuu.navigation.HomeGraph.HomeRoute
import com.tgyuu.navigation.NavigationBus
import com.tgyuu.navigation.NavigationEvent
import com.tgyuu.navigation.RepeatCycleGraph
import com.tgyuu.navigation.TagGraph
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableSet
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class AddTodoViewModel @Inject constructor(
    private val todoRepository: TodoRepository,
    private val configRepository: ConfigRepository,
    private val eventBus: EventBus,
    private val navigationBus: NavigationBus,
    private val alarmScheduler: AlarmScheduler,
    private val analyticsHelper: AnalyticsHelper,
    private val resourceProvider: ResourceProvider,
    private val savedStateHandle: SavedStateHandle,
) : BaseViewModel<AddTodoState, AddTodoIntent>(
    AddTodoState(
        selectedDate = (savedStateHandle.get<String>("selectedDate")
            ?: throw IllegalArgumentException("선택된 날짜가 없습니다.")).toLocalDateOrThrow(),
        tag = DefaultTodoTag.toUiModel(),
        repeatCycle = DefaultRepeatCycles.first().toUiModel(resourceProvider),
    )
) {

    init {
        analyticsHelper.logEvent(
            AnalyticsEvent.View(
                screenName = "AddTodo",
            )
        )

        setState {
            copy(
                tag = tag?.copy(name = resourceProvider.getString(R.string.tag_unassigned))
            )
        }

        initNotificationState()
        initLastSelected()
        initSuggestions()

        viewModelScope.launch {
            configRepository.getMondayStart()
                .collect { setState { copy(mondayStart = it) } }
        }
    }

    private fun initLastSelected() = viewModelScope.launch {
        // 사용 이력에 삭제된 id가 남아 있을 수 있으므로, 아직 존재하는 가장 최근 항목을 선택한다.
        val lastTag = configRepository.getTagUsageOrder()
            .firstNotNullOfOrNull { tagId -> todoRepository.loadTag(tagId) }
        lastTag?.let { tag ->
            val uiTag = tag.toUiModel().let {
                if (it.id == DefaultTodoTag.id) {
                    it.copy(name = resourceProvider.getString(R.string.tag_unassigned))
                } else {
                    it
                }
            }
            setState { copy(tag = uiTag) }
        }

        val customRepeatCycles = todoRepository.loadRepeatCycles()
        val lastRepeatCycle = configRepository.getRepeatCycleUsageOrder()
            .firstNotNullOfOrNull { cycleId ->
                DefaultRepeatCycles.find { it.id == cycleId }
                    ?: customRepeatCycles.find { it.id == cycleId }
            }
        lastRepeatCycle?.let {
            setState { copy(repeatCycle = it.toUiModel(resourceProvider)) }
        }
    }

    /** 최근에 만든 일정부터 다음 진도를 추정해 '다음 진도 추천' 칩으로 보여줘요. */
    private fun initSuggestions() = viewModelScope.launch {
        val tagsById = todoRepository.loadTags().associateBy { it.id }
        val repeatCycles = DefaultRepeatCycles + todoRepository.loadRepeatCycles()

        val suggestions = todoRepository.loadAllSchedules()
            .groupBy { it.infoId }
            .values
            .sortedWith(
                compareByDescending<List<TodoSchedule>> { it.first().infoCreatedAt }
                    .thenByDescending { it.first().infoId }
            )
            .distinctBy { suggestNextProgress(it.first().title).title }
            .take(MAX_SUGGESTION_COUNT)
            .mapNotNull { schedules ->
                val info = schedules.first()
                val tag = tagsById[info.tagId] ?: return@mapNotNull null
                val restDays = runCatching { todoRepository.loadTodoInfoById(info.infoId).restDays }
                    .getOrDefault(emptySet())
                ProgressSuggestion(
                    title = suggestNextProgress(info.title).title,
                    tag = tag.toUiModel().let {
                        if (it.id == DefaultTodoTag.id) {
                            it.copy(name = resourceProvider.getString(R.string.tag_unassigned))
                        } else {
                            it
                        }
                    },
                    repeatCycle = inferRepeatCycle(
                        dates = schedules.map { it.date },
                        restDays = restDays,
                        candidates = repeatCycles,
                    )?.toUiModel(resourceProvider),
                )
            }

        setState { copy(suggestions = suggestions.toImmutableList()) }
    }

    /** 일정에는 반복 주기가 저장되지 않아서, 회차 날짜를 다시 만들어 보고 일치하는 주기를 찾아요. */
    private fun inferRepeatCycle(
        dates: List<LocalDate>,
        restDays: Set<DayOfWeek>,
        candidates: List<RepeatCycle>,
    ): RepeatCycle? {
        val sortedDates = dates.sorted()
        val baseDate = sortedDates.firstOrNull() ?: return null
        return candidates.firstOrNull { cycle ->
            val generated = if (cycle.id == RepeatCycle.DAILY_REPEAT_ID) {
                generateDailySchedules(baseDate, cycle.intervals, restDays)
            } else {
                generateValidSchedules(baseDate, cycle.intervals, restDays)
            }
            generated == sortedDates
        }
    }

    private fun initNotificationState() = viewModelScope.launch {
        val (hour, minute) = configRepository.getAlarmTime()
        val defaultMessage = resourceProvider.getString(R.string.default_alarm_message)
        val placeholderToken = resourceProvider.getString(R.string.alarm_placeholder_token)
        val storedMessage = configRepository.getAlarmMessage()
        val message = if (storedMessage == DEFAULT_ALARM_MESSAGE) defaultMessage else storedMessage

        setState {
            copy(
                notificationState = notificationState.copy(
                    alarmHour = hour,
                    alarmMinute = minute,
                    defaultMessage = defaultMessage,
                    message = message,
                    originMessage = message,
                    placeholderToken = placeholderToken,
                )
            )
        }
    }

    internal fun loadNewTag() {
        todoRepository.recentAddedTagId?.let {
            viewModelScope.launch {
                val newTag = todoRepository.loadTag(it.toInt()) ?: return@launch
                setState { copy(tag = newTag.toUiModel()) }
            }
        }
    }

    internal fun loadNewRepeatCycle() {
        todoRepository.recentAddedRepeatCycleId?.let {
            viewModelScope.launch {
                val newRepeatCycle = todoRepository.loadRepeatCycle(it.toInt())
                setState { copy(repeatCycle = newRepeatCycle.toUiModel(resourceProvider)) }
            }
        }
    }

    internal fun loadTags() = viewModelScope.launch {
        val loadedTagList = todoRepository.loadTags()
        val usageOrder = configRepository.getTagUsageOrder()
        val sortedTagList = loadedTagList.sortedByUsageOrder(usageOrder) { it.id }
        setState { copy(tagList = sortedTagList.toUiModels()) }
    }

    internal fun loadRepeatCycles() = viewModelScope.launch {
        val loadedRepeatCycleList = todoRepository.loadRepeatCycles()
        val allRepeatCycles = DefaultRepeatCycles + loadedRepeatCycleList
        val usageOrder = configRepository.getRepeatCycleUsageOrder()
        val sortedRepeatCycles = allRepeatCycles.sortedByUsageOrder(usageOrder) { it.id }
        setState { copy(repeatCycleList = sortedRepeatCycles.toUiModels(resourceProvider)) }
    }

    override suspend fun processIntent(intent: AddTodoIntent) {
        when (intent) {
            AddTodoIntent.OnBackClick -> {
                analyticsHelper.logEvent(
                    AnalyticsEvent.Click(screenName = "AddTodo", buttonName = "Back")
                )
                navigationBus.navigate(NavigationEvent.Up)
            }

            is AddTodoIntent.OnSelectedDataChangeClick -> eventBus.sendEvent(
                ShowBottomSheet(intent.content)
            )

            is AddTodoIntent.OnSelectedDateChange -> onSelectedDateChange(intent.selectedDate)
            is AddTodoIntent.OnTitleChange -> onTitleChange(intent.title)
            is AddTodoIntent.OnSuggestionClick -> onSuggestionClick(intent.suggestion)
            is AddTodoIntent.OnPinnedChange -> onPinnedChange(intent.isPinned)
            is AddTodoIntent.OnRepeatCycleDropDownClick -> eventBus.sendEvent(
                ShowBottomSheet(intent.content)
            )

            is AddTodoIntent.OnRepeatCycleChange -> onRepeatCycleChange(intent.repeatCycle)
            is AddTodoIntent.OnRestDayChange -> onRestDayChange(intent.restDay)
            is AddTodoIntent.OnTagDropDownClick -> eventBus.sendEvent(
                ShowBottomSheet(intent.content)
            )

            is AddTodoIntent.OnTagChange -> onTagChange(intent.tag)
            AddTodoIntent.OnAddTagClick -> onAddTagClick()
            AddTodoIntent.OnSaveClick -> onSaveClick()
            AddTodoIntent.OnAddRepeatCycleClick -> onAddRepeatCycleClick()

            // Notification 관련 Intent
            AddTodoIntent.OnNotificationToggleClick -> onNotificationToggleClick()
            is AddTodoIntent.OnAlarmTimeChange -> onAlarmTimeChange(intent.hour, intent.minute)
            is AddTodoIntent.OnAlarmMessageChange -> onAlarmMessageChange(intent.message)
            AddTodoIntent.OnAlarmMessageReset -> onAlarmMessageReset()
            AddTodoIntent.OnNotificationBackClick -> onNotificationBackClick()
            AddTodoIntent.OnNotificationSaveClick -> onNotificationSaveClick()
        }
    }

    private suspend fun onSelectedDateChange(date: LocalDate) {
        eventBus.sendEvent(EbbingEvent.HideBottomSheet)

        setState { copy(selectedDate = date) }
    }

    private fun onTitleChange(title: String) {
        setState { copy(title = title) }
    }

    private fun onSuggestionClick(suggestion: ProgressSuggestion) {
        analyticsHelper.logEvent(
            AnalyticsEvent.Click(screenName = "AddTodo", buttonName = "NextProgressSuggestion")
        )

        setState {
            copy(
                title = suggestion.title,
                tag = suggestion.tag,
                isTagFromLast = true,
                repeatCycle = suggestion.repeatCycle ?: repeatCycle,
                isRepeatCycleFromLast = suggestion.repeatCycle != null,
            )
        }
    }

    private fun onPinnedChange(isPinned: Boolean) {
        setState { copy(isPinned = isPinned) }
    }

    private suspend fun onTagChange(todoTag: TodoTagUiModel) {
        eventBus.sendEvent(EbbingEvent.HideBottomSheet)

        setState { copy(tag = todoTag, isTagFromLast = false) }
    }

    private suspend fun onRepeatCycleChange(repeatCycle: RepeatCycleUiModel) {
        eventBus.sendEvent(EbbingEvent.HideBottomSheet)
        eventBus.awaitBottomSheetHidden()
        setState { copy(repeatCycle = repeatCycle, isRepeatCycleFromLast = false) }
    }

    private suspend fun onRestDayChange(restDay: DayOfWeek) {
        val origin = currentState.restDays.toMutableSet()

        val newRestDays = if (origin.contains(restDay)) {
            origin - restDay
        } else {
            origin + restDay
        }

        if (newRestDays.size == DayOfWeek.entries.size) {
            eventBus.sendEvent(EbbingEvent.ShowSnackBar(resourceProvider.getString(R.string.home_snackbar_all_rest_days)))
            return
        }

        setState { copy(restDays = newRestDays.toImmutableSet()) }
    }

    private suspend fun onAddTagClick() {
        eventBus.sendEvent(EbbingEvent.HideBottomSheet)
        navigationBus.navigate(NavigationEvent.To(TagGraph.AddTagRoute))
    }

    private suspend fun onAddRepeatCycleClick() {
        eventBus.sendEvent(EbbingEvent.HideBottomSheet)
        navigationBus.navigate(NavigationEvent.To(RepeatCycleGraph.AddRepeatCycleRoute))
    }

    private suspend fun onSaveClick() {
        analyticsHelper.logEvent(
            AnalyticsEvent.Click(
                screenName = "AddTodo",
                buttonName = "Save",
            )
        )

        if (!currentState.isSaveEnabled) {
            eventBus.sendEvent(EbbingEvent.ShowSnackBar(resourceProvider.getString(R.string.home_snackbar_required_fields)))
            return
        }

        val shouldShowNudge = configRepository.shouldShowNotificationNudge()

        if (shouldShowNudge) {
            setState { copy(page = AddTodoState.Page.NOTIFICATION) }
        } else {
            saveTodoAndNavigateHome()
        }
    }

    private suspend fun saveTodoAndNavigateHome() {
        val tag = currentState.tag ?: return
        todoRepository.addTodo(
            title = currentState.title,
            dates = currentState.schedules,
            tagId = tag.id,
            isPinned = currentState.isPinned,
            restDays = currentState.restDays.toSet(),
        )

        runCatching {
            configRepository.recordTagUsage(tag.id)
            currentState.repeatCycle?.let { configRepository.recordRepeatCycleUsage(it.id) }
        }

        val (hour, minute) = configRepository.getAlarmTime()
        currentState.schedules.forEach { schedule ->
            try {
                val triggerAtMillis = schedule
                    .atTime(LocalTime.of(hour, minute))
                    .atZone(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli()

                if (triggerAtMillis <= System.currentTimeMillis()) return@forEach

                alarmScheduler.scheduleDailyExact(
                    date = schedule,
                    triggerAtMillis = triggerAtMillis,
                )
            } catch (e: Exception) {
                Log.d("AddTodoViewModel", "알람 등록 실패: $schedule", e)
            }
        }

        val isFirstTodo = runCatching { configRepository.markFirstTodoAdded() }.getOrDefault(false)
        runCatching { configRepository.incrementTodoRegisteredCount() }

        eventBus.sendEvent(EbbingEvent.ShowSnackBar(savedSnackBarMessage(currentState.title)))
        navigationBus.navigate(
            NavigationEvent.To(
                route = HomeRoute(
                    workedDate = currentState.selectedDate.toFormattedString(),
                    showWidgetNudge = isFirstTodo,
                ),
                popUpTo = true,
            )
        )
    }

    /** 다음 진도를 알 수 있는 제목이면, 다음에 어디서부터 추천할지 알려줘요. */
    private fun savedSnackBarMessage(title: String): String {
        val nextProgress = suggestNextProgress(title)
        val start = nextProgress.start
        if (start == null || nextProgress.subject.isEmpty()) {
            return resourceProvider.getString(R.string.home_snackbar_todo_added)
        }

        val resId = if (nextProgress.subject.hasFinalConsonant()) {
            R.string.home_snackbar_next_progress_eul
        } else {
            R.string.home_snackbar_next_progress_reul
        }
        return resourceProvider.getString(resId, nextProgress.subject, start)
    }

    private fun onNotificationToggleClick() {
        analyticsHelper.logEvent(
            AnalyticsEvent.Click(
                screenName = "NotificationNudge",
                buttonName = "NotificationNudge_Toggle",
            )
        )

        setState {
            copy(
                notificationState = notificationState.copy(
                    notificationEnabled = !notificationState.notificationEnabled
                )
            )
        }
    }

    private fun onAlarmTimeChange(hour: Int, minute: Int) {
        analyticsHelper.logEvent(
            AnalyticsEvent.Click(
                screenName = "NotificationNudge",
                buttonName = "NotificationNudge_Toggle",
            )
        )

        setState {
            copy(
                notificationState = notificationState.copy(
                    alarmHour = hour,
                    alarmMinute = minute
                )
            )
        }
    }

    private fun onAlarmMessageChange(message: String) {
        setState {
            copy(
                notificationState = notificationState.copy(message = message)
            )
        }
    }

    private fun onAlarmMessageReset() {
        setState {
            copy(
                notificationState = notificationState.copy(
                    message = notificationState.defaultMessage
                )
            )
        }
    }

    private fun onNotificationBackClick() {
        setState { copy(page = AddTodoState.Page.ADD_TODO) }
    }

    private suspend fun onNotificationSaveClick() {
        val notificationState = currentState.notificationState

        configRepository.setNotificationEnabled(notificationState.notificationEnabled)

        if (notificationState.notificationEnabled) {
            configRepository.updateAlarmTime(
                notificationState.alarmHour.toString(),
                notificationState.alarmMinute.toString()
            )
            configRepository.updateAlarmMessage(notificationState.message)
        }

        analyticsHelper.logEvent(
            AnalyticsEvent.Click(
                screenName = "NotificationNudge",
                buttonName = "SaveNotification",
            )
        )
        saveTodoAndNavigateHome()
    }

    private companion object {
        const val MAX_SUGGESTION_COUNT = 5
    }
}
