package com.tgyuu.setting.graph.main.contract

import com.tgyuu.common.base.UiState
import com.tgyuu.domain.model.UpdateInfo

data class SettingState(
    val updateInfo: UpdateInfo? = null,
    val notificationEnabled: Boolean = true,
    val alarmHour: String = "",
    val alarmMinute: String = "",
) : UiState
