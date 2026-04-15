package com.tgyuu.shared.ui.feature.memo

import androidx.compose.runtime.Immutable
import com.tgyuu.shared.base.UiIntent
import com.tgyuu.shared.base.UiState
import com.tgyuu.shared.domain.model.Experiment
import com.tgyuu.shared.domain.model.TodoSchedule

@Immutable
data class MemoState(
    val originSchedule: TodoSchedule? = null,
    val memo: String = "",
    val saveButtonPositionVariant: Experiment.SaveButtonPosition.Variant = Experiment.SaveButtonPosition.Variant.CONTROL,
) : UiState {
    val isTreatment: Boolean = saveButtonPositionVariant == Experiment.SaveButtonPosition.Variant.TREATMENT
    val isSaveEnabled: Boolean
        get() = memo.isNotBlank()
}

sealed class MemoIntent : UiIntent {
    data object OnBackClick : MemoIntent()
    data class OnMemoChange(val memo: String) : MemoIntent()
    data object OnSaveClick : MemoIntent()
}
