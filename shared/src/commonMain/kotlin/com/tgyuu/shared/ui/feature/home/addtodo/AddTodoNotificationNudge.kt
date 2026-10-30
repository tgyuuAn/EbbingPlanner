package com.tgyuu.shared.ui.feature.home.addtodo

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import com.tgyuu.shared.designsystem.component.EbbingDialog
import com.tgyuu.shared.designsystem.component.EbbingDialogDefaultTop
import com.tgyuu.shared.designsystem.component.EbbingSolidButton
import com.tgyuu.shared.designsystem.component.EbbingSubTopBar
import com.tgyuu.shared.designsystem.component.EbbingTextInputDefault
import com.tgyuu.shared.designsystem.component.EbbingToggle
import com.tgyuu.shared.designsystem.component.picker.EbbingPicker
import com.tgyuu.shared.designsystem.foundation.EbbingTheme
import ebbingplanner.shared.generated.resources.Res
import ebbingplanner.shared.generated.resources.alarm_placeholder_token
import ebbingplanner.shared.generated.resources.ds_am
import ebbingplanner.shared.generated.resources.ds_pm
import ebbingplanner.shared.generated.resources.home_add_todo_button
import ebbingplanner.shared.generated.resources.home_apply
import ebbingplanner.shared.generated.resources.home_notification_header_sub
import ebbingplanner.shared.generated.resources.home_notification_nudge
import ebbingplanner.shared.generated.resources.home_notification_receive
import ebbingplanner.shared.generated.resources.notification_placeholder_desc
import ebbingplanner.shared.generated.resources.setting_alarm_message
import ebbingplanner.shared.generated.resources.setting_alarm_message_error_length
import ebbingplanner.shared.generated.resources.setting_alarm_message_error_placeholder
import ebbingplanner.shared.generated.resources.setting_alarm_message_hint
import ebbingplanner.shared.generated.resources.setting_alarm_message_length
import ebbingplanner.shared.generated.resources.home_alarm_preview_sample
import ebbingplanner.shared.generated.resources.home_alarm_reset
import ebbingplanner.shared.generated.resources.home_alarm_time_sub
import ebbingplanner.shared.generated.resources.setting_alarm_time
import ebbingplanner.shared.generated.resources.home_notification_title
import ebbingplanner.shared.generated.resources.setting_preview
import org.jetbrains.compose.resources.stringResource

/**
 * Android home/graph/notification/NotificationScreen(넛지 페이지) 대응.
 * AddTodo 저장 시 최초 1회 노출되는 알림 설정 페이지. Save 시 설정을 영속하고 투두를 저장한다.
 */
@Composable
internal fun NotificationNudgeContent(
    state: NotificationState,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit,
    onToggleClick: () -> Unit,
    onTimePickerClick: () -> Unit,
    onTimePickerDismiss: () -> Unit,
    onTimeChange: (Int, Int) -> Unit,
    onMessageChange: (String) -> Unit,
    onResetClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    if (state.isShowTimePicker) {
        AlarmTimePickerDialog(
            initialHour = state.alarmHour,
            initialMinute = state.alarmMinute,
            onDismiss = onTimePickerDismiss,
            onConfirm = onTimeChange,
        )
    }

    val isTablet = EbbingTheme.windowLayout.isTablet

    val topBar: @Composable () -> Unit = {
        EbbingSubTopBar(
            title = stringResource(Res.string.home_notification_title),
            onNavigationClick = onBackClick,
            rightComponent = {},
            modifier = Modifier.padding(horizontal = 20.dp),
        )
    }
    val saveButton: @Composable () -> Unit = {
        EbbingSolidButton(
            label = stringResource(Res.string.home_add_todo_button),
            onClick = {
                focusManager.clearFocus()
                onSaveClick()
            },
            modifier = Modifier
                .fillMaxWidth()
                .background(EbbingTheme.colors.background)
                .padding(horizontal = 20.dp, vertical = 16.dp),
        )
    }
    val detail: @Composable (showDivider: Boolean) -> Unit = { showDivider ->
        // Android와 동일: 토글 off면 접힘
        AnimatedVisibility(
            visible = state.notificationEnabled,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically(),
        ) {
            NudgeDetailSection(
                state = state,
                showDivider = showDivider,
                onTimeClick = onTimePickerClick,
                onMessageChange = onMessageChange,
                onResetClick = onResetClick,
            )
        }
    }

    if (isTablet) {
        // Android NotificationScreenTablet과 동일: 좌(헤더+토글) | 우(상세) 2단, 구분바 없음
        Column(modifier = modifier.fillMaxSize()) {
            topBar()
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .imePadding(),
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(scrollState)
                        .padding(20.dp)
                        .padding(horizontal = 20.dp),
                ) {
                    NudgeHeader()
                    NudgeToggleRow(checked = state.notificationEnabled, onToggleClick = onToggleClick)
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(20.dp)
                        .padding(horizontal = 20.dp),
                ) {
                    detail(false)
                }
            }
            saveButton()
        }
    } else {
        Column(
            modifier = modifier
                .fillMaxSize()
                .imePadding(),
        ) {
            topBar()
            // Android NotificationScreen과 동일: 세로 패딩만, 각 자식이 horizontal=20(구분선은 풀폭)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .padding(vertical = 20.dp),
            ) {
                NudgeHeader()
                NudgeToggleRow(checked = state.notificationEnabled, onToggleClick = onToggleClick)
                detail(true)
            }
            saveButton()
        }
    }
}

