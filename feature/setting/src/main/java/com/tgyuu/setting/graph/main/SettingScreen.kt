@file:OptIn(ExperimentalPermissionsApi::class)

package com.tgyuu.setting.graph.main

import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.Activity
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Dp
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
import com.tgyuu.common.util.EbbingVisibleAnimation
import com.tgyuu.common.util.clickable
import com.tgyuu.designsystem.BasePreview
import com.tgyuu.designsystem.EbbingPreview
import com.tgyuu.designsystem.R
import com.tgyuu.designsystem.component.EbbingMainTopBar
import com.tgyuu.designsystem.component.EbbingToggle
import com.tgyuu.designsystem.foundation.EbbingTheme
import com.tgyuu.domain.model.UpdateInfo
import com.tgyuu.setting.BuildConfig
import com.tgyuu.setting.graph.main.contract.SettingIntent
import com.tgyuu.setting.graph.main.contract.SettingSideEffect
import com.tgyuu.setting.graph.main.contract.SettingState
import com.tgyuu.setting.graph.ui.bottomsheet.AlarmMessageBottomSheet
import com.tgyuu.setting.graph.ui.bottomsheet.AlarmTimeBottomSheet
import com.tgyuu.setting.graph.ui.bottomsheet.CalendarStartDayBottomSheet
import com.tgyuu.setting.graph.ui.dialog.ConfirmClearDialog
import kotlinx.coroutines.launch

private val SettingItemVerticalPadding = 16.dp
private val SettingDividerBottomPadding = 16.dp
private val SettingDividerThickness = 6.dp

@Composable
internal fun SettingRoute(
    viewModel: SettingViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val activity = context as? Activity

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { sideEffect ->
            when (sideEffect) {
                SettingSideEffect.RequestInAppReview -> {
                    viewModel.inAppReviewManager.openPlayStoreForReview()
                }
                is SettingSideEffect.RequestInAppUpdate -> {
                    activity?.let {
                        viewModel.inAppUpdateManager.requestUpdate(it, sideEffect.isImmediateUpdate)
                    }
                }
            }
        }
    }

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
        onAlarmMessageClick = {
            viewModel.onIntent(SettingIntent.OnAlarmMessageClick {
                AlarmMessageBottomSheet(
                    state = state.alarmMessageBottomSheet,
                    onMessageChange = { message ->
                        viewModel.onIntent(SettingIntent.OnAlarmMessageChange(message))
                    },
                    onResetClick = {
                        viewModel.onIntent(SettingIntent.OnAlarmMessageReset)
                    },
                    onUpdateClick = {
                        viewModel.onIntent(SettingIntent.OnApplyAlarmMessage)
                    },
                )
            })
        },
        onTagManageClick = { viewModel.onIntent(SettingIntent.OnTagManageClick) },
        onRepeatCycleManageClick = { viewModel.onIntent(SettingIntent.OnRepeatCycleManageClick) },
        onSyncClick = { viewModel.onIntent(SettingIntent.OnSyncClick) },
        onClearClick = { viewModel.onIntent(SettingIntent.OnClearClick) },
        onAppThemeManageClick = { viewModel.onIntent(SettingIntent.OnAppThemeManageClick) },
        onWidgetManageClick = { viewModel.onIntent(SettingIntent.OnWidgetManageClick) },
        onPrivacyAndPolicyClick = { viewModel.onIntent(SettingIntent.OnPrivacyAndPolicyClick) },
        onTermsOfUseClick = { viewModel.onIntent(SettingIntent.OnTermsOfUseClick) },
        onNotificationToggleClick = { viewModel.onIntent(SettingIntent.OnNotificationToggleClick) },
        onStartDayClick = {
            viewModel.onIntent(SettingIntent.OnStartDayClick {
                CalendarStartDayBottomSheet(
                    originMondayStart = state.mondayStart,
                    onUpdateClick = { mondayStart ->
                        viewModel.onIntent(SettingIntent.OnUpdateStartDay(mondayStart))
                    },
                )
            })
        },
        onInAppReviewClick = { viewModel.onIntent(SettingIntent.OnInAppReviewClick) },
        onUpdateClick = { isImmediateUpdate ->
            viewModel.onIntent(SettingIntent.OnUpdateClick(isImmediateUpdate))
        },
    )
}

