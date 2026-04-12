package com.tgyuu.setting.graph.main.contract

import com.tgyuu.common.base.UiState
import com.tgyuu.domain.model.UpdateInfo
import com.tgyuu.domain.repository.ConfigRepository.Companion.DEFAULT_ALARM_MESSAGE

data class SettingState(
    val updateInfo: UpdateInfo? = null,
    val hardUpdateInfo: UpdateInfo? = null,
    val notificationEnabled: Boolean = true,
    val alarmHour: String = "",
    val alarmMinute: String = "",
    val alarmMessage: String = DEFAULT_ALARM_MESSAGE,
    val alarmMessageBottomSheet: AlarmMessageBottomSheetState = AlarmMessageBottomSheetState(),
    val mondayStart: Boolean = false,
) : UiState

data class AlarmMessageBottomSheetState(
    val message: String = DEFAULT_ALARM_MESSAGE,
    val originMessage: String = "",
) {
    val placeholderCount: Int = "\\{할일\\}".toRegex().findAll(message).count()

    val isValidPlaceholder: Boolean = placeholderCount <= 1

    val isValidLength: Boolean = message.length <= 50

    val isValid: Boolean = isValidPlaceholder && isValidLength

    val previewMessage: String = if (placeholderCount == 1) {
        message.replace("{할일}", "영어 단어 복습")
    } else if (placeholderCount == 0) {
        message
    } else {
        ""
    }

    val errorMessage: String = when {
        placeholderCount > 1 -> "{할일}은 최대 1번만 사용할 수 있습니다"
        !isValidLength -> "최대 50자까지 입력 가능합니다"
        else -> ""
    }

    val lengthText: String = "${message.length} / 50자"

    val isChanged: Boolean = message != originMessage

    val canApply: Boolean = isValid && isChanged

    val shouldShowResetButton: Boolean = message != DEFAULT_ALARM_MESSAGE
}
