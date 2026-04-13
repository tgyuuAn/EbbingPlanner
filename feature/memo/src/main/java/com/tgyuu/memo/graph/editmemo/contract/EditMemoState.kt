package com.tgyuu.memo.graph.editmemo.contract

import com.tgyuu.common.base.UiState
import com.tgyuu.domain.model.TodoSchedule
import com.tgyuu.experiment.domain.model.Experiment
import com.tgyuu.experiment.domain.model.Experiment.SaveButtonPosition

data class EditMemoState(
    val originSchedule: TodoSchedule? = null,
    val memo: String = "",
    val showSaveDialog: Boolean = false,
    val relatedScheduleCount: Int = 0,
    val saveButtonPositionVariant: SaveButtonPosition.Variant = SaveButtonPosition.Variant.CONTROL,
) : UiState {
    val isTreatment = saveButtonPositionVariant == Experiment.SaveButtonPosition.Variant.TREATMENT
    val isSaveEnabled = memo.isNotEmpty()
}