@Composable
private fun SettingScreen(
    state: SettingState,
    onNoticeClick: () -> Unit,
    onAlarmTimeClick: () -> Unit,
    onAlarmMessageClick: () -> Unit,
    onTagManageClick: () -> Unit,
    onRepeatCycleManageClick: () -> Unit,
    onSyncClick: () -> Unit,
    onClearClick: () -> Unit,
    onAppThemeManageClick: () -> Unit,
    onWidgetManageClick: () -> Unit,
    onPrivacyAndPolicyClick: () -> Unit,
    onTermsOfUseClick: () -> Unit,
    onNotificationToggleClick: () -> Unit,
    onStartDayClick: () -> Unit,
    onInAppReviewClick: () -> Unit,
    onUpdateClick: (isImmediateUpdate: Boolean) -> Unit,
) {
    var isShowClearConfirm by remember { mutableStateOf(false) }
    if (isShowClearConfirm) {
        ConfirmClearDialog(
            onDismissRequest = { isShowClearConfirm = false },
            onClearClick = {
                isShowClearConfirm = false
                onClearClick()
            },
        )
    }

    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    if (windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.COMPACT) {
        PhoneSettingScreen(
            state = state,
            onNoticeClick = onNoticeClick,
            onAlarmTimeClick = onAlarmTimeClick,
            onAlarmMessageClick = onAlarmMessageClick,
            onTagManageClick = onTagManageClick,
            onRepeatCycleManageClick = onRepeatCycleManageClick,
            onSyncClick = onSyncClick,
            onClearClick = { isShowClearConfirm = true },
            onAppThemeManageClick = onAppThemeManageClick,
            onWidgetManageClick = onWidgetManageClick,
            onPrivacyAndPolicyClick = onPrivacyAndPolicyClick,
            onTermsOfUseClick = onTermsOfUseClick,
            onNotificationToggleClick = onNotificationToggleClick,
            onStartDayClick = onStartDayClick,
            onInAppReviewClick = onInAppReviewClick,
            onUpdateClick = onUpdateClick,
        )
    } else {
        TabletSettingScreen(
            state = state,
            onNoticeClick = onNoticeClick,
            onAlarmTimeClick = onAlarmTimeClick,
            onAlarmMessageClick = onAlarmMessageClick,
            onTagManageClick = onTagManageClick,
            onRepeatCycleManageClick = onRepeatCycleManageClick,
            onSyncClick = onSyncClick,
            onClearClick = { isShowClearConfirm = true },
            onAppThemeManageClick = onAppThemeManageClick,
            onWidgetManageClick = onWidgetManageClick,
            onPrivacyAndPolicyClick = onPrivacyAndPolicyClick,
            onTermsOfUseClick = onTermsOfUseClick,
            onNotificationToggleClick = onNotificationToggleClick,
            onStartDayClick = onStartDayClick,
            onInAppReviewClick = onInAppReviewClick,
            onUpdateClick = onUpdateClick,
        )
    }
}

@Composable
private fun PhoneSettingScreen(
    state: SettingState,
    onNoticeClick: () -> Unit,
    onAlarmTimeClick: () -> Unit,
    onAlarmMessageClick: () -> Unit,
    onTagManageClick: () -> Unit,
    onRepeatCycleManageClick: () -> Unit,
    onSyncClick: () -> Unit,
    onClearClick: () -> Unit,
    onAppThemeManageClick: () -> Unit,
    onWidgetManageClick: () -> Unit,
    onPrivacyAndPolicyClick: () -> Unit,
    onTermsOfUseClick: () -> Unit,
    onNotificationToggleClick: () -> Unit,
    onStartDayClick: () -> Unit,
    onInAppReviewClick: () -> Unit,
    onUpdateClick: (isImmediateUpdate: Boolean) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        EbbingMainTopBar(
            title = "설정",
            modifier = Modifier.padding(horizontal = 20.dp),
        )

        HorizontalDivider(
            color = EbbingTheme.colors.fillTextfield,
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
                onAlarmMessageClick = onAlarmMessageClick,
            )

            CalendarStartDayBody(
                mondayStart = state.mondayStart,
                onStartDayClick = onStartDayClick,
            )

            TagRepeatCycleBody(
                onTagManageClick = onTagManageClick,
                onRepeatCycleManageClick = onRepeatCycleManageClick,
            )

            DataBody(
                onSyncClick = onSyncClick,
                onClearClick = onClearClick,
            )

            ThemeBody(
                onThemeManageClick = onAppThemeManageClick,
                onWidgetAlphaManageClick = onWidgetManageClick,
            )

            InquiryBody()

            AnnouncementBody(
                onNoticeClick = onNoticeClick,
                onPrivacyPolicy = onPrivacyAndPolicyClick,
                onTermsClick = onTermsOfUseClick,
            )

            InAppReviewRow(onInAppReviewClick = onInAppReviewClick)

            UpdateBody(
                updateInfo = state.updateInfo,
                hardUpdateInfo = state.hardUpdateInfo,
                onUpdateClick = onUpdateClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = SettingItemVerticalPadding),
            )
        }
    }
}

