package com.tgyuu.shared.ui.feature.home.notification

import com.tgyuu.shared.designsystem.foundation.LayoutConstants
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import ebbingplanner.shared.generated.resources.ds_am
import ebbingplanner.shared.generated.resources.ds_pm
import ebbingplanner.shared.generated.resources.home_apply
import ebbingplanner.shared.generated.resources.notification_placeholder_desc
import ebbingplanner.shared.generated.resources.setting_alarm_message
import ebbingplanner.shared.generated.resources.alarm_placeholder_token
import ebbingplanner.shared.generated.resources.setting_alarm_message_error_placeholder
import ebbingplanner.shared.generated.resources.setting_alarm_message_error_length
import ebbingplanner.shared.generated.resources.setting_alarm_message_length
import ebbingplanner.shared.generated.resources.setting_alarm_message_hint
import ebbingplanner.shared.generated.resources.setting_alarm_time
import ebbingplanner.shared.generated.resources.setting_alarm_time_subtitle
import ebbingplanner.shared.generated.resources.setting_clear
import ebbingplanner.shared.generated.resources.setting_notification
import ebbingplanner.shared.generated.resources.home_notification_receive
import ebbingplanner.shared.generated.resources.home_notification_nudge
import ebbingplanner.shared.generated.resources.home_notification_header_sub
import ebbingplanner.shared.generated.resources.setting_notification_setting
import ebbingplanner.shared.generated.resources.setting_preview
import org.jetbrains.compose.resources.stringResource
import com.tgyuu.shared.designsystem.model.alarmTimeText
import ebbingplanner.shared.generated.resources.setting_alarm_message_preview_sample

@Composable
fun NotificationScreen(
    viewModel: NotificationViewModel,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()

    if (state.isShowTimePicker) {
        AlarmTimePickerDialog(
            initialHour = state.alarmHour,
            initialMinute = state.alarmMinute,
            onDismiss = { viewModel.onIntent(NotificationIntent.OnTimePickerDismiss) },
            onConfirm = { hour, minute ->
                viewModel.onIntent(NotificationIntent.OnTimeChange(hour, minute))
            },
        )
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
    val isWide = maxWidth > LayoutConstants.TABLET_BREAKPOINT
    Column(modifier = Modifier.fillMaxSize()) {
        EbbingSubTopBar(
            title = stringResource(Res.string.setting_notification_setting),
            onNavigationClick = { viewModel.onIntent(NotificationIntent.OnBackClick) },
            modifier = Modifier.padding(horizontal = 20.dp),
        )

        if (isWide) {
            Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
                // Left: toggle section
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(scrollState)
                        .padding(vertical = 20.dp),
                ) {
                    NotificationHeader()
                    NotificationToggleSection(
                        isEnabled = state.isNotificationEnabled,
                        onToggle = { viewModel.onIntent(NotificationIntent.OnNotificationToggle(it)) },
                    )
                }
                // Right: detail section
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 20.dp),
                ) {
                    NotificationDetailSection(
                        state = state,
                        onTimeClick = { viewModel.onIntent(NotificationIntent.OnTimePickerClick) },
                        onMessageChange = { viewModel.onIntent(NotificationIntent.OnMessageChange(it)) },
                        onResetMessage = { viewModel.onIntent(NotificationIntent.OnResetMessage) },
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .padding(vertical = 20.dp),
            ) {
                NotificationHeader()
                NotificationToggleSection(
                    isEnabled = state.isNotificationEnabled,
                    onToggle = { viewModel.onIntent(NotificationIntent.OnNotificationToggle(it)) },
                )
                NotificationDetailSection(
                    state = state,
                    onTimeClick = { viewModel.onIntent(NotificationIntent.OnTimePickerClick) },
                    onMessageChange = { viewModel.onIntent(NotificationIntent.OnMessageChange(it)) },
                    onResetMessage = { viewModel.onIntent(NotificationIntent.OnResetMessage) },
                )
                Spacer(modifier = Modifier.height(60.dp))
            }
        }
    }
    } // BoxWithConstraints
}

@Composable
private fun NotificationHeader() {
    // Android NotificationHeader와 동일: 상단 큰 제목 + 안내 서브텍스트
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
        modifier = Modifier.padding(horizontal = 20.dp).padding(top = 12.dp),
    )
    Spacer(modifier = Modifier.height(32.dp))
}

@Composable
private fun NotificationToggleSection(
    isEnabled: Boolean,
    onToggle: (Boolean) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
    ) {
        Text(
            text = stringResource(Res.string.home_notification_receive),
            // Android: heading18B / textSub
            style = EbbingTheme.typography.headingSSB,
            color = EbbingTheme.colors.dark2,
            modifier = Modifier.weight(1f),
        )
        EbbingToggle(
            checked = isEnabled,
            onCheckedChange = onToggle,
        )
    }
}

