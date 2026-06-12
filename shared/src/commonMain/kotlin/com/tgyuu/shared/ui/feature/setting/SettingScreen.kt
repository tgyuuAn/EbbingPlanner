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
import com.tgyuu.shared.designsystem.component.EbbingDialog
import com.tgyuu.shared.designsystem.component.EbbingDialogBottom
import com.tgyuu.shared.designsystem.component.EbbingMainTopBar
import com.tgyuu.shared.designsystem.component.EbbingToggle
import com.tgyuu.shared.designsystem.foundation.EbbingTheme

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
                    NotificationBody(isEnabled = state.isNotificationEnabled, alarmTime = state.alarmTime, onToggle = { viewModel.onIntent(SettingIntent.OnNotificationToggle(it)) }, onNotificationClick = { viewModel.onIntent(SettingIntent.OnNotificationClick) })
                    TagRepeatCycleBody(onTagManageClick = { viewModel.onIntent(SettingIntent.OnTagManageClick) }, onRepeatCycleManageClick = { viewModel.onIntent(SettingIntent.OnRepeatCycleManageClick) })
                    DataBody(onSyncClick = { viewModel.onIntent(SettingIntent.OnSyncClick) }, onClearClick = { showClearDialog = true })
                    Spacer(modifier = Modifier.height(24.dp))
                }
                // Right column
                Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 20.dp)) {
                    CalendarBody(mondayStart = state.mondayStart, onUpdateStartDay = { viewModel.onIntent(SettingIntent.OnUpdateStartDay(it)) })
                    ThemeBody(onThemeManageClick = { viewModel.onIntent(SettingIntent.OnThemeClick) })
                    AnnouncementBody(onPrivacyPolicyClick = { viewModel.onIntent(SettingIntent.OnPrivacyPolicyClick) }, onTermsClick = { viewModel.onIntent(SettingIntent.OnTermsOfUseClick) }, onNoticeClick = { viewModel.onIntent(SettingIntent.OnNoticeClick) })
                    InquiryBody(onInquiryClick = { viewModel.onIntent(SettingIntent.OnInquiryClick) })
                    InAppReviewRow(onClick = { viewModel.onIntent(SettingIntent.OnInAppReviewClick) })
                    SectionDivider()
                    VersionRow(version = state.appVersion)
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        } else {
            Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp)) {
                NotificationBody(isEnabled = state.isNotificationEnabled, alarmTime = state.alarmTime, onToggle = { viewModel.onIntent(SettingIntent.OnNotificationToggle(it)) }, onNotificationClick = { viewModel.onIntent(SettingIntent.OnNotificationClick) })
                TagRepeatCycleBody(onTagManageClick = { viewModel.onIntent(SettingIntent.OnTagManageClick) }, onRepeatCycleManageClick = { viewModel.onIntent(SettingIntent.OnRepeatCycleManageClick) })
                DataBody(onSyncClick = { viewModel.onIntent(SettingIntent.OnSyncClick) }, onClearClick = { showClearDialog = true })
                CalendarBody(mondayStart = state.mondayStart, onUpdateStartDay = { viewModel.onIntent(SettingIntent.OnUpdateStartDay(it)) })
                ThemeBody(onThemeManageClick = { viewModel.onIntent(SettingIntent.OnThemeClick) })
                AnnouncementBody(onPrivacyPolicyClick = { viewModel.onIntent(SettingIntent.OnPrivacyPolicyClick) }, onTermsClick = { viewModel.onIntent(SettingIntent.OnTermsOfUseClick) }, onNoticeClick = { viewModel.onIntent(SettingIntent.OnNoticeClick) })
                InquiryBody(onInquiryClick = { viewModel.onIntent(SettingIntent.OnInquiryClick) })
                InAppReviewRow(onClick = { viewModel.onIntent(SettingIntent.OnInAppReviewClick) })
                SectionDivider()
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
    onSyncClick: () -> Unit,
    onClearClick: () -> Unit,
) {
    SectionHeader(text = "데이터")

    SettingRow(
        title = "다른 기기와 동기화 하기",
        onClick = onSyncClick,
    )

    SettingRow(
        title = "데이터 초기화 하기",
        onClick = onClearClick,
    )

    SectionDivider()
}

@Composable
private fun ThemeBody(
    onThemeManageClick: () -> Unit,
) {
    SectionHeader(text = "테마")

    SettingRow(
        title = "테마 색상 변경",
        onClick = onThemeManageClick,
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
            .padding(vertical = 17.dp),
    ) {
        Text(
            text = "v$version",
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
            .padding(vertical = 17.dp),
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
    onUpdateStartDay: (Boolean) -> Unit,
) {
    SectionHeader(text = "달력")

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 17.dp),
    ) {
        Text(
            text = "달력 시작 요일",
            style = EbbingTheme.typography.bodyMSB,
            color = EbbingTheme.colors.black,
            modifier = Modifier.weight(1f),
        )

        Row {
            Text(
                text = "일요일",
                style = EbbingTheme.typography.bodyMSB,
                color = if (!mondayStart) EbbingTheme.colors.primaryDefault else EbbingTheme.colors.dark3,
                modifier = Modifier
                    .clickable { onUpdateStartDay(false) }
                    .padding(horizontal = 8.dp, vertical = 4.dp),
            )

            Text(
                text = "월요일",
                style = EbbingTheme.typography.bodyMSB,
                color = if (mondayStart) EbbingTheme.colors.primaryDefault else EbbingTheme.colors.dark3,
                modifier = Modifier
                    .clickable { onUpdateStartDay(true) }
                    .padding(horizontal = 8.dp, vertical = 4.dp),
            )
        }
    }

    SectionDivider()
}

@Composable
private fun SectionDivider(modifier: Modifier = Modifier) {
    HorizontalDivider(
        modifier = modifier.padding(vertical = 16.dp),
        thickness = 1.dp,
        color = EbbingTheme.colors.light2,
    )
}

@Composable
private fun NotificationBody(
    isEnabled: Boolean,
    alarmTime: String,
    onToggle: (Boolean) -> Unit,
    onNotificationClick: () -> Unit,
) {
    SectionHeader(text = "알림")

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 17.dp),
    ) {
        Text(
            text = "알림",
            style = EbbingTheme.typography.bodyMSB,
            color = EbbingTheme.colors.black,
            modifier = Modifier.weight(1f),
        )

        Text(
            text = if (isEnabled) "ON" else "OFF",
            style = EbbingTheme.typography.bodyMSB,
            color = if (isEnabled) EbbingTheme.colors.primaryDefault else EbbingTheme.colors.dark2,
            modifier = Modifier.padding(end = 8.dp),
        )

        EbbingToggle(
            checked = isEnabled,
            onCheckedChange = onToggle,
        )
    }

    if (isEnabled) {
        SettingRow(
            title = "알림 시간: $alarmTime",
            onClick = onNotificationClick,
        )
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
            .padding(vertical = 17.dp),
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
