package com.tgyuu.shared.ui.feature.repeatcycle.editrepeatcycle

import com.tgyuu.shared.base.UiIntent
import com.tgyuu.shared.base.UiState
import com.tgyuu.shared.domain.model.Experiment
import com.tgyuu.shared.domain.model.RepeatCycle
import com.tgyuu.shared.ui.feature.repeatcycle.addrepeatcycle.parsingIntervals

data class EditRepeatCycleState(
    val originRepeatCycle: RepeatCycle? = null,
    val intervals: String = "",
    val saveButtonPositionVariant: Experiment.SaveButtonPosition.Variant = Experiment.SaveButtonPosition.Variant.CONTROL,
) : UiState {
    val isTreatment: Boolean = saveButtonPositionVariant == Experiment.SaveButtonPosition.Variant.TREATMENT
    val parsedIntervals: List<Int> = parsingIntervals(intervals).getOrDefault(emptyList())

    // Android EditRepeatCycleState와 동일: 입력이 비어있지 않으면 활성화 (파싱 실패는 저장 시 스낵바로 안내)
    val isSaveEnabled: Boolean = intervals.isNotEmpty()
}

sealed interface EditRepeatCycleIntent : UiIntent {
    data object OnBackClick : EditRepeatCycleIntent
    data class OnIntervalsChange(val intervals: String) : EditRepeatCycleIntent
    data object OnUpdateClick : EditRepeatCycleIntent
}
