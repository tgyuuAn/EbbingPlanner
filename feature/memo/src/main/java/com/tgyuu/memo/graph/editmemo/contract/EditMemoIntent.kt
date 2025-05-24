package com.tgyuu.memo.graph.editmemo.contract

import com.tgyuu.common.base.UiIntent

sealed interface EditMemoIntent : UiIntent {
    data object OnBackClick : EditMemoIntent
    data class OnMemoChange(val memo: String) : EditMemoIntent
    data object OnUpdateClick : EditMemoIntent
}
