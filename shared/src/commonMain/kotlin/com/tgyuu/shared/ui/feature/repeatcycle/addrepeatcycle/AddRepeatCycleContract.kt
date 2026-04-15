package com.tgyuu.shared.ui.feature.repeatcycle.addrepeatcycle

import com.tgyuu.shared.base.UiIntent
import com.tgyuu.shared.base.UiState
import com.tgyuu.shared.domain.model.Experiment
import com.tgyuu.shared.domain.model.RepeatCycle

data class AddRepeatCycleState(
    val intervals: String = "",
    val saveButtonPositionVariant: Experiment.SaveButtonPosition.Variant = Experiment.SaveButtonPosition.Variant.CONTROL,
) : UiState {
    val isTreatment: Boolean = saveButtonPositionVariant == Experiment.SaveButtonPosition.Variant.TREATMENT
    val previewRepeatCycle: String = parsingIntervals(intervals)
        .getOrDefault(emptyList())
        .toPreviewIntervals()

    val isSaveEnabled: Boolean = intervals.isNotEmpty() && previewRepeatCycle != RepeatCycle.DISPLAY_ERROR
}

sealed interface AddRepeatCycleIntent : UiIntent {
    data object OnBackClick : AddRepeatCycleIntent
    data class OnIntervalsChange(val intervals: String) : AddRepeatCycleIntent
    data object OnSaveClick : AddRepeatCycleIntent
}

internal fun parsingIntervals(intervals: String): Result<List<Int>> = runCatching {
    intervals.split(",")
        .asSequence()
        .map { it.trim() }
        .filter { it.isNotBlank() }
        .filter { it.length < 4 }
        .map { it.toInt() }
        .distinct()
        .sorted()
        .toList()
}

internal fun List<Int>.toPreviewIntervals(): String {
    if (isEmpty()) return RepeatCycle.DISPLAY_ERROR

    return when {
        this.size == 1 && this.first() == 0 -> "당일만"
        else -> this.joinToString(", ") { day ->
            if (day == 0) "당일" else "${day}일"
        }
    }
}
