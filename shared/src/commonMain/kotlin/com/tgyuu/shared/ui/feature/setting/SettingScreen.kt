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
import kotlinx.coroutines.launch

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
            title = "설정",
            modifier = Modifier.padding(horizontal = 20.dp),
        )

        HorizontalDivider(color = EbbingTheme.colors.light2, thickness = 1.dp, modifier = Modifier.padding(bottom = 16.dp))

        if (isWide) {
            Row(modifier = Modifier.fillMaxSize()) {
                // Left column
                Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 20.dp)) {
                    NotificationBody(isEnabled = state.isNotificationEnabled, alarmTime = state.alarmTime, onToggle = { viewModel.onIntent(SettingIntent.OnNotificationToggle(it)) }, onAlarmTimeClick = { openSheet(SettingBottomSheetType.ALARM_TIME) }, onAlarmMessageClick = { viewModel.onIntent(SettingIntent.OnAlarmMessageOpen); openSheet(SettingBottomSheetType.ALARM_MESSAGE) })
                    CalendarBody(mondayStart = state.mondayStart, onClick = { openSheet(SettingBottomSheetType.CALENDAR_START_DAY) })
                    TagRepeatCycleBody(onTagManageClick = { viewModel.onIntent(SettingIntent.OnTagManageClick) }, onRepeatCycleManageClick = { viewModel.onIntent(SettingIntent.OnRepeatCycleManageClick) })
                    DataBody(autoBackupFeatureEnabled = state.autoBackupFeatureEnabled, autoBackupEnabled = state.autoBackupEnabled, lastSyncTime = state.lastSyncTime, onSyncClick = { viewModel.onIntent(SettingIntent.OnSyncClick) }, onClearClick = { showClearDialog = true }, onAutoBackupToggle = { viewModel.onIntent(SettingIntent.OnAutoBackupToggleClick) })
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
            Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp)) {
                NotificationBody(isEnabled = state.isNotificationEnabled, alarmTime = state.alarmTime, onToggle = { viewModel.onIntent(SettingIntent.OnNotificationToggle(it)) }, onAlarmTimeClick = { openSheet(SettingBottomSheetType.ALARM_TIME) }, onAlarmMessageClick = { viewModel.onIntent(SettingIntent.OnAlarmMessageOpen); openSheet(SettingBottomSheetType.ALARM_MESSAGE) })
                CalendarBody(mondayStart = state.mondayStart, onClick = { openSheet(SettingBottomSheetType.CALENDAR_START_DAY) })
                TagRepeatCycleBody(onTagManageClick = { viewModel.onIntent(SettingIntent.OnTagManageClick) }, onRepeatCycleManageClick = { viewModel.onIntent(SettingIntent.OnRepeatCycleManageClick) })
                DataBody(autoBackupFeatureEnabled = state.autoBackupFeatureEnabled, autoBackupEnabled = state.autoBackupEnabled, lastSyncTime = state.lastSyncTime, onSyncClick = { viewModel.onIntent(SettingIntent.OnSyncClick) }, onClearClick = { showClearDialog = true }, onAutoBackupToggle = { viewModel.onIntent(SettingIntent.OnAutoBackupToggleClick) })
                ThemeBody(onThemeManageClick = { viewModel.onIntent(SettingIntent.OnThemeClick) }, onWidgetAlphaClick = { viewModel.onIntent(SettingIntent.OnWidgetClick) })
                InquiryBody(onInquiryClick = { viewModel.onIntent(SettingIntent.OnInquiryClick) })
                AnnouncementBody(onPrivacyPolicyClick = { viewModel.onIntent(SettingIntent.OnPrivacyPolicyClick) }, onTermsClick = { viewModel.onIntent(SettingIntent.OnTermsOfUseClick) }, onNoticeClick = { viewModel.onIntent(SettingIntent.OnNoticeClick) })
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
    SectionHeader(text = "태그 / 반복 주기")

    SettingRow(
        title = "태그 관리",
        onClick = onTagManageClick,
    )

    SettingRow(
        title = "반복 주기 관리",
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
    onClearClick: () -> Unit,
    onAutoBackupToggle: () -> Unit,
) {
    SectionHeader(text = "데이터")

    SettingRow(
        title = "다른 기기와 동기화 하기",
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
                    text = "자동 백업",
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
                    text = "마지막 동기화 시간 : $lastSyncTime",
                    style = EbbingTheme.typography.bodySM,
                    color = EbbingTheme.colors.dark3,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
        }
    }

    SettingRow(
        title = "데이터 초기화 하기",
        onClick = onClearClick,
    )

    SectionDivider()
}

