package com.tgyuu.shared.ui.feature.home.editdate

import androidx.compose.runtime.Immutable
import com.tgyuu.shared.base.UiIntent
import com.tgyuu.shared.base.UiState
import com.tgyuu.shared.domain.model.Experiment
import com.tgyuu.shared.common.generateValidSchedules
import com.tgyuu.shared.common.now
import com.tgyuu.shared.ui.model.RepeatCycleUiModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate

@Immutable
data class EditDateState(
    val title: String = "",
    val originTagColor: Int = 0XFFBBE1FA.toInt(),
    val selectedDate: LocalDate = LocalDate.now(),
    val repeatCycleList: ImmutableList<RepeatCycleUiModel> = persistentListOf(),
    val repeatCycle: RepeatCycleUiModel? = null,
    val restDays: ImmutableSet<DayOfWeek> = persistentSetOf(),
    val isLoading: Boolean = false,
    val saveButtonPositionVariant: Experiment.SaveButtonPosition.Variant = Experiment.SaveButtonPosition.Variant.CONTROL,
) : UiState {
    val isTreatment: Boolean = saveButtonPositionVariant == Experiment.SaveButtonPosition.Variant.TREATMENT
    val schedules: List<LocalDate>
        get() = repeatCycle?.let {
            generateValidSchedules(
                baseDate = selectedDate,
                intervals = it.intervals.toList(),
                restDays = restDays.toSet()
            )
        } ?: emptyList()
}

sealed class EditDateIntent : UiIntent {
    data object OnBackClick : EditDateIntent()
    data object OnSelectedDateDropDownClick : EditDateIntent()
    data class OnSelectedDateChange(val selectedDate: LocalDate) : EditDateIntent()
    data object OnRepeatCycleDropDownClick : EditDateIntent()
    data class OnRepeatCycleChange(val repeatCycle: RepeatCycleUiModel) : EditDateIntent()
    data object OnAddRepeatCycleClick : EditDateIntent()
    data class OnRestDayChange(val restDay: DayOfWeek) : EditDateIntent()
    data class OnSaveClick(val isDoneSchedules: List<Boolean>) : EditDateIntent()
}
