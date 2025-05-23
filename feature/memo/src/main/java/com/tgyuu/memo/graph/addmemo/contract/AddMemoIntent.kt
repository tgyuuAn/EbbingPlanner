package com.tgyuu.memo.graph.addmemo.contract

import com.tgyuu.common.base.UiIntent

sealed interface AddMemoIntent : UiIntent {
    data object OnBackClick : AddMemoIntent
    data class OnMemoChange(val memo: String) : AddMemoIntent
    data object OnSaveClick : AddMemoIntent
}
