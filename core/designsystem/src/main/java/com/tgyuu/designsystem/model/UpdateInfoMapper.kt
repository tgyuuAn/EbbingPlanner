package com.tgyuu.designsystem.model

import com.tgyuu.domain.model.UpdateInfo
import kotlinx.collections.immutable.toImmutableMap

fun UpdateInfo.toUiModel(): UpdateInfoUiModel {
    return UpdateInfoUiModel(
        minVersion = minVersion,
        noticeMsgs = noticeMsgs.toImmutableMap(),
    )
}
