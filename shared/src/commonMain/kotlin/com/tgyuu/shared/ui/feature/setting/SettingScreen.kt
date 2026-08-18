package com.tgyuu.shared.ui.feature.setting

import com.tgyuu.shared.designsystem.foundation.LayoutConstants
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import com.tgyuu.shared.designsystem.component.EbbingDialog
import com.tgyuu.shared.designsystem.component.EbbingDialogBottom
import com.tgyuu.shared.designsystem.component.EbbingMainTopBar
import com.tgyuu.shared.designsystem.component.EbbingSolidButton
import com.tgyuu.shared.designsystem.component.EbbingTextInputDefault
import com.tgyuu.shared.designsystem.component.EbbingToggle
import com.tgyuu.shared.designsystem.component.bottomsheet.EbbingBottomSheetHeader
import com.tgyuu.shared.designsystem.component.bottomsheet.EbbingBottomSheetListItemDefault
import com.tgyuu.shared.designsystem.component.bottomsheet.EbbingModalBottomSheet
import com.tgyuu.shared.designsystem.component.bottomsheet.rememberEbbingBottomSheetState
import com.tgyuu.shared.designsystem.component.picker.EbbingPicker
import com.tgyuu.shared.designsystem.foundation.EbbingTheme
import ebbingplanner.shared.generated.resources.Res
import ebbingplanner.shared.generated.resources.setting_calendar
import ebbingplanner.shared.generated.resources.setting_data
import ebbingplanner.shared.generated.resources.setting_guidance
import ebbingplanner.shared.generated.resources.setting_inquiry
import ebbingplanner.shared.generated.resources.setting_notification
import ebbingplanner.shared.generated.resources.setting_tag_repeat_cycle
import ebbingplanner.shared.generated.resources.setting_theme
import ebbingplanner.shared.generated.resources.ds_am
import ebbingplanner.shared.generated.resources.ds_pm
import ebbingplanner.shared.generated.resources.setting_title
import ebbingplanner.shared.generated.resources.setting_tag_manage
import ebbingplanner.shared.generated.resources.setting_repeat_cycle_manage
import ebbingplanner.shared.generated.resources.setting_sync_with_other_device
import ebbingplanner.shared.generated.resources.setting_use_on_other_device
import ebbingplanner.shared.generated.resources.setting_auto_backup
import ebbingplanner.shared.generated.resources.setting_last_sync_time
import ebbingplanner.shared.generated.resources.setting_clear_data
import ebbingplanner.shared.generated.resources.sync_restore_title
import ebbingplanner.shared.generated.resources.setting_theme_color_change
import ebbingplanner.shared.generated.resources.setting_widget_alpha_change
import ebbingplanner.shared.generated.resources.setting_announcement
import ebbingplanner.shared.generated.resources.setting_privacy_policy
import ebbingplanner.shared.generated.resources.setting_term
import ebbingplanner.shared.generated.resources.setting_version
import ebbingplanner.shared.generated.resources.setting_calendar_start_day
import ebbingplanner.shared.generated.resources.setting_monday
import ebbingplanner.shared.generated.resources.setting_sunday
import ebbingplanner.shared.generated.resources.setting_notification_setting
import ebbingplanner.shared.generated.resources.setting_alarm_time
import ebbingplanner.shared.generated.resources.setting_alarm_message
import ebbingplanner.shared.generated.resources.setting_contact_us
import ebbingplanner.shared.generated.resources.setting_app_review
import ebbingplanner.shared.generated.resources.setting_clear
import ebbingplanner.shared.generated.resources.setting_clear_dialog_title_prefix
import ebbingplanner.shared.generated.resources.setting_clear_dialog_title_highlight
import ebbingplanner.shared.generated.resources.setting_clear_dialog_title_suffix
import ebbingplanner.shared.generated.resources.setting_clear_dialog_subtext
import ebbingplanner.shared.generated.resources.setting_back
import ebbingplanner.shared.generated.resources.setting_apply
import ebbingplanner.shared.generated.resources.setting_apply_action
import ebbingplanner.shared.generated.resources.setting_alarm_time_title
import ebbingplanner.shared.generated.resources.setting_alarm_time_subtitle
import ebbingplanner.shared.generated.resources.setting_alarm_message_setting
import ebbingplanner.shared.generated.resources.alarm_placeholder_token
import ebbingplanner.shared.generated.resources.setting_alarm_message_placeholder_guide
import ebbingplanner.shared.generated.resources.setting_alarm_message_hint
import ebbingplanner.shared.generated.resources.setting_preview
import ebbingplanner.shared.generated.resources.setting_restore_default
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import com.tgyuu.shared.designsystem.model.alarmTimeText
import ebbingplanner.shared.generated.resources.setting_alarm_message_error_placeholder
import ebbingplanner.shared.generated.resources.setting_alarm_message_error_length
import ebbingplanner.shared.generated.resources.setting_alarm_message_length
import ebbingplanner.shared.generated.resources.setting_alarm_message_preview_sample
import org.jetbrains.compose.resources.painterResource
import ebbingplanner.shared.generated.resources.ic_arrow_right

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    viewModel: SettingViewModel,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsState()
    var showClearDialog by remember { mutableStateOf(false) }

    if (showClearDialog) {
        ClearDataDialog(
            onConfirm = {
                viewModel.onIntent(SettingIntent.OnClearDataConfirm)
                showClearDialog = false
            },
            onDismiss = { showClearDialog = false },
        )
    }

    val scope = rememberCoroutineScope()
    val bottomSheetState = rememberEbbingBottomSheetState()
    var currentSheet by remember { mutableStateOf<SettingBottomSheetType?>(null) }

    val closeSheet: () -> Unit = {
        scope.launch {
            bottomSheetState.hide()
            currentSheet = null
        }
    }
    val openSheet: (SettingBottomSheetType) -> Unit = { type ->
        currentSheet = type
        scope.launch { bottomSheetState.show() }
    }

    EbbingModalBottomSheet(
        sheetState = bottomSheetState,
        onDismissRequest = closeSheet,
        content = when (currentSheet) {
            SettingBottomSheetType.ALARM_TIME -> {
                {
                    AlarmTimeBottomSheetContent(
                        originHour = state.alarmHour,
                        originMinute = state.alarmMinute,
                        onUpdateClick = { hour, minute ->
                            viewModel.onIntent(SettingIntent.OnUpdateAlarmTime(hour, minute))
                            closeSheet()
                        },
                    )
                }
            }
            SettingBottomSheetType.ALARM_MESSAGE -> {
                {
                    AlarmMessageBottomSheetContent(
                        sheetState = state.alarmMessageBottomSheet,
                        onMessageChange = { viewModel.onIntent(SettingIntent.OnAlarmMessageChange(it)) },
                        onResetClick = { viewModel.onIntent(SettingIntent.OnAlarmMessageReset) },
                        onUpdateClick = {
                            viewModel.onIntent(SettingIntent.OnApplyAlarmMessage)
                            closeSheet()
                        },
                    )
                }
            }
            SettingBottomSheetType.CALENDAR_START_DAY -> {
                {
                    CalendarStartDayBottomSheetContent(
                        originMondayStart = state.mondayStart,
                        onUpdateClick = { mondayStart ->
                            viewModel.onIntent(SettingIntent.OnUpdateStartDay(mondayStart))
                            closeSheet()
                        },
                    )
                }
            }
            null -> null
        },
    )

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
    val isWide = maxWidth > LayoutConstants.TABLET_BREAKPOINT
    Column(modifier = Modifier.fillMaxSize()) {
        EbbingMainTopBar(
            title = stringResource(Res.string.setting_title),
            modifier = Modifier.padding(horizontal = 20.dp),
        )

        HorizontalDivider(color = EbbingTheme.colors.light2, thickness = 1.dp, modifier = Modifier.padding(bottom = 16.dp))

        if (isWide) {
            Row(modifier = Modifier.fillMaxSize()) {
                // Left column
                Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 20.dp)) {
                    NotificationBody(isEnabled = state.isNotificationEnabled, alarmTime = alarmTimeText(state.alarmHour, state.alarmMinute), onToggle = { viewModel.onIntent(SettingIntent.OnNotificationToggle(it)) }, onAlarmTimeClick = { openSheet(SettingBottomSheetType.ALARM_TIME) }, onAlarmMessageClick = { viewModel.onIntent(SettingIntent.OnAlarmMessageOpen); openSheet(SettingBottomSheetType.ALARM_MESSAGE) })
                    CalendarBody(mondayStart = state.mondayStart, onClick = { openSheet(SettingBottomSheetType.CALENDAR_START_DAY) })
                    TagRepeatCycleBody(onTagManageClick = { viewModel.onIntent(SettingIntent.OnTagManageClick) }, onRepeatCycleManageClick = { viewModel.onIntent(SettingIntent.OnRepeatCycleManageClick) })
                    DataBody(autoBackupFeatureEnabled = state.autoBackupFeatureEnabled, autoBackupEnabled = state.autoBackupEnabled, lastSyncTime = state.lastSyncTime, onSyncClick = { viewModel.onIntent(SettingIntent.OnSyncClick) }, onRestoreClick = { viewModel.onIntent(SettingIntent.OnRestoreByDeviceIdClick) }, onClearClick = { showClearDialog = true }, onAutoBackupToggle = { viewModel.onIntent(SettingIntent.OnAutoBackupToggleClick) })
                    Spacer(modifier = Modifier.height(24.dp))
                }
                // Right column
                Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 20.dp)) {
                    ThemeBody(onThemeManageClick = { viewModel.onIntent(SettingIntent.OnThemeClick) }, onWidgetAlphaClick = { viewModel.onIntent(SettingIntent.OnWidgetClick) })
                    InquiryBody(onInquiryClick = { viewModel.onIntent(SettingIntent.OnInquiryClick) })
                    AnnouncementBody(onPrivacyPolicyClick = { viewModel.onIntent(SettingIntent.OnPrivacyPolicyClick) }, onTermsClick = { viewModel.onIntent(SettingIntent.OnTermsOfUseClick) }, onNoticeClick = { viewModel.onIntent(SettingIntent.OnNoticeClick) })
                    InAppReviewRow(onClick = { viewModel.onIntent(SettingIntent.OnInAppReviewClick) })
                    VersionRow(version = state.appVersion)
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        } else {
            // Android PhoneSettingScreen과 동일 순서: 알림→캘린더→태그/반복→테마→문의→안내→데이터→리뷰→버전
            Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp)) {
                NotificationBody(isEnabled = state.isNotificationEnabled, alarmTime = alarmTimeText(state.alarmHour, state.alarmMinute), onToggle = { viewModel.onIntent(SettingIntent.OnNotificationToggle(it)) }, onAlarmTimeClick = { openSheet(SettingBottomSheetType.ALARM_TIME) }, onAlarmMessageClick = { viewModel.onIntent(SettingIntent.OnAlarmMessageOpen); openSheet(SettingBottomSheetType.ALARM_MESSAGE) })
                CalendarBody(mondayStart = state.mondayStart, onClick = { openSheet(SettingBottomSheetType.CALENDAR_START_DAY) })
                TagRepeatCycleBody(onTagManageClick = { viewModel.onIntent(SettingIntent.OnTagManageClick) }, onRepeatCycleManageClick = { viewModel.onIntent(SettingIntent.OnRepeatCycleManageClick) })
                ThemeBody(onThemeManageClick = { viewModel.onIntent(SettingIntent.OnThemeClick) }, onWidgetAlphaClick = { viewModel.onIntent(SettingIntent.OnWidgetClick) })
                InquiryBody(onInquiryClick = { viewModel.onIntent(SettingIntent.OnInquiryClick) })
                AnnouncementBody(onPrivacyPolicyClick = { viewModel.onIntent(SettingIntent.OnPrivacyPolicyClick) }, onTermsClick = { viewModel.onIntent(SettingIntent.OnTermsOfUseClick) }, onNoticeClick = { viewModel.onIntent(SettingIntent.OnNoticeClick) })
                DataBody(autoBackupFeatureEnabled = state.autoBackupFeatureEnabled, autoBackupEnabled = state.autoBackupEnabled, lastSyncTime = state.lastSyncTime, onSyncClick = { viewModel.onIntent(SettingIntent.OnSyncClick) }, onRestoreClick = { viewModel.onIntent(SettingIntent.OnRestoreByDeviceIdClick) }, onClearClick = { showClearDialog = true }, onAutoBackupToggle = { viewModel.onIntent(SettingIntent.OnAutoBackupToggleClick) })
                InAppReviewRow(onClick = { viewModel.onIntent(SettingIntent.OnInAppReviewClick) })
                VersionRow(version = state.appVersion)
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
    } // BoxWithConstraints
}

