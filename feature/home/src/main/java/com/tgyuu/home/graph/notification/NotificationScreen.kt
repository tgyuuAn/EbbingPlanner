package com.tgyuu.home.graph.notification

import android.Manifest.permission.POST_NOTIFICATIONS
import android.content.Intent
import android.net.Uri
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.TIRAMISU
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.tgyuu.common.util.clickable
import com.tgyuu.common.util.throttledClickable
import com.tgyuu.designsystem.component.EbbingSubTopBar
import com.tgyuu.designsystem.component.EbbingTextInputDefault
import com.tgyuu.designsystem.component.EbbingToggle
import com.tgyuu.designsystem.foundation.EbbingTheme
import com.tgyuu.home.graph.addtodo.contract.AddTodoState
import com.tgyuu.home.graph.notification.ui.dialog.AlarmTimeDialog

@OptIn(ExperimentalPermissionsApi::class)
@Composable
internal fun NotificationScreen(
    state: AddTodoState,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit,
    onNotificationToggleClick: () -> Unit,
    onAlarmTimeChange: (Int, Int) -> Unit,
    onMessageChange: (String) -> Unit,
    onResetClick: () -> Unit,
) {
    val notificationState = state.notificationState
    val context = LocalContext.current
    val permissionState = if (SDK_INT >= TIRAMISU) rememberPermissionState(POST_NOTIFICATIONS)
    else null

    // 권한 요청 대기 중인지 추적
    var pendingNotificationEnable by remember { mutableStateOf(false) }

    // 권한 요청 후 승인되었을 때만 토글 켜기
    LaunchedEffect(permissionState?.status) {
        if (pendingNotificationEnable && permissionState?.status == PermissionStatus.Granted) {
            onNotificationToggleClick()
            pendingNotificationEnable = false
        }
    }

    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    // Dialog 상태
    var isShowTimeDialog by remember { mutableStateOf(false) }

    if (isShowTimeDialog) {
        AlarmTimeDialog(
            initialHour = notificationState.alarmHour,
            initialMinute = notificationState.alarmMinute,
            onDismissRequest = { isShowTimeDialog = false },
            onConfirmClick = { hour, minute ->
                onAlarmTimeChange(hour, minute)
                isShowTimeDialog = false
            },
        )
    }

    // 시간 포맷팅
    val formattedTime = remember(notificationState.alarmHour, notificationState.alarmMinute) {
        val hour = notificationState.alarmHour.toString().padStart(2, '0')
        val minute = notificationState.alarmMinute.toString().padStart(2, '0')
        "$hour:$minute"
    }

    Column(modifier = modifier.fillMaxSize()) {
        EbbingSubTopBar(
            title = "알림 설정",
            onNavigationClick = onBackClick,
            rightComponent = {
                Text(
                    text = "저장",
                    style = EbbingTheme.typography.bodyMSB,
                    color = EbbingTheme.colors.primaryDefault,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .throttledClickable(throttleTime = 1500L) {
                            onSaveClick()
                            focusManager.clearFocus()
                        },
                )
            },
            modifier = Modifier.padding(horizontal = 20.dp),
        )

        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .padding(vertical = 20.dp)
                .imePadding(),
        ) {
            Text(
                text = "다음 복습일을 놓치지 않도록 \n알려드릴까요?",
                style = EbbingTheme.typography.headingLSB,
                color = EbbingTheme.colors.black,
                modifier = Modifier.padding(horizontal = 20.dp),
            )

            Text(
                text = "알림을 설정하면 다음 복습일에 일정을 알려드려요.\n알림은 언제든 설정 탭에서 변경할 수 있어요.",
                style = EbbingTheme.typography.bodyMM,
                color = EbbingTheme.colors.dark3,
                modifier = Modifier
                    .padding(top = 12.dp)
                    .padding(horizontal = 20.dp),
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp)
                    .padding(horizontal = 20.dp),
            ) {
                Text(
                    text = "알림 받기",
                    style = EbbingTheme.typography.headingSSB,
                    color = EbbingTheme.colors.dark1,
                    modifier = Modifier.padding(end = 8.dp),
                )

                EbbingToggle(
                    checked = notificationState.notificationEnabled,
                    onCheckedChange = { desiredOn ->
                        if (!desiredOn) {
                            onNotificationToggleClick()
                        } else {
                            if (permissionState == null) {
                                onNotificationToggleClick()
                                return@EbbingToggle
                            }

                            when (val status = permissionState.status) {
                                PermissionStatus.Granted -> onNotificationToggleClick()
                                is PermissionStatus.Denied -> {
                                    pendingNotificationEnable = true
                                    if (status.shouldShowRationale) {
                                        val intent =
                                            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                                .apply {
                                                    data = Uri.fromParts(
                                                        "package",
                                                        context.packageName,
                                                        null
                                                    )
                                                }
                                        context.startActivity(intent)
                                    } else {
                                        permissionState.launchPermissionRequest()
                                    }
                                }
                            }
                        }
                    },
                )
            }

            AnimatedVisibility(
                visible = notificationState.notificationEnabled,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically(),
            ) {
                Column {
                    Spacer(
                        modifier = Modifier
                            .padding(vertical = 28.dp)
                            .fillMaxWidth()
                            .height(6.dp)
                            .background(color = EbbingTheme.colors.light2)
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .padding(bottom = 40.dp),
                    ) {
                        Text(
                            text = "알림 시간",
                            style = EbbingTheme.typography.headingSSB,
                            color = EbbingTheme.colors.dark1,
                            modifier = Modifier.padding(end = 8.dp),
                        )

                        Text(
                            text = buildAnnotatedString {
                                withStyle(SpanStyle(textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline)) {
                                    append(formattedTime)
                                }
                            },
                            style = EbbingTheme.typography.headingSM,
                            color = EbbingTheme.colors.primaryDefault,
                            modifier = Modifier.clickable { isShowTimeDialog = true },
                        )
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                    ) {
                        Text(
                            text = "알림 메시지",
                            style = EbbingTheme.typography.headingSSB,
                            color = EbbingTheme.colors.dark1,
                            modifier = Modifier.padding(bottom = 6.dp),
                        )

                        Text(
                            text = buildAnnotatedString {
                                withStyle(
                                    SpanStyle(
                                        color = EbbingTheme.colors.primaryDefault,
                                        fontWeight = FontWeight.Bold
                                    )
                                ) {
                                    append("{할일}")
                                }
                                append("은 할 일 제목으로 자동 변환됩니다 (최대 1번)")
                            },
                            style = EbbingTheme.typography.bodySM,
                            color = EbbingTheme.colors.dark2,
                            modifier = Modifier.padding(bottom = 16.dp),
                        )

                        EbbingTextInputDefault(
                            value = notificationState.message,
                            onValueChange = onMessageChange,
                            hint = "알림 메시지를 입력하세요",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 24.dp),
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = notificationState.errorMessage,
                                style = EbbingTheme.typography.captionM,
                                color = EbbingTheme.colors.error,
                                modifier = Modifier.weight(1f),
                            )

                            Text(
                                text = notificationState.lengthText,
                                style = EbbingTheme.typography.captionM,
                                color = if (notificationState.isValidLength) EbbingTheme.colors.dark3 else EbbingTheme.colors.error,
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        if (notificationState.isValidPlaceholder && notificationState.previewMessage.isNotEmpty()) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        color = EbbingTheme.colors.light3,
                                        shape = RoundedCornerShape(16.dp),
                                    )
                                    .padding(
                                        vertical = 12.dp,
                                        horizontal = 16.dp
                                    ),
                            ) {
                                Text(
                                    text = "미리보기",
                                    style = EbbingTheme.typography.captionM,
                                    color = EbbingTheme.colors.dark2,
                                    modifier = Modifier.padding(bottom = 4.dp),
                                )
                                Text(
                                    text = notificationState.previewMessage,
                                    style = EbbingTheme.typography.bodyMM,
                                    color = EbbingTheme.colors.dark1,
                                )
                            }
                        }

                        if (notificationState.shouldShowResetButton) {
                            Text(
                                text = "알림 메시지 기본값으로 복원",
                                style = EbbingTheme.typography.bodySSB,
                                color = EbbingTheme.colors.dark2,
                                modifier = Modifier
                                    .clickable { onResetClick() }
                                    .padding(top = 20.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}