@Composable
private fun NudgeHeader() {
    Text(
        text = stringResource(Res.string.home_notification_nudge),
        style = EbbingTheme.typography.headingLSB,
        color = EbbingTheme.colors.black,
        modifier = Modifier.padding(horizontal = 20.dp),
    )
    Text(
        text = stringResource(Res.string.home_notification_header_sub),
        style = EbbingTheme.typography.bodyMM,
        color = EbbingTheme.colors.dark3,
        modifier = Modifier.padding(top = 12.dp).padding(horizontal = 20.dp),
    )
}

@Composable
private fun NudgeToggleRow(
    checked: Boolean,
    onToggleClick: () -> Unit,
) {
    // Android와 동일: 라벨(heading18B/textSub) + end=8 + 토글 (좌측 그룹, weight 아님)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 32.dp)
            .padding(horizontal = 20.dp),
    ) {
        Text(
            text = stringResource(Res.string.home_notification_receive),
            style = EbbingTheme.typography.headingSSB,
            color = EbbingTheme.colors.dark2,
            modifier = Modifier.padding(end = 8.dp),
        )
        EbbingToggle(
            checked = checked,
            onCheckedChange = { onToggleClick() },
        )
    }
}

@Composable
private fun NudgeDetailSection(
    state: NotificationState,
    showDivider: Boolean,
    onTimeClick: () -> Unit,
    onMessageChange: (String) -> Unit,
    onResetClick: () -> Unit,
) {
    val placeholderToken = state.placeholderToken
    val placeholderDesc = stringResource(Res.string.notification_placeholder_desc)
    val previewSample = stringResource(Res.string.home_alarm_preview_sample)
    val previewMessage = when {
        state.placeholderCount == 1 -> state.message.replace(placeholderToken, previewSample)
        state.placeholderCount == 0 -> state.message
        else -> ""
    }
    Column {
        // 6dp 채움 구분바 (Android NotificationScreen: fillTextfield, vertical=28, 풀폭) — 태블릿은 없음
        if (showDivider) {
            Spacer(
                modifier = Modifier
                    .padding(vertical = 28.dp)
                    .fillMaxWidth()
                    .height(6.dp)
                    .background(EbbingTheme.colors.light3),
            )
        }

        // 알림 시간
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(bottom = 40.dp),
        ) {
            Text(
                text = stringResource(Res.string.setting_alarm_time),
                // Android home_alarm_time: heading18B(Bold 18) / textSub
                style = EbbingTheme.typography.headingSSB,
                color = EbbingTheme.colors.dark2,
                modifier = Modifier.padding(end = 8.dp),
            )
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(textDecoration = TextDecoration.Underline)) {
                        // Android formattedAlarmTime과 동일: HH:mm
                        append(
                            "${state.alarmHour.toString().padStart(2, '0')}:" +
                                state.alarmMinute.toString().padStart(2, '0')
                        )
                    }
                },
                // Android: body18M(Medium 18) / primaryNormal — iOS headingSM = Medium 18/22
                style = EbbingTheme.typography.headingSM,
                color = EbbingTheme.colors.primaryDefault,
                modifier = Modifier.clickable { onTimeClick() },
            )
        }

        // 알림 메시지
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
            Text(
                text = stringResource(Res.string.setting_alarm_message),
                style = EbbingTheme.typography.headingSSB,
                color = EbbingTheme.colors.dark2,
                modifier = Modifier.padding(bottom = 6.dp),
            )
            // 플레이스홀더 안내: 토큰 볼드 강조, 입력창 위에 위치 (Android 동일)
            Text(
                text = buildAnnotatedString {
                    if (placeholderDesc.startsWith(placeholderToken)) {
                        withStyle(SpanStyle(color = EbbingTheme.colors.primaryDefault, fontWeight = FontWeight.Bold)) {
                            append(placeholderToken)
                        }
                        append(placeholderDesc.removePrefix(placeholderToken))
                    } else {
                        append(placeholderDesc)
                    }
                },
                style = EbbingTheme.typography.bodySM,
                color = EbbingTheme.colors.dark3,
                modifier = Modifier.padding(bottom = 16.dp),
            )
            EbbingTextInputDefault(
                value = state.message,
                hint = stringResource(Res.string.setting_alarm_message_hint),
                onValueChange = onMessageChange,
                modifier = Modifier.padding(top = 24.dp).fillMaxWidth(),
            )
            val errorText = when {
                state.placeholderCount > 1 -> stringResource(Res.string.setting_alarm_message_error_placeholder)
                !state.isValidLength -> stringResource(Res.string.setting_alarm_message_error_length)
                else -> ""
            }
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                Text(text = errorText, style = EbbingTheme.typography.captionR12, color = EbbingTheme.colors.error, modifier = Modifier.weight(1f))
                Text(text = stringResource(Res.string.setting_alarm_message_length, state.messageLength), style = EbbingTheme.typography.captionR12, color = if (state.isValidLength) EbbingTheme.colors.dark3 else EbbingTheme.colors.error)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 미리보기 카드 (라운드 배경) — 유효 플레이스홀더 & 비어있지 않을 때만
            if (state.isValidPlaceholder && previewMessage.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(EbbingTheme.colors.background, RoundedCornerShape(16.dp))
                        .padding(vertical = 12.dp, horizontal = 16.dp),
                ) {
                    Text(text = stringResource(Res.string.setting_preview), style = EbbingTheme.typography.captionR12, color = EbbingTheme.colors.dark3, modifier = Modifier.padding(bottom = 4.dp))
                    Text(text = previewMessage, style = EbbingTheme.typography.bodyMM, color = EbbingTheme.colors.dark2)
                }
            }

            // 초기화 링크 — 하단, 기본값과 다를 때만 (Android ResetButton 위치)
            if (state.shouldShowResetButton) {
                Text(
                    text = stringResource(Res.string.home_alarm_reset),
                    style = EbbingTheme.typography.bodySSB,
                    color = EbbingTheme.colors.dark3,
                    modifier = Modifier.padding(top = 20.dp).clickable { onResetClick() },
                )
            }
        }
    }
}

