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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.tgyuu.common.ui.clickable
import com.tgyuu.designsystem.R
import com.tgyuu.designsystem.component.BasePreview
import com.tgyuu.designsystem.component.EbbingMainTopBar
import com.tgyuu.designsystem.component.EbbingToggle
import com.tgyuu.designsystem.foundation.EbbingTheme
import com.tgyuu.setting.graph.main.contract.SettingIntent
import com.tgyuu.setting.graph.main.contract.SettingState

@Composable
internal fun SettingRoute(
    viewModel: SettingViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(viewModel) {
        val version = getVersionInfo(
            context = context,
            onError = { },
        )
        viewModel.setAppVersion(version?.let { "v$it" } ?: "")
    }

    SettingScreen(
        state = state,
        onNoticeClick = { viewModel.onIntent(SettingIntent.OnNoticeClick) },
        onPrivacyAndPolicyClick = { viewModel.onIntent(SettingIntent.OnPrivacyAndPolicyClick) },
        onTermsOfUseClick = { viewModel.onIntent(SettingIntent.OnTermsOfUseClick) },
        onInquiryClick = { viewModel.onIntent(SettingIntent.OnInquiryClick) },
    )
}

@Composable
private fun SettingScreen(
    state: SettingState,
    onNoticeClick: () -> Unit,
    onPrivacyAndPolicyClick: () -> Unit,
    onTermsOfUseClick: () -> Unit,
    onInquiryClick: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
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
            NotificationBody()

            InquiryBody(onContactUsClick = onInquiryClick)

            AnnouncementBody(
                onNoticeClick = onNoticeClick,
                onPrivacyPolicy = onPrivacyAndPolicyClick,
                onTermsClick = onTermsOfUseClick,
            )

            Text(
                text = stringResource(R.string.setting_version, state.version),
                style = EbbingTheme.typography.headingSSB,
                color = EbbingTheme.colors.dark3,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 17.dp),
            )
        }
    }
}

@Composable
private fun NotificationBody() {
    val notificationPermission =
        if (SDK_INT >= TIRAMISU) rememberPermissionState(POST_NOTIFICATIONS)
        else null
    val isPermissionGranted = notificationPermission?.status == PermissionStatus.Granted
            || notificationPermission == null

    val context = LocalContext.current

    Text(
        text = "알림",
        style = EbbingTheme.typography.bodySM,
        color = EbbingTheme.colors.dark2,
        modifier = Modifier.padding(bottom = 8.dp),
    )

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

        EbbingToggle(
            checked = isPermissionGranted,
            onCheckedChange = { handlePermission(context, notificationPermission) },
        )
    }

    HorizontalDivider(
        modifier = Modifier.padding(vertical = 16.dp),
        thickness = 1.dp,
        color = EbbingTheme.colors.light2
    )
}

@OptIn(ExperimentalPermissionsApi::class)
internal fun handlePermission(context: Context, permission: PermissionState?) {
    permission?.let { state ->
        when (state.status) {
            PermissionStatus.Granted -> return  // 이미 허용된 상태면 아무 일도 하지 않음

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

@Preview
@Composable
private fun PreviewSettingScreen() {
    BasePreview {
        SettingScreen(
            state = SettingState(version = "v1.0.0"),
            onNoticeClick = {},
            onPrivacyAndPolicyClick = {},
            onTermsOfUseClick = {},
            onInquiryClick = {},
        )
    }
}