@Composable
private fun TagRepeatCycleBody(
    onTagManageClick: () -> Unit,
    onRepeatCycleManageClick: () -> Unit,
) {
    SectionHeader(text = stringResource(Res.string.setting_tag_repeat_cycle))

    SettingRow(
        title = stringResource(Res.string.setting_tag_manage),
        onClick = onTagManageClick,
    )

    SettingRow(
        title = stringResource(Res.string.setting_repeat_cycle_manage),
        onClick = onRepeatCycleManageClick,
    )

    SectionDivider()
}

@Composable
private fun DataBody(
    autoBackupFeatureEnabled: Boolean,
    autoBackupEnabled: Boolean,
    lastSyncTime: String?,
    onSyncClick: () -> Unit,
    onRestoreClick: () -> Unit,
    onClearClick: () -> Unit,
    onAutoBackupToggle: () -> Unit,
) {
    SectionHeader(text = stringResource(Res.string.setting_data))

    SettingRow(
        title = stringResource(Res.string.setting_use_on_other_device),
        onClick = onSyncClick,
    )

    if (autoBackupFeatureEnabled) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = SettingItemVerticalPadding),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = stringResource(Res.string.setting_auto_backup),
                    style = EbbingTheme.typography.bodyMSB,
                    color = EbbingTheme.colors.black,
                    modifier = Modifier.weight(1f),
                )

                EbbingToggle(
                    checked = autoBackupEnabled,
                    onCheckedChange = { onAutoBackupToggle() },
                )
            }

            if (lastSyncTime != null) {
                Text(
                    text = stringResource(Res.string.setting_last_sync_time, lastSyncTime),
                    style = EbbingTheme.typography.bodySM,
                    color = EbbingTheme.colors.dark3,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
        }
    }

    // Android DataBody와 동일: 기기 ID 기반 데이터 복원 진입 행
    SettingRow(
        title = stringResource(Res.string.sync_restore_title),
        onClick = onRestoreClick,
    )

    SettingRow(
        title = stringResource(Res.string.setting_clear_data),
        onClick = onClearClick,
    )

    SectionDivider()
}