@Composable
private fun TabletSettingScreen(
    state: SettingState,
    onNoticeClick: () -> Unit,
    onAlarmTimeClick: () -> Unit,
    onAlarmMessageClick: () -> Unit,
    onTagManageClick: () -> Unit,
    onRepeatCycleManageClick: () -> Unit,
    onSyncClick: () -> Unit,
    onClearClick: () -> Unit,
    onAppThemeManageClick: () -> Unit,
    onWidgetManageClick: () -> Unit,
    onPrivacyAndPolicyClick: () -> Unit,
    onTermsOfUseClick: () -> Unit,
    onNotificationToggleClick: () -> Unit,
    onStartDayClick: () -> Unit,
    onInAppReviewClick: () -> Unit,
    onUpdateClick: (isImmediateUpdate: Boolean) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        EbbingMainTopBar(
            title = "설정",
            modifier = Modifier.padding(horizontal = 20.dp),
        )

        Row(modifier = Modifier.fillMaxWidth()) {
            val leftScrollState = rememberScrollState()
            val rightScrollState = rememberScrollState()

            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .verticalScroll(leftScrollState)
                    .weight(1f)
                    .padding(horizontal = 20.dp),
            ) {
                NotificationBody(
                    notificationEnabled = state.notificationEnabled,
                    alarmTime = "${state.alarmHour}:${state.alarmMinute}",
                    onNotificationToggleClick = onNotificationToggleClick,
                    onAlarmTimeClick = onAlarmTimeClick,
                    onAlarmMessageClick = onAlarmMessageClick,
                )

                CalendarStartDayBody(
                    mondayStart = state.mondayStart,
                    onStartDayClick = onStartDayClick,
                )

                TagRepeatCycleBody(
                    onTagManageClick = onTagManageClick,
                    onRepeatCycleManageClick = onRepeatCycleManageClick,
                )

                DataBody(
                    onSyncClick = onSyncClick,
                    onClearClick = onClearClick,
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .verticalScroll(rightScrollState)
                    .weight(1f)
                    .padding(horizontal = 20.dp),
            ) {
                ThemeBody(
                    onThemeManageClick = onAppThemeManageClick,
                    onWidgetAlphaManageClick = onWidgetManageClick,
                )

                InquiryBody()

                AnnouncementBody(
                    onNoticeClick = onNoticeClick,
                    onPrivacyPolicy = onPrivacyAndPolicyClick,
                    onTermsClick = onTermsOfUseClick,
                )

                InAppReviewRow(onInAppReviewClick = onInAppReviewClick)

                UpdateBody(
                    updateInfo = state.updateInfo,
                    hardUpdateInfo = state.hardUpdateInfo,
                    onUpdateClick = onUpdateClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = SettingItemVerticalPadding),
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
    onAlarmMessageClick: () -> Unit,
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
        style = EbbingTheme.typography.body14M,
        color = EbbingTheme.colors.textSub,
        modifier = Modifier.padding(bottom = 8.dp),
    )

    val isOn = ((permissionState?.status == PermissionStatus.Granted || permissionState == null)
            && notificationEnabled)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = SettingItemVerticalPadding),
    ) {
        Text(
            text = "알림 설정",
            style = EbbingTheme.typography.heading16SB,
            color = EbbingTheme.colors.textOnBackground,
            modifier = Modifier.weight(1f),
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
                .padding(vertical = SettingItemVerticalPadding),
        ) {
            Text(
                text = "알림 시간",
                style = EbbingTheme.typography.heading16SB,
                color = EbbingTheme.colors.textOnBackground,
                modifier = Modifier.weight(1f),
            )

            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(textDecoration = TextDecoration.Underline)) {
                        append(alarmTime)
                    }
                },
                textAlign = TextAlign.End,
                style = EbbingTheme.typography.heading16SB,
                color = EbbingTheme.colors.textPrimary,
                modifier = Modifier.clickable { onAlarmTimeClick() },
            )
        }
    }

    EbbingVisibleAnimation(
        visible = isOn,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = SettingItemVerticalPadding)
                .clickable { onAlarmMessageClick() },
        ) {
            Text(
                text = "알림 메시지",
                style = EbbingTheme.typography.heading16SB,
                color = EbbingTheme.colors.textOnBackground,
                modifier = Modifier.weight(1f),
            )

            Image(
                painter = painterResource(R.drawable.ic_arrow_right),
                contentDescription = "상세 내용",
                modifier = Modifier.padding(start = 4.dp),
            )
        }
    }

    HorizontalDivider(
        modifier = Modifier
            .ignoreHorizontalPadding()
            .padding(bottom = SettingDividerBottomPadding),
        thickness = SettingDividerThickness,
        color = EbbingTheme.colors.strokeSecondary
    )
}

