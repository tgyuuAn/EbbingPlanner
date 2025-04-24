package com.tgyuu.home.graph.addtag.contract

import com.tgyuu.common.base.UiIntent
import com.tgyuu.common.event.BottomSheetContent

sealed class AddTagIntent : UiIntent {
    data object OnBackClick : AddTagIntent()
    data class OnNameChange(val name: String) : AddTagIntent()
    data class OnColorDropDownClick(val content: BottomSheetContent) : AddTagIntent()
    data class OnColorChange(val colorValue: Int) : AddTagIntent()
    data object OnSaveClick : AddTagIntent()
}
