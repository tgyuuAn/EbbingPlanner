package com.tgyuu.sync.contract

import com.tgyuu.common.base.UiIntent
import com.tgyuu.common.event.BottomSheetContent

sealed interface SyncIntent : UiIntent {
    data object OnBackClick : SyncIntent
    data object OnUuidClick : SyncIntent
    data object OnSyncClick : SyncIntent
}