@Composable
private fun CalendarStartDayBody(
    mondayStart: Boolean,
    onStartDayClick: () -> Unit,
) {
    Text(
        text = "달력",
        style = EbbingTheme.typography.body14M,
        color = EbbingTheme.colors.textSub,
        modifier = Modifier.padding(bottom = 8.dp),
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = SettingItemVerticalPadding),
    ) {
        Text(
            text = "달력 시작 요일",
            style = EbbingTheme.typography.heading16SB,
            color = EbbingTheme.colors.textOnBackground,
            modifier = Modifier.weight(1f),
        )

        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(textDecoration = TextDecoration.Underline)) {
                    append(if (mondayStart) "월요일" else "일요일")
                }
            },
            textAlign = TextAlign.End,
            style = EbbingTheme.typography.heading16SB,
            color = EbbingTheme.colors.textPrimary,
            modifier = Modifier.clickable { onStartDayClick() },
        )
    }

    HorizontalDivider(
        modifier = Modifier
            .ignoreHorizontalPadding()
            .padding(bottom = SettingDividerBottomPadding),
        thickness = SettingDividerThickness,
        color = EbbingTheme.colors.strokeSecondary
    )
}

@Composable
private fun TagRepeatCycleBody(
    onTagManageClick: () -> Unit,
    onRepeatCycleManageClick: () -> Unit,
) {
    Text(
        text = "태그 / 반복 주기",
        style = EbbingTheme.typography.body14M,
        color = EbbingTheme.colors.textSub,
        modifier = Modifier.padding(bottom = 8.dp),
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = SettingItemVerticalPadding)
            .clickable { onTagManageClick() },
    ) {
        Text(
            text = "태그 관리",
            style = EbbingTheme.typography.heading16SB,
            color = EbbingTheme.colors.textOnBackground,
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
            .padding(vertical = SettingItemVerticalPadding)
            .clickable { onRepeatCycleManageClick() },
    ) {
        Text(
            text = "반복 주기 관리",
            style = EbbingTheme.typography.heading16SB,
            color = EbbingTheme.colors.textOnBackground,
            modifier = Modifier.weight(1f),
        )

        Image(
            painter = painterResource(R.drawable.ic_arrow_right),
            contentDescription = "상세 내용",
            modifier = Modifier.padding(start = 4.dp),
        )
    }

    HorizontalDivider(
        modifier = Modifier
            .ignoreHorizontalPadding()
            .padding(bottom = SettingDividerBottomPadding),
        thickness = SettingDividerThickness,
        color = EbbingTheme.colors.strokeSecondary
    )
}

