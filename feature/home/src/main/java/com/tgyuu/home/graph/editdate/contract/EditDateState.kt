package com.tgyuu.home.graph.editdate.contract

import androidx.compose.runtime.Immutable
import com.tgyuu.common.base.UiState
import com.tgyuu.common.generateDailySchedules
import com.tgyuu.common.generateValidSchedules
import com.tgyuu.common.now
import com.tgyuu.designsystem.model.RepeatCycleUiModel
import com.tgyuu.domain.model.RepeatCycle
import com.tgyuu.experiment.domain.model.Experiment
import com.tgyuu.experiment.domain.model.Experiment.SaveButtonPosition
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
    val tagId: Int? = null,
    val selectedDate: LocalDate = LocalDate.now(),
    val repeatCycleList: ImmutableList<RepeatCycleUiModel> = persistentListOf(),
    val repeatCycle: RepeatCycleUiModel? = null,
    val restDays: ImmutableSet<DayOfWeek> = persistentSetOf(),
    val mondayStart: Boolean = false,
    val saveButtonPositionVariant: SaveButtonPosition.Variant = SaveButtonPosition.Variant.CONTROL,
) : UiState {
    val isTreatment = saveButtonPositionVariant == Experiment.SaveButtonPosition.Variant.TREATMENT
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
}
