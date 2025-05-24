package com.tgyuu.memo.contract

import com.tgyuu.common.base.UiIntent

sealed interface MemoIntent : UiIntent {
    data object OnBackClick : MemoIntent
    data class OnMemoChange(val memo: String) : MemoIntent
    data object OnSaveClick : MemoIntent
}