@Composable
private fun DataBody(
    onSyncClick: () -> Unit,
    onClearClick: () -> Unit,
) {
    Text(
        text = "데이터",
        style = EbbingTheme.typography.body14M,
        color = EbbingTheme.colors.textSub,
        modifier = Modifier.padding(bottom = 8.dp),
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = SettingItemVerticalPadding)
            .clickable { onSyncClick() },
    ) {
        Text(
            text = "다른 기기와 동기화 하기",
            style = EbbingTheme.typography.heading16SB,
            color = EbbingTheme.colors.textOnBackground,
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
            .padding(vertical = SettingItemVerticalPadding)
            .clickable { onClearClick() },
    ) {
        Text(
            text = "데이터 초기화 하기",
            style = EbbingTheme.typography.heading16SB,
            color = EbbingTheme.colors.textOnBackground,
            modifier = Modifier.weight(1f),
        )

        Image(
            painter = painterResource(R.drawable.ic_arrow_right),
            contentDescription = "상세 내용",
            modifier = Modifier.padding(start = 4.dp),
        )
    }

    HorizontalDivider(
        modifier = Modifier
            .ignoreHorizontalPadding()
            .padding(bottom = SettingDividerBottomPadding),
        thickness = SettingDividerThickness,
        color = EbbingTheme.colors.strokeSecondary
    )
}

@Composable
private fun InquiryBody() {
    val context = LocalContext.current

    Text(
        text = "문의",
        style = EbbingTheme.typography.body14M,
        color = EbbingTheme.colors.textSub,
        modifier = Modifier.padding(bottom = 8.dp),
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = SettingItemVerticalPadding)
            .clickable {
                val url = BuildConfig.EBBING_TALK_URL
                val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            },
    ) {
        Text(
            text = "문의하기",
            style = EbbingTheme.typography.heading16SB,
            color = EbbingTheme.colors.textOnBackground,
            modifier = Modifier.weight(1f),
        )

        Image(
            painter = painterResource(R.drawable.ic_arrow_right),
            contentDescription = "상세 내용",
            modifier = Modifier.padding(start = 4.dp),
        )
    }

    HorizontalDivider(
        modifier = Modifier
            .ignoreHorizontalPadding()
            .padding(bottom = SettingDividerBottomPadding),
        thickness = SettingDividerThickness,
        color = EbbingTheme.colors.strokeSecondary
    )
}

@Composable
private fun InAppReviewRow(onInAppReviewClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = SettingItemVerticalPadding)
            .clickable { onInAppReviewClick() },
    ) {
        Text(
            text = "앱 리뷰 작성",
            style = EbbingTheme.typography.heading16SB,
            color = EbbingTheme.colors.textOnBackground,
            modifier = Modifier.weight(1f),
        )

        Image(
            painter = painterResource(R.drawable.ic_arrow_right),
            contentDescription = "상세 내용",
            modifier = Modifier.padding(start = 4.dp),
        )
    }
}

