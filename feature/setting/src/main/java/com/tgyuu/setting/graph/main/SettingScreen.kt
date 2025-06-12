@file:OptIn(ExperimentalPermissionsApi::class)

package com.tgyuu.setting.graph.main

import android.Manifest.permission.POST_NOTIFICATIONS
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.TIRAMISU
import android.provider.Settings
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowWidthSizeClass
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.tgyuu.common.ui.EbbingVisibleAnimation
import com.tgyuu.common.ui.clickable
import com.tgyuu.designsystem.BasePreview
import com.tgyuu.designsystem.EbbingPreview
import com.tgyuu.designsystem.R
import com.tgyuu.designsystem.component.EbbingMainTopBar
import com.tgyuu.designsystem.component.EbbingToggle
import com.tgyuu.designsystem.foundation.EbbingTheme
import com.tgyuu.domain.model.UpdateInfo
import com.tgyuu.setting.graph.main.contract.SettingIntent
import com.tgyuu.setting.graph.main.contract.SettingState
import com.tgyuu.setting.graph.main.ui.bottomsheet.AlarmTimeBottomSheet

@Composable
internal fun SettingRoute(
    viewModel: SettingViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    SettingScreen(
        state = state,
        onNoticeClick = { viewModel.onIntent(SettingIntent.OnNoticeClick) },
        onAlarmTimeClick = {
            viewModel.onIntent(SettingIntent.OnAlarmTimeClick({
                AlarmTimeBottomSheet(
                    originHour = state.alarmHour,
                    originMinute = state.alarmMinute,
                    onUpdateClick = { hour, minute ->
                        viewModel.onIntent(SettingIntent.OnUpdateAlarmTime(hour, minute))
                    },
                )
            }))
        },
        onTagManageClick = { viewModel.onIntent(SettingIntent.OnTagManageClick) },
        onRepeatCycleManageClick = { viewModel.onIntent(SettingIntent.OnRepeatCycleManageClick) },
        onPrivacyAndPolicyClick = { viewModel.onIntent(SettingIntent.OnPrivacyAndPolicyClick) },
        onTermsOfUseClick = { viewModel.onIntent(SettingIntent.OnTermsOfUseClick) },
        onInquiryClick = { viewModel.onIntent(SettingIntent.OnInquiryClick) },
        onNotificationToggleClick = { viewModel.onIntent(SettingIntent.OnNotificationToggleClick) },
    )
}

@Composable
private fun SettingScreen(
    state: SettingState,
    onNoticeClick: () -> Unit,
    onAlarmTimeClick: () -> Unit,
    onTagManageClick: () -> Unit,
    onRepeatCycleManageClick: () -> Unit,
    onPrivacyAndPolicyClick: () -> Unit,
    onTermsOfUseClick: () -> Unit,
    onInquiryClick: () -> Unit,
    onNotificationToggleClick: () -> Unit,
) {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass

    if (windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.COMPACT) {
        PhoneSettingScreen(
            state = state,
            onNoticeClick = onNoticeClick,
            onAlarmTimeClick = onAlarmTimeClick,
            onTagManageClick = onTagManageClick,
            onRepeatCycleManageClick = onRepeatCycleManageClick,
            onPrivacyAndPolicyClick = onPrivacyAndPolicyClick,
            onTermsOfUseClick = onTermsOfUseClick,
            onInquiryClick = onInquiryClick,
            onNotificationToggleClick = onNotificationToggleClick,
        )
    } else {
        TabletSettingScreen(
            state = state,
            onNoticeClick = onNoticeClick,
            onAlarmTimeClick = onAlarmTimeClick,
            onTagManageClick = onTagManageClick,
            onRepeatCycleManageClick = onRepeatCycleManageClick,
            onPrivacyAndPolicyClick = onPrivacyAndPolicyClick,
            onTermsOfUseClick = onTermsOfUseClick,
            onInquiryClick = onInquiryClick,
            onNotificationToggleClick = onNotificationToggleClick,
        )
    }
}

