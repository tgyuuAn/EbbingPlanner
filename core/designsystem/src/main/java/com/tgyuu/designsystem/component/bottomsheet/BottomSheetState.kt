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

/**
 * 앱 전역 시트 상태. 폰은 바텀시트, 태블릿은 가운데 다이얼로그로 같은 content를 띄워요.
 * 표시 방식은 [EbbingAdaptiveSheetHost]가 화면 크기에 맞춰 [presentation]으로 정해요.
 */
class EbbingBottomSheetState(
    val state: ModalBottomSheetState,
) {
    var content by mutableStateOf<BottomSheetContent?>(null)
        private set

    var presentation by mutableStateOf(SheetPresentation.BottomSheet)
        internal set

    var isDialogVisible by mutableStateOf(false)
        private set

    val isVisible: Boolean
        get() = when (presentation) {
            SheetPresentation.BottomSheet -> state.isVisible
            SheetPresentation.Dialog -> isDialogVisible
        }

    fun setBottomSheetContent(newContent: BottomSheetContent) {
        content = newContent
    }

    suspend fun hide() = when (presentation) {
        SheetPresentation.BottomSheet -> state.hide()
        SheetPresentation.Dialog -> isDialogVisible = false
    }

    suspend fun show() = when (presentation) {
        SheetPresentation.BottomSheet -> state.show()
        SheetPresentation.Dialog -> isDialogVisible = true
    }
}

enum class SheetPresentation { BottomSheet, Dialog }
