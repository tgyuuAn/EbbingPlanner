package com.tgyuu.shared.designsystem.component.bottomsheet

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

typealias BottomSheetContent = @Composable () -> Unit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun rememberEbbingBottomSheetState(
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
): EbbingBottomSheetState {
    return remember(sheetState) { EbbingBottomSheetState(state = sheetState) }
}

@OptIn(ExperimentalMaterial3Api::class)
class EbbingBottomSheetState(
    val state: SheetState,
) {
    var content by mutableStateOf<BottomSheetContent?>(null)
        private set

    var isVisible by mutableStateOf(false)
        private set

    fun setBottomSheetContent(newContent: BottomSheetContent) {
        content = newContent
    }

    suspend fun hide() {
        state.hide()
        isVisible = false
    }

    suspend fun show() {
        isVisible = true
        // state.show() is called via LaunchedEffect in EbbingModalBottomSheet
        // to ensure animation works after composition
    }

    fun showSync() {
        isVisible = true
    }

    fun hideSync() {
        isVisible = false
    }
}
