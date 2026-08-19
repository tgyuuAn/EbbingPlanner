package com.tgyuu.shared.ui.feature.home.edittodo
import androidx.lifecycle.viewModelScope

import com.tgyuu.shared.base.BaseViewModel
import com.tgyuu.shared.common.currentInstant
import com.tgyuu.shared.common.loadTagsByUsage
import com.tgyuu.shared.domain.repository.ConfigRepository
import com.tgyuu.shared.domain.repository.ExperimentRepository
import com.tgyuu.shared.domain.repository.TodoRepository
import com.tgyuu.shared.platform.AnalyticsEvent
import com.tgyuu.shared.platform.AnalyticsHelper
import com.tgyuu.shared.platform.NotificationScheduler
import com.tgyuu.shared.ui.model.TodoScheduleUiModel
import com.tgyuu.shared.ui.model.TodoTagUiModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.collections.immutable.toImmutableSet
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.toInstant
import com.tgyuu.shared.domain.model.DefaultTodoTag
import ebbingplanner.shared.generated.resources.Res
import ebbingplanner.shared.generated.resources.alarm_placeholder_token
import ebbingplanner.shared.generated.resources.tag_unassigned
import ebbingplanner.shared.generated.resources.snack_date_has_schedule
import ebbingplanner.shared.generated.resources.snack_required_fields
import ebbingplanner.shared.generated.resources.snack_todo_update_failed
import ebbingplanner.shared.generated.resources.snack_todo_updated
import org.jetbrains.compose.resources.getString

