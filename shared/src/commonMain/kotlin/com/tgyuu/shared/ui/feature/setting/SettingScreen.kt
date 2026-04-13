package com.tgyuu.shared.ui.feature.setting

import androidx.compose.foundation.clickable
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

    Column(modifier = modifier.fillMaxSize()) {
        EbbingMainTopBar(
            title = "설정",
            modifier = Modifier.padding(horizontal = 20.dp),
        )

        HorizontalDivider(
            color = EbbingTheme.colors.light2,
            thickness = 1.dp,
            modifier = Modifier.padding(bottom = 16.dp),
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
        ) {
            // Notification Section
            NotificationBody(
                isEnabled = state.isNotificationEnabled,
                alarmTime = state.alarmTime,
                onToggle = { viewModel.onIntent(SettingIntent.OnNotificationToggle(it)) },
                onNotificationClick = { viewModel.onIntent(SettingIntent.OnNotificationClick) },
            )

            // Tag / Repeat Cycle Section
            TagRepeatCycleBody(
                onTagManageClick = { viewModel.onIntent(SettingIntent.OnTagManageClick) },
                onRepeatCycleManageClick = { viewModel.onIntent(SettingIntent.OnRepeatCycleManageClick) },
            )

            // Data Section
            DataBody(
                onSyncClick = { viewModel.onIntent(SettingIntent.OnSyncClick) },
                onClearClick = { showClearDialog = true },
            )

            // Theme Section
            ThemeBody(
                onThemeManageClick = { viewModel.onIntent(SettingIntent.OnThemeClick) },
            )

            // Announcement Section
            AnnouncementBody(
                onPrivacyPolicyClick = { viewModel.onIntent(SettingIntent.OnPrivacyPolicyClick) },
                onTermsClick = { viewModel.onIntent(SettingIntent.OnTermsOfUseClick) },
            )

            // Inquiry Section
            InquiryBody()

            // In-App Review
            InAppReviewRow(
                onClick = { viewModel.onIntent(SettingIntent.OnInAppReviewClick) },
            )

            SectionDivider()

            // Version
            VersionRow(version = state.appVersion)

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
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
) {
    SectionHeader(text = "안내")

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
            style = EbbingTheme.typography.headingSSB,
            color = EbbingTheme.colors.dark3,
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
        color = EbbingTheme.colors.dark2,
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
            style = EbbingTheme.typography.headingSSB,
            color = EbbingTheme.colors.dark1,
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
            style = EbbingTheme.typography.headingSSB,
            color = EbbingTheme.colors.dark1,
            modifier = Modifier.weight(1f),
        )

        Text(
            text = if (isEnabled) "ON" else "OFF",
            style = EbbingTheme.typography.headingSSB,
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
private fun InquiryBody() {
    SectionHeader(text = "문의")

    SettingRow(
        title = "문의하기",
        onClick = { /* TODO: Open email or contact form */ },
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
            style = EbbingTheme.typography.headingSSB,
            color = EbbingTheme.colors.dark1,
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
