package com.tgyuu.sync.graph.upload.contract

import com.tgyuu.common.base.UiIntent

sealed interface UploadIntent : UiIntent {
    data object OnBackClick : UploadIntent
    data object OnUuidClick : UploadIntent
    data object OnUploadClick : UploadIntent
}
