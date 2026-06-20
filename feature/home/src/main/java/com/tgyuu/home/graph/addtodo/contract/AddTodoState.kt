package com.tgyuu.home.graph.addtodo.contract

import androidx.compose.runtime.Immutable
import com.tgyuu.common.base.UiState
import com.tgyuu.common.generateDailySchedules
import com.tgyuu.common.generateValidSchedules
import com.tgyuu.common.now
import com.tgyuu.designsystem.model.RepeatCycleUiModel
import com.tgyuu.designsystem.model.TodoTagUiModel
import com.tgyuu.domain.model.RepeatCycle
import com.tgyuu.domain.repository.ConfigRepository.Companion.DEFAULT_ALARM_MESSAGE
import com.tgyuu.experiment.domain.model.Experiment
import com.tgyuu.experiment.domain.model.Experiment.SaveButtonPosition
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
    val priority: String? = null,
    val tag: TodoTagUiModel? = null,
    val tagList: ImmutableList<TodoTagUiModel> = persistentListOf(),
    val repeatCycleList: ImmutableList<RepeatCycleUiModel> = persistentListOf(),
    val repeatCycle: RepeatCycleUiModel? = null,
    val restDays: ImmutableSet<DayOfWeek> = persistentSetOf(),
    val notificationState: NotificationState = NotificationState(),
    val mondayStart: Boolean = false,
    val saveButtonPositionVariant: SaveButtonPosition.Variant = SaveButtonPosition.Variant.CONTROL,
) : UiState {
    val isTreatment = saveButtonPositionVariant == Experiment.SaveButtonPosition.Variant.TREATMENT
    val isSaveEnabled = title.isNotEmpty()
    val isModified = title.isNotEmpty() || !priority.isNullOrEmpty() || restDays.isNotEmpty()
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
data class NotificationState(
    val notificationEnabled: Boolean = false,
    val alarmHour: Int = 0,
    val alarmMinute: Int = 0,
    val message: String = DEFAULT_ALARM_MESSAGE,
    val originMessage: String = DEFAULT_ALARM_MESSAGE,
) {
    val formattedAlarmTime: String
        get() {
            val hour = alarmHour.toString().padStart(2, '0')
            val minute = alarmMinute.toString().padStart(2, '0')
            return "$hour:$minute"
        }

    val nudgeText: String = "바쁜 날에도\n복습일을 자동으로 챙겨드릴게요"
    private val placeholderCount: Int = "\\{할일\\}".toRegex().findAll(message).count()

    val isValidPlaceholder: Boolean = placeholderCount <= 1

    val isValidLength: Boolean = message.length <= 50

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

    val shouldShowResetButton: Boolean = message != DEFAULT_ALARM_MESSAGE
}