@Composable
private fun NotificationDetailSection(
    state: NotificationState,
    onTimeClick: () -> Unit,
    onMessageChange: (String) -> Unit,
    onResetMessage: () -> Unit,
) {
    // Android NotificationScreen 구조와 동일하게 재구성
    val placeholderToken = stringResource(Res.string.alarm_placeholder_token)
    val placeholderDesc = stringResource(Res.string.notification_placeholder_desc)
    val previewSample = stringResource(Res.string.setting_alarm_message_preview_sample)
    val placeholderCount = if (placeholderToken.isEmpty()) 0
        else state.alarmMessage.split(placeholderToken).size - 1
    val isValidLength = state.alarmMessage.length <= 50
    val isValidPlaceholder = placeholderCount <= 1
    val shouldShowReset = state.alarmMessage != NotificationState.DEFAULT_ALARM_MESSAGE
    val previewMessage = when {
        placeholderCount == 1 -> state.alarmMessage.replace(placeholderToken, previewSample)
        placeholderCount == 0 -> state.alarmMessage
        else -> ""
    }

    // 6dp 채움 구분바 (Android: fillTextfield, vertical=28)
    Spacer(
        modifier = Modifier
            .padding(vertical = 28.dp)
            .fillMaxWidth()
            .height(6.dp)
            .background(EbbingTheme.colors.light2),
    )

    // 이하 컨텐츠만 좌우 20dp 인셋 (위 6dp 바는 풀폭 — Android와 동일)
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
    // 알림 시간 (Android AlarmTimeRow): 라벨 heading18B/textSub + 밑줄 시간 body18M/primaryNormal
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().padding(bottom = 40.dp),
    ) {
        Text(
            text = stringResource(Res.string.setting_alarm_time),
            style = EbbingTheme.typography.headingSSB,
            color = EbbingTheme.colors.dark2,
            modifier = Modifier.padding(end = 8.dp),
        )
        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(textDecoration = TextDecoration.Underline)) {
                    append(alarmTimeText(state.alarmHour, state.alarmMinute))
                }
            },
            style = EbbingTheme.typography.headingSM,
            color = EbbingTheme.colors.primaryDefault,
            modifier = Modifier.clickable { onTimeClick() },
        )
    }

    // 알림 메시지 (Android AlarmMessageSection)
    Text(
        text = stringResource(Res.string.setting_alarm_message),
        style = EbbingTheme.typography.headingSSB,
        color = EbbingTheme.colors.dark2,
        modifier = Modifier.padding(bottom = 6.dp),
    )
    // 플레이스홀더 안내: 토큰 볼드 강조 + 입력창 위에 배치 (Android 동일)
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
        value = state.alarmMessage,
        hint = stringResource(Res.string.setting_alarm_message_hint),
        onValueChange = onMessageChange,
        limit = 50,
        modifier = Modifier.padding(top = 24.dp).fillMaxWidth(),
    )
    val errorText = when {
        placeholderCount > 1 -> stringResource(Res.string.setting_alarm_message_error_placeholder)
        !isValidLength -> stringResource(Res.string.setting_alarm_message_error_length)
        else -> ""
    }
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
        Text(text = errorText, style = EbbingTheme.typography.captionR12, color = EbbingTheme.colors.error, modifier = Modifier.weight(1f))
        Text(text = stringResource(Res.string.setting_alarm_message_length, state.alarmMessage.length), style = EbbingTheme.typography.captionR12, color = if (isValidLength) EbbingTheme.colors.dark3 else EbbingTheme.colors.error)
    }

    Spacer(modifier = Modifier.height(16.dp))

    // 미리보기: 라운드 카드 (Android MessagePreview)
    if (isValidPlaceholder && previewMessage.isNotEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(EbbingTheme.colors.light3, RoundedCornerShape(16.dp))
                .padding(vertical = 12.dp, horizontal = 16.dp),
        ) {
            Text(text = stringResource(Res.string.setting_preview), style = EbbingTheme.typography.captionR12, color = EbbingTheme.colors.dark3, modifier = Modifier.padding(bottom = 4.dp))
            Text(text = previewMessage, style = EbbingTheme.typography.bodyMM, color = EbbingTheme.colors.dark2)
        }
    }

    // 초기화: 하단 링크 (Android ResetButton), 기본 메시지와 다를 때만
    if (shouldShowReset) {
        Text(
            text = stringResource(Res.string.setting_clear),
            style = EbbingTheme.typography.bodySSB,
            color = EbbingTheme.colors.dark3,
            modifier = Modifier.padding(top = 20.dp).clickable { onResetMessage() },
        )
    }
    } // Column(horizontal = 20)
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
                subText = stringResource(Res.string.setting_alarm_time_subtitle),
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
