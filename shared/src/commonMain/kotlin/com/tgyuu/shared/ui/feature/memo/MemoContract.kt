package com.tgyuu.shared.ui.feature.memo

import androidx.compose.runtime.Immutable
import com.tgyuu.shared.base.UiIntent
import com.tgyuu.shared.base.UiState
import com.tgyuu.shared.domain.model.TodoSchedule

@Immutable
data class MemoState(
    val originSchedule: TodoSchedule? = null,
    val memo: String = "",
    val showSaveDialog: Boolean = false,
    val relatedScheduleCount: Int = 0,
    val isEditEntry: Boolean = false,
) : UiState {
    // Android AddMemoState/EditMemoState와 동일: 공백만 입력해도 저장 가능
    val isSaveEnabled: Boolean
        get() = memo.isNotEmpty()
}

sealed class MemoIntent : UiIntent {
    data object OnBackClick : MemoIntent()
    data class OnMemoChange(val memo: String) : MemoIntent()
    data object OnSaveClick : MemoIntent()
    data object OnDismissSaveDialog : MemoIntent()
    data object OnSaveToAllRelatedClick : MemoIntent()
    data object OnSaveToSingleClick : MemoIntent()
}
