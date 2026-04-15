package com.tgyuu.shared.ui.feature.home.addtodo

import androidx.compose.runtime.Immutable
import com.tgyuu.shared.base.UiIntent
import com.tgyuu.shared.base.UiState
import com.tgyuu.shared.domain.model.Experiment
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
    val selectedDate: LocalDate = LocalDate.now(),
    val title: String = "",
    val priority: String = "",
    val tag: TodoTagUiModel? = null,
    val tagList: ImmutableList<TodoTagUiModel> = persistentListOf(),
    val repeatCycleList: ImmutableList<RepeatCycleUiModel> = persistentListOf(),
    val repeatCycle: RepeatCycleUiModel? = null,
    val restDays: ImmutableSet<DayOfWeek> = persistentSetOf(),
    val isLoading: Boolean = false,
    val saveButtonPositionVariant: Experiment.SaveButtonPosition.Variant = Experiment.SaveButtonPosition.Variant.CONTROL,
) : UiState {
    val isTreatment: Boolean = saveButtonPositionVariant == Experiment.SaveButtonPosition.Variant.TREATMENT
    val isSaveEnabled: Boolean = title.isNotEmpty()
    val isModified: Boolean = title.isNotEmpty() || priority.isNotEmpty() || restDays.isNotEmpty()
    val schedules: List<LocalDate>
        get() = repeatCycle?.let {
            generateValidSchedules(
                baseDate = selectedDate,
                intervals = it.intervals.toList(),
                restDays = restDays.toSet()
            )
        } ?: emptyList()
}

sealed class AddTodoIntent : UiIntent {
    data object OnBackClick : AddTodoIntent()
    data class OnSelectedDateChange(val selectedDate: LocalDate) : AddTodoIntent()
    data class OnTitleChange(val title: String) : AddTodoIntent()
    data class OnPriorityChange(val priority: String) : AddTodoIntent()
    data object OnTagDropDownClick : AddTodoIntent()
    data class OnTagChange(val tag: TodoTagUiModel) : AddTodoIntent()
    data object OnAddTagClick : AddTodoIntent()
    data object OnRepeatCycleDropDownClick : AddTodoIntent()
    data class OnRepeatCycleChange(val repeatCycle: RepeatCycleUiModel) : AddTodoIntent()
    data object OnAddRepeatCycleClick : AddTodoIntent()
    data class OnRestDayChange(val restDay: DayOfWeek) : AddTodoIntent()
    data object OnSaveClick : AddTodoIntent()
}
