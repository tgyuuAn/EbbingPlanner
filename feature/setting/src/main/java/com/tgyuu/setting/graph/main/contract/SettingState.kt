package com.tgyuu.setting.graph.main.contract

import com.tgyuu.common.base.UiState

data class SettingState(
    val version: String = "1.0.0",
    val notificationEnabled: Boolean = true,
    val alarmHour: String = "",
    val alarmMinute: String = "",
) : UiState
