package com.tgyuu.tag.graph.addtag.contract

import com.tgyuu.common.base.UiState
import com.tgyuu.domain.model.DefaultTodoTag
import com.tgyuu.experiment.domain.model.Experiment
import com.tgyuu.experiment.domain.model.Experiment.SaveButtonPosition

data class AddTagState(
    val name: String = "",
    val colorValue: Int = DefaultTodoTag.color,
    val saveButtonPositionVariant: SaveButtonPosition.Variant = SaveButtonPosition.Variant.CONTROL,
) : UiState {
    val isTreatment = saveButtonPositionVariant == Experiment.SaveButtonPosition.Variant.TREATMENT
    val isSaveEnabled: Boolean = name.isNotEmpty()
}