@Composable
private fun ThemeBody(
    onThemeManageClick: () -> Unit,
    onWidgetAlphaClick: () -> Unit,
) {
    SectionHeader(text = stringResource(Res.string.setting_theme))

    SettingRow(
        title = stringResource(Res.string.setting_theme_color_change),
        onClick = onThemeManageClick,
    )

    SettingRow(
        title = stringResource(Res.string.setting_widget_alpha_change),
        onClick = onWidgetAlphaClick,
    )

    SectionDivider()
}

@Composable
private fun AnnouncementBody(
    onPrivacyPolicyClick: () -> Unit,
    onTermsClick: () -> Unit,
    onNoticeClick: () -> Unit,
) {
    SectionHeader(text = stringResource(Res.string.setting_guidance))

    SettingRow(
        title = stringResource(Res.string.setting_announcement),
        onClick = onNoticeClick,
    )

    SettingRow(
        title = stringResource(Res.string.setting_privacy_policy),
        onClick = onPrivacyPolicyClick,
    )

    SettingRow(
        title = stringResource(Res.string.setting_term),
        onClick = onTermsClick,
    )

    SectionDivider()
}

@Composable
private fun VersionRow(
    version: String,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = SettingItemVerticalPadding),
    ) {
        Text(
            text = stringResource(Res.string.setting_version, "v$version"),
            style = EbbingTheme.typography.bodyMSB,
            color = EbbingTheme.colors.black,
        )
    }
}