@Composable
private fun PhoneSettingScreen(
    state: SettingState,
    onNoticeClick: () -> Unit,
    onAlarmTimeClick: () -> Unit,
    onTagManageClick: () -> Unit,
    onRepeatCycleManageClick: () -> Unit,
    onPrivacyAndPolicyClick: () -> Unit,
    onTermsOfUseClick: () -> Unit,
    onInquiryClick: () -> Unit,
    onNotificationToggleClick: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
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
            NotificationBody(
                notificationEnabled = state.notificationEnabled,
                alarmTime = "${state.alarmHour}:${state.alarmMinute}",
                onNotificationToggleClick = onNotificationToggleClick,
                onAlarmTimeClick = onAlarmTimeClick,
            )

            UserConfigBody(
                onTagManageClick = onTagManageClick,
                onRepeatCycleManageClick = onRepeatCycleManageClick,
            )

            InquiryBody(onContactUsClick = onInquiryClick)

            AnnouncementBody(
                onNoticeClick = onNoticeClick,
                onPrivacyPolicy = onPrivacyAndPolicyClick,
                onTermsClick = onTermsOfUseClick,
            )

            UpdateBody(
                updateInfo = state.updateInfo,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 17.dp),
            )
        }
    }
}

@Composable
private fun TabletSettingScreen(
    state: SettingState,
    onNoticeClick: () -> Unit,
    onAlarmTimeClick: () -> Unit,
    onTagManageClick: () -> Unit,
    onRepeatCycleManageClick: () -> Unit,
    onPrivacyAndPolicyClick: () -> Unit,
    onTermsOfUseClick: () -> Unit,
    onInquiryClick: () -> Unit,
    onNotificationToggleClick: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        EbbingMainTopBar(
            title = "설정",
            modifier = Modifier.padding(horizontal = 20.dp),
        )

        Row(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .padding(horizontal = 20.dp),
            ) {
                NotificationBody(
                    notificationEnabled = state.notificationEnabled,
                    alarmTime = "${state.alarmHour}:${state.alarmMinute}",
                    onNotificationToggleClick = onNotificationToggleClick,
                    onAlarmTimeClick = onAlarmTimeClick,
                )

                UserConfigBody(
                    onTagManageClick = onTagManageClick,
                    onRepeatCycleManageClick = onRepeatCycleManageClick,
                )

                InquiryBody(onContactUsClick = onInquiryClick)

                AnnouncementBody(
                    onNoticeClick = onNoticeClick,
                    onPrivacyPolicy = onPrivacyAndPolicyClick,
                    onTermsClick = onTermsOfUseClick,
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .padding(horizontal = 20.dp),
            ) {
                UpdateBody(
                    updateInfo = state.updateInfo,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 17.dp),
                )
            }
        }
    }
}

@Composable
private fun NotificationBody(
    notificationEnabled: Boolean,
    alarmTime: String,
    onNotificationToggleClick: () -> Unit,
    onAlarmTimeClick: () -> Unit,
) {
    val context = LocalContext.current
    val permissionState = if (SDK_INT >= TIRAMISU) rememberPermissionState(POST_NOTIFICATIONS)
    else null

    LaunchedEffect(permissionState?.status) {
        if (permissionState?.status == PermissionStatus.Granted && !notificationEnabled) {
            onNotificationToggleClick()
        }
    }

    Text(
        text = "알림",
        style = EbbingTheme.typography.bodySM,
        color = EbbingTheme.colors.dark2,
        modifier = Modifier.padding(bottom = 8.dp),
    )

    val isOn = ((permissionState?.status == PermissionStatus.Granted || permissionState == null)
            && notificationEnabled)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 17.dp),
    ) {
        Text(
            text = "알림 설정",
            style = EbbingTheme.typography.headingSSB,
            color = EbbingTheme.colors.dark1,
            modifier = Modifier.weight(1f),
        )

        Text(
            text = if (isOn) "ON" else "OFF",
            style = EbbingTheme.typography.headingSM,
            color = if (isOn) EbbingTheme.colors.primaryDefault else EbbingTheme.colors.dark2,
            modifier = Modifier.padding(end = 12.dp),
        )

        EbbingToggle(
            checked = isOn,
            onCheckedChange = { desiredOn ->
                if (!desiredOn) {
                    onNotificationToggleClick()
                } else {
                    handlePermission(
                        context = context,
                        permission = permissionState,
                        onNotificationToggleClick = onNotificationToggleClick,
                    )
                }
            }
        )
    }

    EbbingVisibleAnimation(
        visible = isOn,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 17.dp),
        ) {
            Text(
                text = "알림 시간",
                style = EbbingTheme.typography.headingSSB,
                color = EbbingTheme.colors.dark1,
                modifier = Modifier.weight(1f),
            )

            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(textDecoration = TextDecoration.Underline)) {
                        append(alarmTime)
                    }
                },
                textAlign = TextAlign.End,
                style = EbbingTheme.typography.headingSM,
                color = EbbingTheme.colors.primaryDefault,
                modifier = Modifier.clickable { onAlarmTimeClick() },
            )
        }
    }

    HorizontalDivider(
        modifier = Modifier.padding(vertical = 16.dp),
        thickness = 1.dp,
        color = EbbingTheme.colors.light2
    )
}

