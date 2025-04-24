package com.tgyuu.setting.contract

import com.tgyuu.common.base.UiState

data class SettingState(
    val version: String = "1.0.0",
) : UiState
