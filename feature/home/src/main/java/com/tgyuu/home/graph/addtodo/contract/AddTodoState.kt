package com.tgyuu.home.graph.addtodo.contract

import androidx.compose.runtime.Immutable
import com.tgyuu.common.base.UiState
import com.tgyuu.common.generateDailySchedules
import com.tgyuu.common.generateValidSchedules
import com.tgyuu.designsystem.model.RepeatCycleUiModel
import com.tgyuu.designsystem.model.TodoTagUiModel
import com.tgyuu.domain.model.RepeatCycle
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf
import java.time.DayOfWeek
import java.time.LocalDate

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
    val mondayStart: Boolean = false,
    val suggestions: ImmutableList<ProgressSuggestion> = persistentListOf(),
    val isTagFromLast: Boolean = false,
    val isRepeatCycleFromLast: Boolean = false,
) : UiState {
    val isSaveEnabled = title.isNotEmpty()
    val isModified = title.isNotEmpty() || isPinned || restDays.isNotEmpty()

    /** 입력 중인 제목과 겹치는 칩만 보여주고, 이미 칩 그대로 채워졌으면 숨겨요. */
    val visibleSuggestions: List<ProgressSuggestion>
        get() {
            val query = title.trim()
            if (query.isEmpty()) return suggestions
            return suggestions.filter {
                it.title != query && it.title.contains(query, ignoreCase = true)
            }
        }

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

    enum class Page {
        ADD_TODO,
        NOTIFICATION
    }
}

@Immutable
data class ProgressSuggestion(
    val title: String,
    val tag: TodoTagUiModel,
    val repeatCycle: RepeatCycleUiModel?,
)

@Immutable
data class NotificationState(
    val notificationEnabled: Boolean = false,
    val alarmHour: Int = 0,
    val alarmMinute: Int = 0,
    val defaultMessage: String = "",
    val message: String = defaultMessage,
    val originMessage: String = defaultMessage,
    val placeholderToken: String = "{할일}",
) {
    val formattedAlarmTime: String
        get() {
            val hour = alarmHour.toString().padStart(2, '0')
            val minute = alarmMinute.toString().padStart(2, '0')
            return "$hour:$minute"
        }

    val placeholderCount: Int = Regex.escape(placeholderToken).toRegex().findAll(message).count()

    val isValidPlaceholder: Boolean = placeholderCount <= 1

    val isValidLength: Boolean = message.length <= 50

    val messageLength: Int = message.length

    val shouldShowResetButton: Boolean = message != defaultMessage
}
