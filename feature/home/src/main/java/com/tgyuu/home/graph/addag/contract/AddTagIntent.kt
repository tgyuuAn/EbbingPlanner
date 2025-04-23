package com.tgyuu.home.graph.addag.contract

import com.tgyuu.common.base.UiIntent

sealed class AddTagIntent : UiIntent {
    data object OnBackClick : AddTagIntent()
    data object OnSaveClick : AddTagIntent()
}