@Composable
private fun UserConfigBody(
    onTagManageClick: () -> Unit,
    onRepeatCycleManageClick: () -> Unit,
) {
    Text(
        text = "태그 / 반복 주기",
        style = EbbingTheme.typography.bodySM,
        color = EbbingTheme.colors.dark2,
        modifier = Modifier.padding(bottom = 8.dp),
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 17.dp)
            .clickable { onTagManageClick() },
    ) {
        Text(
            text = "태그 관리",
            style = EbbingTheme.typography.headingSSB,
            color = EbbingTheme.colors.dark1,
            modifier = Modifier.weight(1f),
        )

        Image(
            painter = painterResource(R.drawable.ic_arrow_right),
            contentDescription = "상세 내용",
            modifier = Modifier.padding(start = 4.dp),
        )
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 17.dp)
            .clickable { onRepeatCycleManageClick() },
    ) {
        Text(
            text = "반복 주기 관리",
            style = EbbingTheme.typography.headingSSB,
            color = EbbingTheme.colors.dark1,
            modifier = Modifier.weight(1f),
        )

        Image(
            painter = painterResource(R.drawable.ic_arrow_right),
            contentDescription = "상세 내용",
            modifier = Modifier.padding(start = 4.dp),
        )
    }

    HorizontalDivider(
        color = EbbingTheme.colors.light2,
        thickness = 1.dp,
        modifier = Modifier.padding(vertical = 16.dp)
    )
}

@Composable
private fun InquiryBody(onContactUsClick: () -> Unit) {
    Text(
        text = "문의",
        style = EbbingTheme.typography.bodySM,
        color = EbbingTheme.colors.dark2,
        modifier = Modifier.padding(bottom = 8.dp),
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 17.dp)
            .clickable { onContactUsClick() },
    ) {
        Text(
            text = "문의하기",
            style = EbbingTheme.typography.headingSSB,
            color = EbbingTheme.colors.dark1,
            modifier = Modifier.weight(1f),
        )

        Image(
            painter = painterResource(R.drawable.ic_arrow_right),
            contentDescription = "상세 내용",
            modifier = Modifier.padding(start = 4.dp),
        )
    }

    HorizontalDivider(
        color = EbbingTheme.colors.light2,
        thickness = 1.dp,
        modifier = Modifier.padding(vertical = 16.dp)
    )
}

