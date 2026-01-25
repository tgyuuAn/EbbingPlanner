package com.tgyuu.home.graph.addtodo.contract

import androidx.compose.runtime.Immutable
import com.tgyuu.common.base.UiState
import com.tgyuu.common.generateValidSchedules
import com.tgyuu.domain.model.DefaultRepeatCycles
import com.tgyuu.domain.model.DefaultTodoTag
import com.tgyuu.domain.model.RepeatCycle
import com.tgyuu.domain.model.TodoTag
import com.tgyuu.domain.repository.ConfigRepository.Companion.DEFAULT_ALARM_MESSAGE
import java.time.DayOfWeek
import java.time.LocalDate

data class AddTodoState(
    val page: Page = Page.ADD_TODO,
    val selectedDate: LocalDate = LocalDate.now(),
    val title: String = "",
    val priority: String? = null,
    val tag: TodoTag = DefaultTodoTag,
    val tagList: List<TodoTag> = emptyList(),
    val repeatCycleList: List<RepeatCycle> = DefaultRepeatCycles,
    val repeatCycle: RepeatCycle = DefaultRepeatCycles.first(),
    val restDays: Set<DayOfWeek> = emptySet(),
    val notificationState: NotificationState = NotificationState(),
) : UiState {
    val isSaveEnabled = title.isNotEmpty()
    val isModified = title.isNotEmpty() || !priority.isNullOrEmpty() || restDays.isNotEmpty()
    val schedules: List<LocalDate>
        get() = generateValidSchedules(
            baseDate = selectedDate,
            intervals = repeatCycle.intervals,
            restDays = restDays
        )

    enum class Page {
        ADD_TODO,
        NOTIFICATION
    }
}

@Immutable
data class NotificationState(
    val notificationEnabled: Boolean = false,
    val alarmHour: Int = 0,
    val alarmMinute: Int = 0,
    val message: String = DEFAULT_ALARM_MESSAGE,
    val originMessage: String = DEFAULT_ALARM_MESSAGE,
) {
    private val placeholderCount: Int = "\\{할일\\}".toRegex().findAll(message).count()

    val isValidPlaceholder: Boolean = placeholderCount <= 1

    val isValidLength: Boolean = message.length <= 50

    val isValid: Boolean = isValidPlaceholder && isValidLength

    val previewMessage: String = when {
        placeholderCount == 1 -> message.replace("{할일}", "영어 단어 복습")
        placeholderCount == 0 -> message
        else -> ""
    }

    val errorMessage: String = when {
        placeholderCount > 1 -> "{할일}은 최대 1번만 사용할 수 있습니다"
        !isValidLength -> "최대 50자까지 입력 가능합니다"
        else -> ""
    }

    val lengthText: String = "${message.length} / 50자"

    val isChanged: Boolean = message != originMessage

    val canApply: Boolean = isValid && isChanged

    val shouldShowResetButton: Boolean = message != DEFAULT_ALARM_MESSAGE
}