package com.tgyuu.shared.ui.feature.home.addtodo
import androidx.lifecycle.viewModelScope

import com.tgyuu.shared.base.BaseViewModel
import com.tgyuu.shared.domain.AlarmRescheduler
import com.tgyuu.shared.domain.repository.ConfigRepository
import com.tgyuu.shared.domain.repository.ExperimentRepository
import com.tgyuu.shared.domain.model.DefaultRepeatCycles
import com.tgyuu.shared.domain.model.DefaultTodoTag
import com.tgyuu.shared.domain.repository.TodoRepository
import com.tgyuu.shared.platform.AnalyticsEvent
import com.tgyuu.shared.platform.AnalyticsHelper
import com.tgyuu.shared.platform.NotificationScheduler
import com.tgyuu.shared.platform.NotificationPermissionStatus
import com.tgyuu.shared.platform.openAppSettings
import com.tgyuu.shared.navigation.AppExternalEvents
import com.tgyuu.shared.ui.model.RepeatCycleUiModel
import com.tgyuu.shared.ui.model.TodoTagUiModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableSet
import kotlinx.coroutines.launch
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import ebbingplanner.shared.generated.resources.Res
import ebbingplanner.shared.generated.resources.alarm_placeholder_token
import ebbingplanner.shared.generated.resources.default_alarm_message
import ebbingplanner.shared.generated.resources.tag_unassigned
import ebbingplanner.shared.generated.resources.snack_all_rest_days
import ebbingplanner.shared.generated.resources.snack_required_fields
import ebbingplanner.shared.generated.resources.snack_todo_add_failed
import ebbingplanner.shared.generated.resources.snack_todo_added
import org.jetbrains.compose.resources.getString
import com.tgyuu.shared.common.sortedByUsageOrder
import com.tgyuu.shared.designsystem.model.toDisplayName