@Composable
private fun AnnouncementBody(
    onNoticeClick: () -> Unit,
    onPrivacyPolicy: () -> Unit,
    onTermsClick: () -> Unit,
) {
    Text(
        text = stringResource(R.string.setting_guidance),
        style = EbbingTheme.typography.bodySM,
        color = EbbingTheme.colors.dark2,
        modifier = Modifier.padding(bottom = 8.dp),
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 17.dp)
            .clickable { onNoticeClick() },
    ) {
        Text(
            text = stringResource(R.string.setting_announcement),
            style = EbbingTheme.typography.headingSSB,
            color = EbbingTheme.colors.dark1,
            modifier = Modifier.weight(1f),
        )

        Image(
            painter = painterResource(R.drawable.ic_arrow_right),
            contentDescription = "상세 내용",
            modifier = Modifier.padding(start = 4.dp),
        )
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 17.dp)
            .clickable { onPrivacyPolicy() },
    ) {
        Text(
            text = stringResource(R.string.setting_privacy_policy),
            style = EbbingTheme.typography.headingSSB,
            color = EbbingTheme.colors.dark1,
            modifier = Modifier.weight(1f),
        )

        Image(
            painter = painterResource(R.drawable.ic_arrow_right),
            contentDescription = "상세 내용",
            modifier = Modifier.padding(start = 4.dp),
        )
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 17.dp)
            .clickable { onTermsClick() },
    ) {
        Text(
            text = stringResource(R.string.setting_term),
            style = EbbingTheme.typography.headingSSB,
            color = EbbingTheme.colors.dark1,
            modifier = Modifier.weight(1f),
        )

        Image(
            painter = painterResource(R.drawable.ic_arrow_right),
            contentDescription = "상세 내용",
            modifier = Modifier.padding(start = 4.dp),
        )
    }

    HorizontalDivider(
        color = EbbingTheme.colors.light2,
        thickness = 1.dp,
        modifier = Modifier.padding(vertical = 16.dp)
    )
}

@Composable
private fun UpdateBody(
    updateInfo: UpdateInfo?,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val version = getVersionInfo(context, {})?.let { "v$it" } ?: ""

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
    ) {
        Text(
            text = stringResource(R.string.setting_version, version),
            style = EbbingTheme.typography.headingSSB,
            color = EbbingTheme.colors.dark3,
        )

        if (isShowUpdateButton(context, updateInfo)) {
            Image(
                painter = painterResource(R.drawable.ic_arrow_right),
                contentDescription = "상세 내용",
                modifier = Modifier
                    .padding(start = 4.dp)
                    .clickable {
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            "https://play.google.com/store/apps/details?id=com.tgyuu.ebbingplanner".toUri(),
                        )

                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(intent)
                    },
            )
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
internal fun handlePermission(
    context: Context,
    permission: PermissionState?,
    onNotificationToggleClick: () -> Unit,
) {
    permission?.let { state ->
        when (state.status) {
            PermissionStatus.Granted -> onNotificationToggleClick()

            is PermissionStatus.Denied -> {
                if (state.status.shouldShowRationale) {
                    // 한 번 거부한 상태 → (선택) 설명 다이얼로그를 띄우거나,
                    // 사용자가 다시 권한을 허용할 수 있도록 설정 화면으로 보냅니다.
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        .apply { data = Uri.fromParts("package", context.packageName, null) }
                    context.startActivity(intent)
                } else {
                    // 최초 요청 혹은 '다시 묻지 않음' 상태 → 권한 요청 다이얼로그
                    state.launchPermissionRequest()
                }
            }
        }
    }
}

private fun getVersionInfo(
    context: Context,
    onError: (Exception) -> Unit,
): String? {
    var version: String? = null
    try {
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        version = packageInfo.versionName
    } catch (e: PackageManager.NameNotFoundException) {
        onError(e)
    }
    return version
}

private fun isShowUpdateButton(
    context: Context,
    info: UpdateInfo?,
): Boolean {
    if (info == null) return false

    val currentVersion = context.packageManager.getPackageInfo(context.packageName, 0).versionName
    return checkShouldUpdate(currentVersion!!, info.minVersion)
}

private fun checkShouldUpdate(currentVersion: String, minVersion: String): Boolean {
    val current = normalizeVersion(currentVersion)
    val min = normalizeVersion(minVersion)
    return (0..2).any { current[it] < min[it] }
}

private fun normalizeVersion(version: String): List<Int> = version.split('.')
    .map { it.toIntOrNull() ?: 0 }
    .let { if (it.size == 2) it + 0 else it }

@EbbingPreview
@Composable
private fun PreviewSettingScreen() {
    BasePreview {
        SettingScreen(
            state = SettingState(),
            onNoticeClick = {},
            onAlarmTimeClick = {},
            onTagManageClick = {},
            onRepeatCycleManageClick = {},
            onPrivacyAndPolicyClick = {},
            onTermsOfUseClick = {},
            onInquiryClick = {},
            onNotificationToggleClick = {},
        )
    }
}
