package com.tgyuu.designsystem.component.bottomsheet

import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.tgyuu.common.event.BottomSheetContent

@Composable
fun rememberEbbingBottomSheetState(
    bottomSheetState: ModalBottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true,
    ),
): EbbingBottomSheetState {
    return remember(bottomSheetState) { EbbingBottomSheetState(state = bottomSheetState) }
}

class EbbingBottomSheetState(
    val state: ModalBottomSheetState,
) {
    var content by mutableStateOf<BottomSheetContent?>(null)
        private set

    fun setBottomSheetContent(newContent: BottomSheetContent) {
        content = newContent
    }

    suspend fun hide() = state.hide()
    suspend fun show() = state.show()
}