class EditTodoViewModel(
    private val scheduleId: Int,
    private val todoRepository: TodoRepository,
    private val configRepository: ConfigRepository,
    private val notificationScheduler: NotificationScheduler,
    private val analyticsHelper: AnalyticsHelper? = null,
    private val onNavigateBack: () -> Unit,
    private val onNavigateToHome: (LocalDate) -> Unit = {},
    private val onNavigateToAddTag: () -> Unit = {},
    private val onShowSnackbar: (String) -> Unit = {},
    private val experimentRepository: ExperimentRepository? = null,
    private val onShowTagBottomSheet: (() -> Unit)? = null,
    private val onShowDateBottomSheet: (() -> Unit)? = null,
) : BaseViewModel<EditTodoState, EditTodoIntent>(EditTodoState()) {

    init {
        loadScheduleData()
        safeScope.launch {
            configRepository.getMondayStart().collect { setState { copy(mondayStart = it) } }
        }
    }

    private fun loadScheduleData() {
        safeScope.launch {
            val originSchedule = todoRepository.loadSchedule(scheduleId) ?: run {
                onNavigateBack()
                return@launch
            }

            val originTagDeferred = async { todoRepository.loadTag(originSchedule.tagId) }
            val sameInfoSchedulesDeferred =
                async { todoRepository.loadSchedulesByTodoInfo(originSchedule.infoId) }
            val todoInfoDeferred = async { todoRepository.loadTodoInfoById(originSchedule.infoId) }

            val originTag = originTagDeferred.await() ?: run {
                // Android과 동일: 태그를 찾을 수 없으면 뒤로가기(조용한 무동작 방지)
                onNavigateBack()
                return@launch
            }
            val schedulesByDateMap = sameInfoSchedulesDeferred.await()
            val todoInfo = todoInfoDeferred.await()

            val unassignedName = getString(Res.string.tag_unassigned)
            val tagModel = originTag.toUiModel()
                .let { if (it.id == DefaultTodoTag.id) it.copy(name = unassignedName) else it }

            setState {
                copy(
                    originSchedule = originSchedule,
                    schedulesByDateMap = schedulesByDateMap
                        .groupBy { it.date }
                        .mapValues { (_, list) -> list.map { it.toUiModel() }.toImmutableList() }
                        .toImmutableMap(),
                    selectedDate = originSchedule.date,
                    title = originSchedule.title,
                    isPinned = originSchedule.isPinned,
                    tag = tagModel,
                    restDays = todoInfo.restDays.toImmutableSet(),
                )
            }
            loadTags()
        }
    }

    private suspend fun loadTags() {
        val unassignedName = getString(Res.string.tag_unassigned)
        val models = todoRepository.loadTagsByUsage(configRepository)
            .map { it.toUiModel() }
            .map { if (it.id == DefaultTodoTag.id) it.copy(name = unassignedName) else it }
            .toImmutableList()
        setState {
            copy(tagList = models)
        }
    }

    // Android 대응: 태그 추가 화면 복귀 시 방금 추가한 태그 자동 선택(recentAddedTagId는 읽으면 소비됨).
    fun loadNewTag() {
        val id = todoRepository.recentAddedTagId?.toInt() ?: return
        safeScope.launch {
            val newTag = todoRepository.loadTag(id) ?: return@launch
            setState { copy(tag = newTag.toUiModel()) }
        }
    }

    override suspend fun processIntent(intent: EditTodoIntent) {
        when (intent) {
            EditTodoIntent.OnBackClick -> onNavigateBack()
            EditTodoIntent.OnSelectedDateDropDownClick -> onShowDateBottomSheet?.invoke()
            is EditTodoIntent.OnSelectedDateChange -> onSelectedDateChange(intent.selectedDate)
            is EditTodoIntent.OnTitleChange -> onTitleChange(intent.title)
            is EditTodoIntent.OnPinnedChange -> setState { copy(isPinned = intent.isPinned) }
            EditTodoIntent.OnTagDropDownClick -> onShowTagBottomSheet?.invoke()
            is EditTodoIntent.OnTagChange -> setState { copy(tag = intent.tag) }
            EditTodoIntent.OnAddTagClick -> onNavigateToAddTag()
            EditTodoIntent.OnSaveClick -> onSaveClick()
        }
    }

    private fun onSelectedDateChange(date: LocalDate) {
        if (date == currentState.selectedDate) return

        val scheduledDates: Set<LocalDate> = currentState.schedulesByDateMap[date]
            ?.map { it.date }
            ?.toSet()
            ?: emptySet()

        if (date in scheduledDates) {
            viewModelScope.launch { onShowSnackbar(getString(Res.string.snack_date_has_schedule)) }
            return
        }

        setState { copy(selectedDate = date) }
    }

    private fun onTitleChange(title: String) {
        setState { copy(title = title) }
    }

    private suspend fun onSaveClick() {
        analyticsHelper?.logEvent(
            AnalyticsEvent(
                type = AnalyticsEvent.Types.BUTTON_CLICK,
                properties = mapOf(
                    AnalyticsEvent.PropertiesKeys.SCREEN_NAME to "EditTodo",
                    AnalyticsEvent.PropertiesKeys.BUTTON_NAME to "Save",
                ),
            )
        )

        if (!currentState.isSaveEnabled) {
            onShowSnackbar(getString(Res.string.snack_required_fields))
            return
        }

        val tag = currentState.tag ?: return
        val originSchedule = currentState.originSchedule ?: return
        val newSchedule = originSchedule.copy(
            title = currentState.title,
            date = currentState.selectedDate,
            tagId = tag.id,
            name = tag.name,
            color = tag.color,
            isPinned = currentState.isPinned,
        )

        try {
            todoRepository.updateTodo(newSchedule)
            todoRepository.updateTodoInfo(newSchedule, currentState.restDays.toSet())
            // 저장 완료 후 부가 기록 실패가 완료 흐름을 막지 않도록 격리
            runCatching { configRepository.recordTagUsage(tag.id) }

            // 날짜가 바뀌었으면 알림 재예약 (Android EditTodoViewModel 대응)
            runCatching { rescheduleAlarmOnDateChange(originSchedule.date, newSchedule.date, newSchedule.title) }

            onShowSnackbar(getString(Res.string.snack_todo_updated))
            onNavigateToHome(currentState.selectedDate)
        } catch (e: Exception) {
            onShowSnackbar(getString(Res.string.snack_todo_update_failed))
        }
    }

    /**
     * 일정 날짜 변경 시 기존 날짜의 알림을 취소하고, 알림이 켜져 있으며 새 날짜가 미래면 재등록한다.
     * Android는 새 알림만 등록(구 알림 미취소)하지만, iOS는 stale 알림 방지를 위해 구 알림도 취소한다.
     */
    private suspend fun rescheduleAlarmOnDateChange(oldDate: LocalDate, newDate: LocalDate, title: String) {
        if (newDate == oldDate) return

        // 기존 날짜 알림 취소 (id = date.hashCode(), AddTodo 예약과 동일 규칙)
        notificationScheduler.cancelNotification(oldDate.hashCode())

        val enabled = configRepository.getNotificationEnabled().first()
        if (!enabled) return

        val (hour, minute) = configRepository.getAlarmTime()
        if (newDate.atTime(hour, minute).toInstant(TimeZone.currentSystemDefault()) <= currentInstant()) return

        val storedMessage = configRepository.getAlarmMessage()
        val token = getString(Res.string.alarm_placeholder_token)
        notificationScheduler.scheduleNotification(
            id = newDate.hashCode(),
            title = title,
            message = storedMessage.replace(token, title),
            hour = hour,
            minute = minute,
            date = newDate,
        )
    }

    private fun com.tgyuu.shared.domain.model.TodoTag.toUiModel() = TodoTagUiModel(
        id = id,
        name = name,
        color = color,
        createdAt = createdAt,
    )

    private fun com.tgyuu.shared.domain.model.TodoSchedule.toUiModel() = TodoScheduleUiModel(
        id = id,
        infoId = infoId,
        date = date,
        title = title,
        tagId = tagId,
        name = name,
        color = color,
        isPinned = isPinned,
        isDone = isDone,
        memo = memo,
        createdAt = createdAt,
        infoCreatedAt = infoCreatedAt,
    )
}