@Composable
private fun AnnouncementBody(
    onNoticeClick: () -> Unit,
    onPrivacyPolicy: () -> Unit,
    onTermsClick: () -> Unit,
) {
    Text(
        text = stringResource(R.string.setting_guidance),
        style = EbbingTheme.typography.body14M,
        color = EbbingTheme.colors.textSub,
        modifier = Modifier.padding(bottom = 8.dp),
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = SettingItemVerticalPadding)
            .clickable { onNoticeClick() },
    ) {
        Text(
            text = stringResource(R.string.setting_announcement),
            style = EbbingTheme.typography.heading16SB,
            color = EbbingTheme.colors.textOnBackground,
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
            .padding(vertical = SettingItemVerticalPadding)
            .clickable { onPrivacyPolicy() },
    ) {
        Text(
            text = stringResource(R.string.setting_privacy_policy),
            style = EbbingTheme.typography.heading16SB,
            color = EbbingTheme.colors.textOnBackground,
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
            .padding(vertical = SettingItemVerticalPadding)
            .clickable { onTermsClick() },
    ) {
        Text(
            text = stringResource(R.string.setting_term),
            style = EbbingTheme.typography.heading16SB,
            color = EbbingTheme.colors.textOnBackground,
            modifier = Modifier.weight(1f),
        )

        Image(
            painter = painterResource(R.drawable.ic_arrow_right),
            contentDescription = "상세 내용",
            modifier = Modifier.padding(start = 4.dp),
        )
    }

    HorizontalDivider(
        modifier = Modifier
            .ignoreHorizontalPadding()
            .padding(bottom = SettingDividerBottomPadding),
        thickness = SettingDividerThickness,
        color = EbbingTheme.colors.strokeSecondary
    )
}

@Composable
private fun ThemeBody(
    onThemeManageClick: () -> Unit,
    onWidgetAlphaManageClick: () -> Unit,
) {
    Text(
        text = stringResource(R.string.setting_theme),
        style = EbbingTheme.typography.body14M,
        color = EbbingTheme.colors.textSub,
        modifier = Modifier.padding(bottom = 8.dp),
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = SettingItemVerticalPadding)
            .clickable { onThemeManageClick() },
    ) {
        Text(
            text = stringResource(R.string.setting_theme_color_change),
            style = EbbingTheme.typography.heading16SB,
            color = EbbingTheme.colors.textOnBackground,
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
            .padding(vertical = SettingItemVerticalPadding)
            .clickable { onWidgetAlphaManageClick() },
    ) {
        Text(
            text = stringResource(R.string.setting_widget_alpha_change),
            style = EbbingTheme.typography.heading16SB,
            color = EbbingTheme.colors.textOnBackground,
            modifier = Modifier.weight(1f),
        )

        Image(
            painter = painterResource(R.drawable.ic_arrow_right),
            contentDescription = "상세 내용",
            modifier = Modifier.padding(start = 4.dp),
        )
    }

    HorizontalDivider(
        modifier = Modifier
            .ignoreHorizontalPadding()
            .padding(bottom = SettingDividerBottomPadding),
        thickness = SettingDividerThickness,
        color = EbbingTheme.colors.strokeSecondary
    )
}

@Composable
private fun UpdateBody(
    updateInfo: UpdateInfo?,
    hardUpdateInfo: UpdateInfo?,
    onUpdateClick: (isImmediateUpdate: Boolean) -> Unit,
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
            style = EbbingTheme.typography.heading16SB,
            color = EbbingTheme.colors.textOnBackground,
        )

        if (isShowUpdateButton(context, updateInfo)) {
            val isImmediateUpdate = shouldUpdate(context, hardUpdateInfo)

            Image(
                painter = painterResource(R.drawable.ic_arrow_right),
                contentDescription = "상세 내용",
                modifier = Modifier
                    .padding(start = 4.dp)
                    .clickable { onUpdateClick(isImmediateUpdate) },
            )
        }
    }
}

private fun shouldUpdate(context: Context, info: UpdateInfo?): Boolean {
    if (info == null) return false

    val currentVersion = context.packageManager.getPackageInfo(context.packageName, 0)
        .versionName ?: return false

    return checkShouldUpdate(currentVersion, info.minVersion)
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

private fun Modifier.ignoreHorizontalPadding(horizontal: Dp = 20.dp) = layout { measurable, constraints ->
    val extraWidth = (horizontal * 2).roundToPx()
    val placeable = measurable.measure(
        constraints.copy(maxWidth = constraints.maxWidth + extraWidth)
    )
    layout(constraints.maxWidth, placeable.height) {
        placeable.place(-horizontal.roundToPx(), 0)
    }
}

@EbbingPreview
@Composable
private fun PreviewSettingScreen() {
    BasePreview {
        SettingScreen(
            state = SettingState(),
            onNoticeClick = {},
            onAlarmTimeClick = {},
            onAlarmMessageClick = {},
            onTagManageClick = {},
            onRepeatCycleManageClick = {},
            onSyncClick = {},
            onClearClick = {},
            onAppThemeManageClick = {},
            onWidgetManageClick = {},
            onPrivacyAndPolicyClick = {},
            onTermsOfUseClick = {},
            onNotificationToggleClick = {},
            onStartDayClick = {},
            onInAppReviewClick = {},
            onUpdateClick = {},
        )
    }
}