@Composable
private fun SectionHeader(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        style = EbbingTheme.typography.bodySM,
        color = EbbingTheme.colors.dark3,
        modifier = modifier.padding(bottom = 8.dp),
    )
}

@Composable
private fun SettingRow(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = SettingItemVerticalPadding),
    ) {
        Text(
            text = title,
            style = EbbingTheme.typography.bodyMSB,
            color = EbbingTheme.colors.black,
            modifier = Modifier.weight(1f),
        )

        Icon(
            painter = painterResource(Res.drawable.ic_arrow_right),
            contentDescription = null,
            tint = EbbingTheme.colors.dark3,
        )
    }
}

@Composable
private fun CalendarBody(
    mondayStart: Boolean,
    onClick: () -> Unit,
) {
    SectionHeader(text = stringResource(Res.string.setting_calendar))

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = SettingItemVerticalPadding),
    ) {
        Text(
            text = stringResource(Res.string.setting_calendar_start_day),
            style = EbbingTheme.typography.bodyMSB,
            color = EbbingTheme.colors.black,
            modifier = Modifier.weight(1f),
        )

        val startDayLabel = if (mondayStart) stringResource(Res.string.setting_monday)
        else stringResource(Res.string.setting_sunday)
        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(textDecoration = TextDecoration.Underline)) {
                    append(startDayLabel)
                }
            },
            textAlign = TextAlign.End,
            style = EbbingTheme.typography.bodyMSB,
            color = EbbingTheme.colors.primaryDefault,
            modifier = Modifier.clickable { onClick() },
        )
    }

    SectionDivider()
}

