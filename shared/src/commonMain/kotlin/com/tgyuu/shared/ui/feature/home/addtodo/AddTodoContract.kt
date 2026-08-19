package com.tgyuu.shared.ui.feature.home.addtodo

import androidx.compose.runtime.Immutable
import com.tgyuu.shared.base.UiIntent
import com.tgyuu.shared.base.UiState
import com.tgyuu.shared.domain.model.RepeatCycle
import com.tgyuu.shared.common.generateDailySchedules
import com.tgyuu.shared.common.generateValidSchedules
import com.tgyuu.shared.common.now
import com.tgyuu.shared.ui.model.RepeatCycleUiModel
import com.tgyuu.shared.ui.model.TodoTagUiModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate

@Immutable
data class AddTodoState(
    val page: Page = Page.ADD_TODO,
    val selectedDate: LocalDate = LocalDate.now(),
    val title: String = "",
    val isPinned: Boolean = false,
    val tag: TodoTagUiModel? = null,
    val tagList: ImmutableList<TodoTagUiModel> = persistentListOf(),
    val repeatCycleList: ImmutableList<RepeatCycleUiModel> = persistentListOf(),
    val repeatCycle: RepeatCycleUiModel? = null,
    val restDays: ImmutableSet<DayOfWeek> = persistentSetOf(),
    val notificationState: NotificationState = NotificationState(),
    val isLoading: Boolean = false,
) : UiState {
    val isSaveEnabled: Boolean = title.isNotEmpty()
    val isModified: Boolean = title.isNotEmpty() || isPinned || restDays.isNotEmpty()
    val schedules: List<LocalDate>
        get() = repeatCycle?.let {
            if (it.id == RepeatCycle.DAILY_REPEAT_ID) {
                generateDailySchedules(
                    baseDate = selectedDate,
                    intervals = it.intervals.toList(),
                    restDays = restDays.toSet()
                )
            } else {
                generateValidSchedules(
                    baseDate = selectedDate,
                    intervals = it.intervals.toList(),
                    restDays = restDays.toSet()
                )
            }
        } ?: emptyList()

    // Android AddTodoState.Page와 동일: 저장 후 알림 넛지 페이지로 전환
    enum class Page {
        ADD_TODO,
        NOTIFICATION,
    }
}

/**
 * Android home.graph.addtodo.contract.NotificationState 대응 (알림 넛지 페이지 상태).
 * defaultMessage/placeholderToken은 VM initNotificationState에서 리소스로 채운다.
 */
@Immutable
data class NotificationState(
    val notificationEnabled: Boolean = false,
    val alarmHour: Int = 0,
    val alarmMinute: Int = 0,
    val defaultMessage: String = "",
    val message: String = defaultMessage,
    val originMessage: String = defaultMessage,
    val placeholderToken: String = "{할일}",
    val isShowTimePicker: Boolean = false,
) {
    val placeholderCount: Int = Regex.escape(placeholderToken).toRegex().findAll(message).count()
    val isValidPlaceholder: Boolean = placeholderCount <= 1
    val isValidLength: Boolean = message.length <= 50
    val messageLength: Int = message.length
    val shouldShowResetButton: Boolean = message != defaultMessage
}

sealed class AddTodoIntent : UiIntent {
    data object OnBackClick : AddTodoIntent()
    data class OnSelectedDateChange(val selectedDate: LocalDate) : AddTodoIntent()
    data class OnTitleChange(val title: String) : AddTodoIntent()
    data class OnPinnedChange(val isPinned: Boolean) : AddTodoIntent()
    data object OnTagDropDownClick : AddTodoIntent()
    data class OnTagChange(val tag: TodoTagUiModel) : AddTodoIntent()
    data object OnAddTagClick : AddTodoIntent()
    data object OnRepeatCycleDropDownClick : AddTodoIntent()
    data class OnRepeatCycleChange(val repeatCycle: RepeatCycleUiModel) : AddTodoIntent()
    data object OnAddRepeatCycleClick : AddTodoIntent()
    data class OnRestDayChange(val restDay: DayOfWeek) : AddTodoIntent()
    data object OnSaveClick : AddTodoIntent()

    // 알림 넛지 페이지 (Android AddTodoIntent 대응)
    data object OnNotificationToggleClick : AddTodoIntent()
    data object OnAlarmTimePickerClick : AddTodoIntent()
    data object OnAlarmTimePickerDismiss : AddTodoIntent()
    data class OnAlarmTimeChange(val hour: Int, val minute: Int) : AddTodoIntent()
    data class OnAlarmMessageChange(val message: String) : AddTodoIntent()
    data object OnAlarmMessageReset : AddTodoIntent()
    data object OnNotificationBackClick : AddTodoIntent()
    data object OnNotificationSaveClick : AddTodoIntent()
}
