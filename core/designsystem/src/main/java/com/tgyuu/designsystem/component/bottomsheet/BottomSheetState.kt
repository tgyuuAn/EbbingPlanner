package com.tgyuu.designsystem.component.bottomsheet

import android.util.Log
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
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
    init {
        Log.d("test", "ebbingBOttomSheetState 재생성")
    }

    private val _content = mutableStateOf<BottomSheetContent?>(null)
    val content: State<BottomSheetContent?> get() = _content

    fun setBottomSheetContent(content: BottomSheetContent) {
        _content.value = content
        Log.d("test", "Contet 갱신 ${this.content}")
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