@Composable
private fun SectionDivider(modifier: Modifier = Modifier) {
    HorizontalDivider(
        modifier = modifier
            .ignoreHorizontalPadding()
            .padding(bottom = SettingItemVerticalPadding),
        thickness = 6.dp,
        color = EbbingTheme.colors.light2,
    )
}

private val SettingItemVerticalPadding = 16.dp

/** 좌우 20dp 패딩을 무시하고 가로 풀블리드로 그리는 modifier (Android와 동일) */
private fun Modifier.ignoreHorizontalPadding(horizontal: Dp = 20.dp) = layout { measurable, constraints ->
    val extraWidth = (horizontal * 2).roundToPx()
    val placeable = measurable.measure(
        constraints.copy(maxWidth = constraints.maxWidth + extraWidth)
    )
    layout(constraints.maxWidth, placeable.height) {
        placeable.place(-horizontal.roundToPx(), 0)
    }
}

@Composable
private fun NotificationBody(
    isEnabled: Boolean,
    alarmTime: String,
    onToggle: (Boolean) -> Unit,
    onAlarmTimeClick: () -> Unit,
    onAlarmMessageClick: () -> Unit,
) {
    SectionHeader(text = stringResource(Res.string.setting_notification))

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = SettingItemVerticalPadding),
    ) {
        Text(
            text = stringResource(Res.string.setting_notification_setting),
            style = EbbingTheme.typography.bodyMSB,
            color = EbbingTheme.colors.black,
            modifier = Modifier.weight(1f),
        )

        EbbingToggle(
            checked = isEnabled,
            onCheckedChange = onToggle,
        )
    }

    if (isEnabled) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = SettingItemVerticalPadding),
        ) {
            Text(
                text = stringResource(Res.string.setting_alarm_time),
                style = EbbingTheme.typography.bodyMSB,
                color = EbbingTheme.colors.black,
                modifier = Modifier.weight(1f),
            )

            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(textDecoration = TextDecoration.Underline)) {
                        append(alarmTime)
                    }
                },
                textAlign = TextAlign.End,
                style = EbbingTheme.typography.bodyMSB,
                color = EbbingTheme.colors.primaryDefault,
                modifier = Modifier.clickable { onAlarmTimeClick() },
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = SettingItemVerticalPadding)
                .clickable { onAlarmMessageClick() },
        ) {
            Text(
                text = stringResource(Res.string.setting_alarm_message),
                style = EbbingTheme.typography.bodyMSB,
                color = EbbingTheme.colors.black,
                modifier = Modifier.weight(1f),
            )

            Icon(
                painter = painterResource(Res.drawable.ic_arrow_right),
                contentDescription = null,
                tint = EbbingTheme.colors.dark3,
            )
        }
    }

    SectionDivider()
}