@Composable
private fun ThemeBody(
    onThemeManageClick: () -> Unit,
    onWidgetAlphaClick: () -> Unit,
) {
    SectionHeader(text = "테마")

    SettingRow(
        title = "테마 색상 변경",
        onClick = onThemeManageClick,
    )

    SettingRow(
        title = "위젯 알파 변경",
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
    SectionHeader(text = "안내")

    SettingRow(
        title = "공지사항",
        onClick = onNoticeClick,
    )

    SettingRow(
        title = "개인정보처리방침",
        onClick = onPrivacyPolicyClick,
    )

    SettingRow(
        title = "이용약관",
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
            text = "현재 버전 정보 v$version",
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
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
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
    SectionHeader(text = "달력")

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = SettingItemVerticalPadding),
    ) {
        Text(
            text = "달력 시작 요일",
            style = EbbingTheme.typography.bodyMSB,
            color = EbbingTheme.colors.black,
            modifier = Modifier.weight(1f),
        )

        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(textDecoration = TextDecoration.Underline)) {
                    append(if (mondayStart) "월요일" else "일요일")
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
    SectionHeader(text = "알림")

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = SettingItemVerticalPadding),
    ) {
        Text(
            text = "알림 설정",
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
                text = "알림 시간",
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
                text = "알림 메시지",
                style = EbbingTheme.typography.bodyMSB,
                color = EbbingTheme.colors.black,
                modifier = Modifier.weight(1f),
            )

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
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
    SectionHeader(text = "문의")

    SettingRow(
        title = "문의하기",
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
            text = "앱 리뷰 남기기",
            style = EbbingTheme.typography.bodyMSB,
            color = EbbingTheme.colors.black,
            modifier = Modifier.weight(1f),
        )

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
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
            Text(
                text = "데이터 초기화",
                style = EbbingTheme.typography.headingMSB,
                color = EbbingTheme.colors.black,
                modifier = Modifier.padding(top = 40.dp),
            )

            Text(
                text = "모든 데이터가 삭제됩니다.\n계속하시겠습니까?",
                style = EbbingTheme.typography.bodyMM,
                color = EbbingTheme.colors.dark1,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.padding(top = 16.dp, bottom = 24.dp),
            )

            EbbingDialogBottom(
                leftButtonText = "취소",
                rightButtonText = "초기화",
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
    val pickerAmPm = if (originHour >= 12) "오후" else "오전"
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
            title = "알람 시간",
            subTitle = "언제 남은 일정 알림을 보낼까요?",
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
            label = "적용하기",
            onClick = {
                val adjustedHour = when {
                    newAmPm == "오후" && newHour == 12 -> 12
                    newAmPm == "오후" -> newHour + 12
                    newAmPm == "오전" && newHour == 12 -> 0
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
            title = "알림 메시지 설정",
            modifier = Modifier.padding(bottom = 16.dp),
        )

        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(color = EbbingTheme.colors.primaryDefault, fontWeight = FontWeight.Bold)) {
                    append("{할일}")
                }
                append("은 할 일 제목으로 자동 변환됩니다 (최대 1번)")
            },
            style = EbbingTheme.typography.bodySM,
            color = EbbingTheme.colors.dark3,
            modifier = Modifier.padding(bottom = 16.dp),
        )

        EbbingTextInputDefault(
            value = sheetState.message,
            onValueChange = onMessageChange,
            hint = "알림 메시지를 입력하세요",
            modifier = Modifier.fillMaxWidth(),
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
        ) {
            Text(
                text = sheetState.errorMessage,
                style = EbbingTheme.typography.captionR12,
                color = EbbingTheme.colors.error,
                modifier = Modifier.weight(1f),
            )

            Text(
                text = sheetState.lengthText,
                style = EbbingTheme.typography.captionR12,
                color = if (sheetState.isValidLength) EbbingTheme.colors.dark3 else EbbingTheme.colors.error,
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (sheetState.isValidPlaceholder && sheetState.previewMessage.isNotEmpty()) {
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
                    text = "미리보기",
                    style = EbbingTheme.typography.captionR12,
                    color = EbbingTheme.colors.dark3,
                    modifier = Modifier.padding(bottom = 4.dp),
                )
                Text(
                    text = sheetState.previewMessage,
                    style = EbbingTheme.typography.bodyMM,
                    color = EbbingTheme.colors.dark1,
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        if (sheetState.shouldShowResetButton) {
            Text(
                text = "기본값으로 복원",
                style = EbbingTheme.typography.bodyMM,
                color = EbbingTheme.colors.primaryDefault,
                modifier = Modifier
                    .clickable { onResetClick() }
                    .padding(vertical = 8.dp),
            )

            Spacer(modifier = Modifier.height(8.dp))
        }

        EbbingSolidButton(
            label = "적용",
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
        EbbingBottomSheetHeader(title = "달력 시작 요일")

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, bottom = 8.dp),
        ) {
            EbbingBottomSheetListItemDefault(
                label = "월요일",
                checked = newMondayStart,
                onChecked = { newMondayStart = true },
            )

            EbbingBottomSheetListItemDefault(
                label = "일요일",
                checked = !newMondayStart,
                onChecked = { newMondayStart = false },
            )

            EbbingSolidButton(
                label = "적용하기",
                onClick = { onUpdateClick(newMondayStart) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = 10.dp),
            )
        }
    }
}