class AddTodoViewModel(
    private val selectedDate: LocalDate,
    private val todoRepository: TodoRepository,
    private val configRepository: ConfigRepository,
    private val notificationScheduler: NotificationScheduler,
    private val analyticsHelper: AnalyticsHelper? = null,
    private val onNavigateBack: () -> Unit,
    /** 저장 후 홈 이동: (작업한 날짜, 첫 일정 여부 → 위젯 넛지 노출) — Android HomeRoute(workedDate, showWidgetNudge) 대응 */
    private val onNavigateToHome: (LocalDate, Boolean) -> Unit = { _, _ -> },
    private val onNavigateToAddTag: () -> Unit = {},
    private val onNavigateToAddRepeatCycle: () -> Unit = {},
    private val onShowSnackbar: (String) -> Unit = {},
    private val experimentRepository: ExperimentRepository? = null,
    private val alarmRescheduler: AlarmRescheduler? = null,
) : BaseViewModel<AddTodoState, AddTodoIntent>(AddTodoState(selectedDate = selectedDate)) {

    private var pendingEnableAfterSettings = false

    init {
        loadInitialData()
        initNotificationState()
        safeScope.launch {
            configRepository.getMondayStart().collect { setState { copy(mondayStart = it) } }
        }
        safeScope.launch {
            AppExternalEvents.foreground.collect { refreshNotificationPermission() }
        }
    }

    // Android initNotificationState 대응: 저장된 알림 시간/문구/기본 문구/플레이스홀더 토큰 로드
    private fun initNotificationState() {
        safeScope.launch {
            val (hour, minute) = configRepository.getAlarmTime()
            // Android AddTodoViewModel.initNotificationState와 동일: 저장 sentinel은 로케일별 기본 문구로 치환
            val defaultMessage = getString(Res.string.default_alarm_message)
            val placeholderToken = getString(Res.string.alarm_placeholder_token)
            val storedMessage = configRepository.getAlarmMessage()
            val message = if (storedMessage.isBlank() || storedMessage == ConfigRepository.DEFAULT_ALARM_MESSAGE) {
                defaultMessage
            } else {
                storedMessage
            }

            // Android initNotificationState와 동일: 토글은 기본 off로 시작(넛지에서 opt-in)
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
    }

    private fun loadInitialData() {
        safeScope.launch {
            // 태그·반복 주기와 각 사용 이력을 한 번씩만 조회해 목록 정렬과 초기 선택을 함께 계산한다.
            val tags = todoRepository.loadTags()
            val tagOrder = configRepository.getTagUsageOrder()
            val allRepeatCycles = DefaultRepeatCycles + todoRepository.loadRepeatCycles()
            val cycleOrder = configRepository.getRepeatCycleUsageOrder()

            // 사용 이력에 삭제된 id가 남아 있을 수 있으므로, 아직 존재하는 가장 최근 항목을 선택한다.
            val selectedTag = tagOrder.firstNotNullOfOrNull { id -> tags.find { it.id == id } }
                ?: DefaultTodoTag
            val selectedCycle = cycleOrder.firstNotNullOfOrNull { id -> allRepeatCycles.find { it.id == id } }
                ?: DefaultRepeatCycles.first()

            // Android와 동일: 기본(미지정) 태그는 로컬라이즈된 이름으로 표시
            val unassignedName = getString(Res.string.tag_unassigned)
            fun TodoTagUiModel.localizedIfDefault() =
                if (id == DefaultTodoTag.id) copy(name = unassignedName) else this

            val tagModel = selectedTag.toUiModel().localizedIfDefault()
            val cycleModel = selectedCycle.toUiModel()
            val tagList = tags.sortedByUsageOrder(tagOrder) { it.id }
                .map { it.toUiModel().localizedIfDefault() }.toImmutableList()
            val cycleList = allRepeatCycles.sortedByUsageOrder(cycleOrder) { it.id }
                .map { it.toUiModel() }.toImmutableList()

            setState {
                copy(
                    tag = tagModel,
                    repeatCycle = cycleModel,
                    tagList = tagList,
                    repeatCycleList = cycleList,
                )
            }
        }
    }

    // Android AddTodoViewModel.loadNewTag/loadNewRepeatCycle 대응:
    // 태그/반복주기 추가 화면에서 복귀 시 방금 추가한 항목을 자동 선택(recentAddedId는 읽으면 소비됨).
    fun loadNewTag() {
        val id = todoRepository.recentAddedTagId?.toInt() ?: return
        safeScope.launch {
            val newTag = todoRepository.loadTag(id) ?: return@launch
            setState { copy(tag = newTag.toUiModel()) }
        }
    }

    fun loadNewRepeatCycle() {
        val id = todoRepository.recentAddedRepeatCycleId?.toInt() ?: return
        safeScope.launch {
            val cycleModel = todoRepository.loadRepeatCycle(id).toUiModel()
            setState { copy(repeatCycle = cycleModel) }
        }
    }

    // Android AddTodoViewModel.loadTags/loadRepeatCycles 대응: 화면에 다시 들어올 때마다 목록만 새로 불러와요(선택은 유지)
    fun loadTags() {
        safeScope.launch {
            val tags = todoRepository.loadTags()
            val tagOrder = configRepository.getTagUsageOrder()
            val unassignedName = getString(Res.string.tag_unassigned)
            val tagList = tags.sortedByUsageOrder(tagOrder) { it.id }
                .map { tag ->
                    tag.toUiModel().let { if (it.id == DefaultTodoTag.id) it.copy(name = unassignedName) else it }
                }
                .toImmutableList()
            setState { copy(tagList = tagList) }
        }
    }

    fun loadRepeatCycles() {
        safeScope.launch {
            val allRepeatCycles = DefaultRepeatCycles + todoRepository.loadRepeatCycles()
            val cycleOrder = configRepository.getRepeatCycleUsageOrder()
            val cycleList = allRepeatCycles.sortedByUsageOrder(cycleOrder) { it.id }
                .map { it.toUiModel() }
                .toImmutableList()
            setState { copy(repeatCycleList = cycleList) }
        }
    }

    override suspend fun processIntent(intent: AddTodoIntent) {
        when (intent) {
            AddTodoIntent.OnBackClick -> onNavigateBack()
            is AddTodoIntent.OnSelectedDateChange -> setState { copy(selectedDate = intent.selectedDate) }
            is AddTodoIntent.OnTitleChange -> onTitleChange(intent.title)
            is AddTodoIntent.OnPinnedChange -> setState { copy(isPinned = intent.isPinned) }
            is AddTodoIntent.OnTagChange -> setState { copy(tag = intent.tag) }
            AddTodoIntent.OnAddTagClick -> onNavigateToAddTag()
            is AddTodoIntent.OnRepeatCycleChange -> setState { copy(repeatCycle = intent.repeatCycle) }
            AddTodoIntent.OnAddRepeatCycleClick -> onNavigateToAddRepeatCycle()
            is AddTodoIntent.OnRestDayChange -> onRestDayChange(intent.restDay)
            AddTodoIntent.OnSaveClick -> onSaveClick()

            AddTodoIntent.OnNotificationToggleClick -> onNotificationToggleClick()
            AddTodoIntent.OnAlarmTimePickerClick -> setState {
                copy(notificationState = notificationState.copy(isShowTimePicker = true))
            }
            AddTodoIntent.OnAlarmTimePickerDismiss -> setState {
                copy(notificationState = notificationState.copy(isShowTimePicker = false))
            }
            is AddTodoIntent.OnAlarmTimeChange -> setState {
                copy(
                    notificationState = notificationState.copy(
                        alarmHour = intent.hour,
                        alarmMinute = intent.minute,
                        isShowTimePicker = false,
                    )
                )
            }
            is AddTodoIntent.OnAlarmMessageChange -> setState {
                copy(notificationState = notificationState.copy(message = intent.message))
            }
            AddTodoIntent.OnAlarmMessageReset -> setState {
                copy(notificationState = notificationState.copy(message = notificationState.defaultMessage))
            }
            AddTodoIntent.OnNotificationBackClick -> setState { copy(page = AddTodoState.Page.ADD_TODO) }
            AddTodoIntent.OnNotificationSaveClick -> onNotificationSaveClick()
        }
    }

    private fun onTitleChange(title: String) {
        setState { copy(title = title) }
    }

    private fun onRestDayChange(restDay: DayOfWeek) {
        val origin = currentState.restDays.toMutableSet()
        val newRestDays = if (origin.contains(restDay)) {
            origin - restDay
        } else {
            origin + restDay
        }

        if (newRestDays.size == DayOfWeek.entries.size) {
            viewModelScope.launch { onShowSnackbar(getString(Res.string.snack_all_rest_days)) }
            return
        }

        setState { copy(restDays = newRestDays.toImmutableSet()) }
    }

    private fun logClick(buttonName: String) {
        analyticsHelper?.logEvent(
            AnalyticsEvent(
                type = AnalyticsEvent.Types.BUTTON_CLICK,
                properties = mapOf(
                    AnalyticsEvent.PropertiesKeys.SCREEN_NAME to "AddTodo",
                    AnalyticsEvent.PropertiesKeys.BUTTON_NAME to buttonName,
                ),
            )
        )
    }

    private suspend fun onSaveClick() {
        logClick("Save")

        if (!currentState.isSaveEnabled) {
            onShowSnackbar(getString(Res.string.snack_required_fields))
            return
        }

        if (currentState.tag == null) return

        // Android와 동일: 최초 저장 시 알림 넛지 노출 → 넛지 페이지, 아니면 바로 저장
        if (configRepository.shouldShowNotificationNudge()) {
            analyticsHelper?.logEvent(
                AnalyticsEvent(
                    type = AnalyticsEvent.Types.SCREEN_VIEW,
                    properties = mapOf(AnalyticsEvent.PropertiesKeys.SCREEN_NAME to "NotificationNudge"),
                )
            )
            setState { copy(page = AddTodoState.Page.NOTIFICATION) }
        } else {
            saveTodoAndNavigateHome()
        }
    }

    private fun onNotificationToggleClick() {
        val desiredOn = !currentState.notificationState.notificationEnabled
        if (desiredOn) {
            // Android NotificationScreen과 동일: 허용 → 켜기, 최초 → 시스템 권한 요청, 이미 거부 → 앱 설정으로 이동
            safeScope.launch {
                when (notificationScheduler.getPermissionStatus()) {
                    NotificationPermissionStatus.GRANTED -> setNotificationEnabled(true)
                    NotificationPermissionStatus.NOT_DETERMINED -> {
                        setNotificationEnabled(notificationScheduler.requestPermission())
                    }
                    NotificationPermissionStatus.DENIED -> {
                        pendingEnableAfterSettings = true
                        openAppSettings()
                    }
                }
            }
        } else {
            setState { copy(notificationState = notificationState.copy(notificationEnabled = false)) }
        }
    }

    private fun setNotificationEnabled(enabled: Boolean) {
        setState { copy(notificationState = notificationState.copy(notificationEnabled = enabled)) }
    }

    /** 앱 설정에서 돌아왔을 때 권한이 허용됐으면 토글을 켜요 (Android pendingNotificationEnable 대응) */
    fun refreshNotificationPermission() {
        if (!pendingEnableAfterSettings) return
        safeScope.launch {
            if (notificationScheduler.getPermissionStatus() == NotificationPermissionStatus.GRANTED) {
                pendingEnableAfterSettings = false
                setNotificationEnabled(true)
            }
        }
    }

    private suspend fun onNotificationSaveClick() {
        val notificationState = currentState.notificationState

        configRepository.setNotificationEnabled(notificationState.notificationEnabled)
        if (notificationState.notificationEnabled) {
            configRepository.updateAlarmTime(
                notificationState.alarmHour.toString(),
                notificationState.alarmMinute.toString(),
            )
            configRepository.updateAlarmMessage(notificationState.message)
        }

        // 알림 설정이 바뀌었을 수 있으므로 전체 날짜를 다시 계산한다
        saveTodoAndNavigateHome(rescheduleAll = true)
    }

    private suspend fun saveTodoAndNavigateHome(rescheduleAll: Boolean = false) {
        val tag = currentState.tag ?: return
        try {
            val schedules = currentState.schedules
            todoRepository.addTodo(
                title = currentState.title,
                dates = schedules,
                tagId = tag.id,
                isPinned = currentState.isPinned,
                restDays = currentState.restDays.toSet(),
            )

            // 저장 완료 후 부가 기록 실패가 완료 흐름을 막지 않도록 격리
            runCatching {
                configRepository.recordTagUsage(tag.id)
                currentState.repeatCycle?.let { configRepository.recordRepeatCycleUsage(it.id) }
            }
            val isFirstTodo = runCatching { configRepository.markFirstTodoAdded() }.getOrDefault(false)
            // Android와 동일: 인앱 리뷰 노출 조건(등록 횟수 3회 이상)용 카운트
            runCatching { configRepository.incrementTodoRegisteredCount() }

            // 알림 재계산: 날짜당 1개, 그 날 미완료 일정 기준(Android 발화 시점 결정과 동일한 결과)
            if (rescheduleAll) alarmRescheduler?.rescheduleAll()
            else alarmRescheduler?.rescheduleDates(schedules)

            onShowSnackbar(getString(Res.string.snack_todo_added))
            onNavigateToHome(currentState.selectedDate, isFirstTodo)
        } catch (e: Exception) {
            onShowSnackbar(getString(Res.string.snack_todo_add_failed, e.message ?: ""))
        }
    }

    private fun com.tgyuu.shared.domain.model.TodoTag.toUiModel() = TodoTagUiModel(
        id = id,
        name = name,
        color = color,
        createdAt = createdAt,
    )

    private suspend fun com.tgyuu.shared.domain.model.RepeatCycle.toUiModel(): RepeatCycleUiModel {
        return RepeatCycleUiModel(
            id = id,
            intervals = intervals.toImmutableList(),
            displayName = toDisplayName(),
        )
    }
}