@Composable
private fun InquiryBody(
    onInquiryClick: () -> Unit,
) {
    SectionHeader(text = stringResource(Res.string.setting_inquiry))

    SettingRow(
        title = stringResource(Res.string.setting_contact_us),
        onClick = onInquiryClick,
    )

    SectionDivider()
}

@Composable
private fun InAppReviewRow(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = SettingItemVerticalPadding),
    ) {
        Text(
            text = stringResource(Res.string.setting_app_review),
            style = EbbingTheme.typography.bodyMSB,
            color = EbbingTheme.colors.black,
            modifier = Modifier.weight(1f),
        )

        Icon(
            painter = painterResource(Res.drawable.ic_arrow_right),
            contentDescription = null,
            tint = EbbingTheme.colors.dark3,
        )
    }
}

@Composable
private fun ClearDataDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    EbbingDialog(onDismissRequest = onDismiss) {
        Column(
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 20.dp),
        ) {
            // Android ConfirmClearDialog와 동일: '데이터를 [초기화](빨강) 하시겠습니까?'
            Text(
                text = buildAnnotatedString {
                    append(stringResource(Res.string.setting_clear_dialog_title_prefix))
                    withStyle(SpanStyle(color = EbbingTheme.colors.error)) {
                        append(stringResource(Res.string.setting_clear_dialog_title_highlight))
                    }
                    append(stringResource(Res.string.setting_clear_dialog_title_suffix))
                },
                style = EbbingTheme.typography.headingMSB,
                color = EbbingTheme.colors.black,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.padding(top = 40.dp),
            )

            Text(
                text = stringResource(Res.string.setting_clear_dialog_subtext),
                style = EbbingTheme.typography.bodyMM,
                color = EbbingTheme.colors.dark1,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.padding(top = 16.dp, bottom = 24.dp),
            )

            EbbingDialogBottom(
                leftButtonText = stringResource(Res.string.setting_back),
                rightButtonText = stringResource(Res.string.setting_clear),
                onLeftButtonClick = onDismiss,
                onRightButtonClick = onConfirm,
            )
        }
    }
}

private enum class SettingBottomSheetType { ALARM_TIME, ALARM_MESSAGE, CALENDAR_START_DAY }

@Composable
private fun AlarmTimeBottomSheetContent(
    originHour: Int,
    originMinute: Int,
    onUpdateClick: (Int, Int) -> Unit,
) {
    val amText = stringResource(Res.string.ds_am)
    val pmText = stringResource(Res.string.ds_pm)
    val pickerAmPm = if (originHour >= 12) pmText else amText
    val pickerHour = when {
        originHour == 0 -> "12"
        originHour > 12 -> (originHour - 12).toString()
        else -> originHour.toString()
    }
    val pickerMinute = originMinute.toString().padStart(2, '0')

    var newAmPm by remember { mutableStateOf(pickerAmPm) }
    var newHour by remember { mutableStateOf(pickerHour.toInt()) }
    var newMinute by remember { mutableStateOf(originMinute) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
    ) {
        EbbingBottomSheetHeader(
            title = stringResource(Res.string.setting_alarm_time_title),
            subTitle = stringResource(Res.string.setting_alarm_time_subtitle),
        )

        EbbingPicker(
            initialAmPm = pickerAmPm,
            initialHour = pickerHour,
            initialMinute = pickerMinute,
            onValueChange = { amPm, hour, minute ->
                newAmPm = amPm
                newHour = hour
                newMinute = minute
            },
            modifier = Modifier.padding(vertical = 30.dp),
        )

        EbbingSolidButton(
            label = stringResource(Res.string.setting_apply_action),
            onClick = {
                val adjustedHour = when {
                    newAmPm == pmText && newHour == 12 -> 12
                    newAmPm == pmText -> newHour + 12
                    newAmPm == amText && newHour == 12 -> 0
                    else -> newHour
                }
                onUpdateClick(adjustedHour, newMinute)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, bottom = 10.dp),
        )
    }
}

