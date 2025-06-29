package com.tgyuu.designsystem.component.bottomsheet

import android.util.Log
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.tgyuu.common.event.BottomSheetContent

@Composable
fun rememberEbbingBottomSheetState(
    bottomSheetState: ModalBottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true,
    )
): EbbingBottomSheetState {
    return EbbingBottomSheetState(state = bottomSheetState)
}

class EbbingBottomSheetState(val state: ModalBottomSheetState) {
    var content by mutableStateOf<BottomSheetContent?>(null)
        private set

    fun setBottomSheetContent(sheetContent: BottomSheetContent) {
        this.content = sheetContent
    }

    suspend fun hide() {
        Log.d("test", "Hide 호출")
        state.hide()
    }

    suspend fun show() {
        Log.d("test", "show 호출 ${this.content}")
        state.show()
    }

}