@Composable
private fun AlarmTimePickerDialog(
    initialHour: Int,
    initialMinute: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int, Int) -> Unit,
) {
    val amText = stringResource(Res.string.ds_am)
    val pmText = stringResource(Res.string.ds_pm)
    var newAmPm by remember { mutableStateOf(if (initialHour >= 12) pmText else amText) }
    var newHour by remember { mutableIntStateOf(initialHour) }
    var newMinute by remember { mutableIntStateOf(initialMinute) }

    val pickerAmPm = if (initialHour >= 12) pmText else amText
    val pickerHour = when {
        initialHour == 0 -> "12"
        initialHour > 12 -> (initialHour - 12).toString()
        else -> initialHour.toString()
    }
    val pickerMinute = initialMinute.toString().padStart(2, '0')

    EbbingDialog(
        onDismissRequest = onDismiss,
        dialogTop = {
            EbbingDialogDefaultTop(
                title = stringResource(Res.string.setting_alarm_time),
                subText = stringResource(Res.string.home_alarm_time_sub),
            )
        },
        dialogBottom = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 20.dp),
            ) {
                EbbingPicker(
                    initialAmPm = pickerAmPm,
                    initialHour = pickerHour,
                    initialMinute = pickerMinute,
                    onValueChange = { amPm, hour, minute ->
                        newAmPm = amPm
                        newHour = hour
                        newMinute = minute
                    },
                    modifier = Modifier.padding(vertical = 20.dp),
                )

                EbbingSolidButton(
                    label = stringResource(Res.string.home_apply),
                    onClick = {
                        val adjustedHour = when {
                            newAmPm == pmText && newHour == 12 -> 12
                            newAmPm == pmText -> newHour + 12
                            newAmPm == amText && newHour == 12 -> 0
                            else -> newHour
                        }
                        onConfirm(adjustedHour, newMinute)
                    },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
    )
}