@Composable
private fun AlarmMessageBottomSheetContent(
    sheetState: AlarmMessageBottomSheetState,
    onMessageChange: (String) -> Unit,
    onResetClick: () -> Unit,
    onUpdateClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
    ) {
        EbbingBottomSheetHeader(
            title = stringResource(Res.string.setting_alarm_message_setting),
            modifier = Modifier.padding(bottom = 16.dp),
        )

        val placeholderToken = stringResource(Res.string.alarm_placeholder_token)
        val placeholderGuide = stringResource(Res.string.setting_alarm_message_placeholder_guide)
        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(color = EbbingTheme.colors.primaryDefault, fontWeight = FontWeight.Bold)) {
                    append(placeholderToken)
                }
                append(placeholderGuide)
            },
            style = EbbingTheme.typography.bodySM,
            color = EbbingTheme.colors.dark3,
            modifier = Modifier.padding(bottom = 16.dp),
        )

        EbbingTextInputDefault(
            value = sheetState.message,
            onValueChange = onMessageChange,
            hint = stringResource(Res.string.setting_alarm_message_hint),
            modifier = Modifier.fillMaxWidth(),
        )

        val errorMessageText = when {
            sheetState.placeholderCount > 1 -> stringResource(Res.string.setting_alarm_message_error_placeholder)
            !sheetState.isValidLength -> stringResource(Res.string.setting_alarm_message_error_length)
            else -> ""
        }
        val previewSample = stringResource(Res.string.setting_alarm_message_preview_sample)
        val previewMessageText = when (sheetState.placeholderCount) {
            // 토큰은 로케일별로 다르므로 하드코딩("{할일}") 대신 placeholderToken 사용 (Android 동일)
            1 -> sheetState.message.replace(placeholderToken, previewSample)
            0 -> sheetState.message
            else -> ""
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
        ) {
            Text(
                text = errorMessageText,
                style = EbbingTheme.typography.captionR12,
                color = EbbingTheme.colors.error,
                modifier = Modifier.weight(1f),
            )

            Text(
                text = stringResource(Res.string.setting_alarm_message_length, sheetState.message.length),
                style = EbbingTheme.typography.captionR12,
                color = if (sheetState.isValidLength) EbbingTheme.colors.dark3 else EbbingTheme.colors.error,
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (sheetState.isValidPlaceholder && previewMessageText.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = EbbingTheme.colors.light3,
                        shape = RoundedCornerShape(8.dp),
                    )
                    .padding(12.dp),
            ) {
                Text(
                    text = stringResource(Res.string.setting_preview),
                    style = EbbingTheme.typography.captionR12,
                    color = EbbingTheme.colors.dark3,
                    modifier = Modifier.padding(bottom = 4.dp),
                )
                Text(
                    text = previewMessageText,
                    style = EbbingTheme.typography.bodyMM,
                    color = EbbingTheme.colors.dark1,
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        if (sheetState.shouldShowResetButton) {
            Text(
                text = stringResource(Res.string.setting_restore_default),
                style = EbbingTheme.typography.bodyMM,
                color = EbbingTheme.colors.primaryDefault,
                modifier = Modifier
                    .clickable { onResetClick() }
                    .padding(vertical = 8.dp),
            )

            Spacer(modifier = Modifier.height(8.dp))
        }

        EbbingSolidButton(
            label = stringResource(Res.string.setting_apply),
            onClick = onUpdateClick,
            enabled = sheetState.canApply,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
        )
    }
}

@Composable
private fun CalendarStartDayBottomSheetContent(
    originMondayStart: Boolean,
    onUpdateClick: (Boolean) -> Unit,
) {
    var newMondayStart by remember(originMondayStart) { mutableStateOf(originMondayStart) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
    ) {
        EbbingBottomSheetHeader(title = stringResource(Res.string.setting_calendar_start_day))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, bottom = 8.dp),
        ) {
            EbbingBottomSheetListItemDefault(
                label = stringResource(Res.string.setting_monday),
                checked = newMondayStart,
                onChecked = { newMondayStart = true },
            )

            EbbingBottomSheetListItemDefault(
                label = stringResource(Res.string.setting_sunday),
                checked = !newMondayStart,
                onChecked = { newMondayStart = false },
            )

            EbbingSolidButton(
                label = stringResource(Res.string.setting_apply_action),
                onClick = { onUpdateClick(newMondayStart) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = 10.dp),
            )
        }
    }
}
