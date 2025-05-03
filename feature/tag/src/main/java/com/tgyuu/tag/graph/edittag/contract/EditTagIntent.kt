package com.tgyuu.tag.graph.edittag.contract

import com.tgyuu.common.base.UiIntent
import com.tgyuu.common.event.BottomSheetContent

sealed class EditTagIntent : UiIntent {
    data object OnBackClick : EditTagIntent()
    data class OnNameChange(val name: String) : EditTagIntent()
    data class OnColorDropDownClick(val content: BottomSheetContent) : EditTagIntent()
    data class OnColorChange(val colorValue: Int) : EditTagIntent()
    data object OnSaveClick : EditTagIntent()
}
