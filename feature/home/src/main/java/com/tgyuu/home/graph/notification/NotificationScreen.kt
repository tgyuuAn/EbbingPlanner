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
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowWidthSizeClass
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.tgyuu.analytics.AnalyticsEvent
import com.tgyuu.analytics.AnalyticsHelper
import com.tgyuu.analytics.LocalAnalyticsHelper
import com.tgyuu.common.util.clickable
import com.tgyuu.common.util.throttledClickable
import com.tgyuu.designsystem.component.EbbingSolidButton
import com.tgyuu.designsystem.component.EbbingSubTopBar
import com.tgyuu.designsystem.component.EbbingTextInputDefault
import com.tgyuu.designsystem.component.EbbingToggle
import com.tgyuu.designsystem.foundation.EbbingTheme
import com.tgyuu.designsystem.R
import com.tgyuu.home.graph.addtodo.contract.NotificationState
import com.tgyuu.home.graph.notification.ui.dialog.AlarmTimeDialog

@OptIn(ExperimentalPermissionsApi::class)
@Composable
internal fun NotificationScreen(
    state: NotificationState,
    modifier: Modifier = Modifier,
    isTreatment: Boolean = false,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit,
    onNotificationToggleClick: () -> Unit,
    onAlarmTimeChange: (Int, Int) -> Unit,
    onMessageChange: (String) -> Unit,
    onResetClick: () -> Unit,
) {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass

    if (windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.COMPACT) {
        NotificationScreenPhone(
            state = state,
            isTreatment = isTreatment,
            modifier = modifier,
            onBackClick = onBackClick,
            onSaveClick = onSaveClick,
            onNotificationToggleClick = onNotificationToggleClick,
            onAlarmTimeChange = onAlarmTimeChange,
            onMessageChange = onMessageChange,
            onResetClick = onResetClick,
        )
    } else {
        NotificationScreenTablet(
            state = state,
            isTreatment = isTreatment,
            modifier = modifier,
            onBackClick = onBackClick,
            onSaveClick = onSaveClick,
            onNotificationToggleClick = onNotificationToggleClick,
            onAlarmTimeChange = onAlarmTimeChange,
            onMessageChange = onMessageChange,
            onResetClick = onResetClick,
        )
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun NotificationScreenPhone(
    state: NotificationState,
    isTreatment: Boolean,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit,
    onNotificationToggleClick: () -> Unit,
    onAlarmTimeChange: (Int, Int) -> Unit,
    onMessageChange: (String) -> Unit,
    onResetClick: () -> Unit,
) {
    val analyticsHelper = LocalAnalyticsHelper.current
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    val permissionState = if (SDK_INT >= TIRAMISU) rememberPermissionState(POST_NOTIFICATIONS)
    else null

    var pendingNotificationEnable by remember { mutableStateOf(false) }
    var isShowTimeDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        analyticsHelper.logEvent(
            AnalyticsEvent.View(
                screenName = "NotificationNudge",
                properties = mapOf("variant" to if (isTreatment) "TREATMENT_V2" else "CONTROL_V2"),
            )
        )
    }

    LaunchedEffect(permissionState?.status) {
        if (pendingNotificationEnable && permissionState?.status == PermissionStatus.Granted) {
            onNotificationToggleClick()
            pendingNotificationEnable = false
        }
    }

    if (isShowTimeDialog) {
        AlarmTimeDialog(
            initialHour = state.alarmHour,
            initialMinute = state.alarmMinute,
            onDismissRequest = { isShowTimeDialog = false },
            onConfirmClick = { hour, minute ->
                onAlarmTimeChange(hour, minute)
                isShowTimeDialog = false
            },
        )
    }

    Column(modifier = modifier.fillMaxSize().imePadding()) {
        NotificationTopBar(
            analyticsHelper = analyticsHelper,
            state = state,
            isTreatment = isTreatment,
            onBackClick = onBackClick,
            onSaveClick = {
                onSaveClick()
                focusManager.clearFocus()
            },
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
                .padding(vertical = 20.dp),
        ) {
            NotificationHeader()

            NotificationToggleSection(
                checked = state.notificationEnabled,
                analyticsHelper = analyticsHelper,
                onToggleClick = { desiredOn ->
                    if (!desiredOn) {
                        onNotificationToggleClick()
                        return@NotificationToggleSection
                    }

                    if (permissionState == null) {
                        onNotificationToggleClick()
                        return@NotificationToggleSection
                    }

                    when (val status = permissionState.status) {
                        PermissionStatus.Granted -> onNotificationToggleClick()
                        is PermissionStatus.Denied -> {
                            pendingNotificationEnable = true
                            if (status.shouldShowRationale) {
                                context.startActivity(
                                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                        data = Uri.fromParts("package", context.packageName, null)
                                    }
                                )
                            } else {
                                permissionState.launchPermissionRequest()
                            }
                        }
                    }
                },
            )

            AnimatedVisibility(
                visible = state.notificationEnabled,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically(),
            ) {
                NotificationDetailSection(
                    state = state,
                    analyticsHelper = analyticsHelper,
                    onTimeClick = { isShowTimeDialog = true },
                    onMessageChange = onMessageChange,
                    onResetClick = onResetClick,
                    showDivider = true,
                )
            }
        }

        if (isTreatment) {
            EbbingSolidButton(
                label = stringResource(R.string.home_save),
                onClick = {
                    analyticsHelper.logEvent(
                        AnalyticsEvent.Click(
                            screenName = "NotificationNudge",
                            buttonName = "save",
                            properties = mapOf("notification_enabled" to state.notificationEnabled),
                        )
                    )
                    onSaveClick()
                    focusManager.clearFocus()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(EbbingTheme.colors.background)
                    .padding(horizontal = 20.dp, vertical = 16.dp),
            )
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun NotificationScreenTablet(
    state: NotificationState,
    isTreatment: Boolean = false,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit,
    onNotificationToggleClick: () -> Unit,
    onAlarmTimeChange: (Int, Int) -> Unit,
    onMessageChange: (String) -> Unit,
    onResetClick: () -> Unit,
) {
    val analyticsHelper = LocalAnalyticsHelper.current
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    val permissionState = if (SDK_INT >= TIRAMISU) rememberPermissionState(POST_NOTIFICATIONS)
    else null

    var pendingNotificationEnable by remember { mutableStateOf(false) }
    var isShowTimeDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        analyticsHelper.logEvent(
            AnalyticsEvent.View(
                screenName = "NotificationNudge",
                properties = mapOf("variant" to if (isTreatment) "TREATMENT_V2" else "CONTROL_V2"),
            )
        )
    }

    LaunchedEffect(permissionState?.status) {
        if (pendingNotificationEnable && permissionState?.status == PermissionStatus.Granted) {
            onNotificationToggleClick()
            pendingNotificationEnable = false
        }
    }

    if (isShowTimeDialog) {
        AlarmTimeDialog(
            initialHour = state.alarmHour,
            initialMinute = state.alarmMinute,
            onDismissRequest = { isShowTimeDialog = false },
            onConfirmClick = { hour, minute ->
                onAlarmTimeChange(hour, minute)
                isShowTimeDialog = false
            },
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        NotificationTopBar(
            analyticsHelper = analyticsHelper,
            state = state,
            onBackClick = onBackClick,
            onSaveClick = {
                onSaveClick()
                focusManager.clearFocus()
            },
        )

        Row(
            modifier = modifier
                .fillMaxSize()
                .imePadding(),
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .padding(20.dp)
                    .padding(horizontal = 20.dp),
            ) {
                NotificationHeader()

                NotificationToggleSection(
                    checked = state.notificationEnabled,
                    analyticsHelper = analyticsHelper,
                    onToggleClick = { desiredOn ->
                        if (!desiredOn) {
                            onNotificationToggleClick()
                            return@NotificationToggleSection
                        }

                        if (permissionState == null) {
                            onNotificationToggleClick()
                            return@NotificationToggleSection
                        }

                        when (val status = permissionState.status) {
                            PermissionStatus.Granted -> onNotificationToggleClick()
                            is PermissionStatus.Denied -> {
                                pendingNotificationEnable = true
                                if (status.shouldShowRationale) {
                                    context.startActivity(
                                        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                            data = Uri.fromParts("package", context.packageName, null)
                                        }
                                    )
                                } else {
                                    permissionState.launchPermissionRequest()
                                }
                            }
                        }
                    },
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(20.dp)
                    .padding(horizontal = 20.dp),
            ) {
                AnimatedVisibility(
                    visible = state.notificationEnabled,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically(),
                ) {
                    NotificationDetailSection(
                        state = state,
                        analyticsHelper = analyticsHelper,
                        onTimeClick = { isShowTimeDialog = true },
                        onMessageChange = onMessageChange,
                        onResetClick = onResetClick,
                        showDivider = false,
                    )
                }
            }
        }
    }
}

@Composable
private fun NotificationTopBar(
    analyticsHelper: AnalyticsHelper,
    state: NotificationState,
    isTreatment: Boolean = false,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit,
) {
    EbbingSubTopBar(
        title = stringResource(R.string.home_notification_title),
        onNavigationClick = onBackClick,
        rightComponent = {
            if (!isTreatment) {
                Text(
                    text = stringResource(R.string.home_save),
                    style = EbbingTheme.typography.body16M,
                    color = EbbingTheme.colors.primaryNormal,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .throttledClickable(throttleTime = 1500L) {
                            analyticsHelper.logEvent(
                                AnalyticsEvent.Click(
                                    screenName = "NotificationNudge",
                                    buttonName = "save",
                                    properties = mapOf("notification_enabled" to state.notificationEnabled),
                                )
                            )
                            onSaveClick()
                        },
                )
            }
        },
        modifier = Modifier.padding(horizontal = 20.dp),
    )
}

@Composable
private fun NotificationHeader() {
    Text(
        text = stringResource(R.string.home_notification_nudge),
        style = EbbingTheme.typography.heading24B,
        color = EbbingTheme.colors.textOnBackground,
        modifier = Modifier.padding(horizontal = 20.dp),
    )

    Text(
        text = stringResource(R.string.home_notification_header_sub),
        style = EbbingTheme.typography.body16M,
        color = EbbingTheme.colors.textDisabled,
        modifier = Modifier
            .padding(top = 12.dp)
            .padding(horizontal = 20.dp),
    )
}

@Composable
private fun NotificationToggleSection(
    checked: Boolean,
    analyticsHelper: AnalyticsHelper,
    onToggleClick: (Boolean) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 32.dp)
            .padding(horizontal = 20.dp),
    ) {
        Text(
            text = stringResource(R.string.home_notification_receive),
            style = EbbingTheme.typography.heading18B,
            color = EbbingTheme.colors.textSub,
            modifier = Modifier.padding(end = 8.dp),
        )

        EbbingToggle(
            checked = checked,
            onCheckedChange = { desiredOn ->
                analyticsHelper.logEvent(
                    AnalyticsEvent.Click(
                        screenName = "NotificationNudge",
                        buttonName = "toggle",
                        properties = mapOf("desired_on" to desiredOn),
                    )
                )
                onToggleClick(desiredOn)
            },
        )
    }
}

@Composable
private fun NotificationDetailSection(
    state: NotificationState,
    analyticsHelper: AnalyticsHelper,
    onTimeClick: () -> Unit,
    onMessageChange: (String) -> Unit,
    onResetClick: () -> Unit,
    showDivider: Boolean = true,
) {
    Column {
        if (showDivider) {
            Spacer(
                modifier = Modifier
                    .padding(vertical = 28.dp)
                    .fillMaxWidth()
                    .height(6.dp)
                    .background(color = EbbingTheme.colors.fillTextfield)
            )
        }

        AlarmTimeRow(
            formattedTime = state.formattedAlarmTime,
            analyticsHelper = analyticsHelper,
            onTimeClick = onTimeClick,
        )

        AlarmMessageSection(
            state = state,
            analyticsHelper = analyticsHelper,
            onMessageChange = onMessageChange,
            onResetClick = onResetClick,
        )
    }
}

@Composable
private fun AlarmTimeRow(
    formattedTime: String,
    analyticsHelper: AnalyticsHelper,
    onTimeClick: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(bottom = 40.dp),
    ) {
        Text(
            text = stringResource(R.string.home_alarm_time),
            style = EbbingTheme.typography.heading18B,
            color = EbbingTheme.colors.textSub,
            modifier = Modifier.padding(end = 8.dp),
        )

        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(textDecoration = TextDecoration.Underline)) {
                    append(formattedTime)
                }
            },
            style = EbbingTheme.typography.body18M,
            color = EbbingTheme.colors.primaryNormal,
            modifier = Modifier.clickable {
                analyticsHelper.logEvent(
                    AnalyticsEvent.Click(
                        screenName = "NotificationNudge",
                        buttonName = "time",
                    )
                )
                onTimeClick()
            },
        )
    }
}

@Composable
private fun AlarmMessageSection(
    state: NotificationState,
    analyticsHelper: AnalyticsHelper,
    onMessageChange: (String) -> Unit,
    onResetClick: () -> Unit,
) {
    val errorMessage = when {
        state.placeholderCount > 1 -> stringResource(R.string.home_alarm_placeholder_error)
        !state.isValidLength -> stringResource(R.string.home_alarm_length_error)
        else -> ""
    }
    val previewSample = stringResource(R.string.home_alarm_preview_sample)
    val placeholderToken = state.placeholderToken
    val previewMessage = when {
        state.placeholderCount == 1 -> state.message.replace(placeholderToken, previewSample)
        state.placeholderCount == 0 -> state.message
        else -> ""
    }
    val lengthText = stringResource(R.string.home_alarm_length_text, state.messageLength)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
    ) {
        Text(
            text = stringResource(R.string.home_alarm_message),
            style = EbbingTheme.typography.heading18B,
            color = EbbingTheme.colors.textSub,
            modifier = Modifier.padding(bottom = 6.dp),
        )

        val placeholderToken = state.placeholderToken
        val placeholderDesc = stringResource(R.string.home_alarm_placeholder_desc)
        Text(
            text = buildAnnotatedString {
                withStyle(
                    SpanStyle(
                        color = EbbingTheme.colors.primaryNormal,
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append(placeholderToken)
                }
                append(placeholderDesc)
            },
            style = EbbingTheme.typography.body14M,
            color = EbbingTheme.colors.textDisabled,
            modifier = Modifier.padding(bottom = 16.dp),
        )

        EbbingTextInputDefault(
            value = state.message,
            onValueChange = onMessageChange,
            hint = stringResource(R.string.home_alarm_message_hint),
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
                text = errorMessage,
                style = EbbingTheme.typography.caption12R,
                color = EbbingTheme.colors.statusError,
                modifier = Modifier.weight(1f),
            )

            Text(
                text = lengthText,
                style = EbbingTheme.typography.caption12R,
                color = if (state.isValidLength) EbbingTheme.colors.textDisabled else EbbingTheme.colors.statusError,
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (state.isValidPlaceholder && previewMessage.isNotEmpty()) {
            MessagePreview(previewMessage = previewMessage)
        }

        if (state.shouldShowResetButton) {
            ResetButton(analyticsHelper = analyticsHelper, onResetClick = onResetClick)
        }
    }
}

@Composable
private fun MessagePreview(previewMessage: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = EbbingTheme.colors.fillNormal,
                shape = RoundedCornerShape(16.dp),
            )
            .padding(vertical = 12.dp, horizontal = 16.dp),
    ) {
        Text(
            text = stringResource(R.string.home_alarm_preview),
            style = EbbingTheme.typography.caption12R,
            color = EbbingTheme.colors.textDisabled,
            modifier = Modifier.padding(bottom = 4.dp),
        )
        Text(
            text = previewMessage,
            style = EbbingTheme.typography.body16M,
            color = EbbingTheme.colors.textSub,
        )
    }
}

@Composable
private fun ResetButton(
    analyticsHelper: AnalyticsHelper,
    onResetClick: () -> Unit,
) {
    Text(
        text = stringResource(R.string.home_alarm_reset),
        style = EbbingTheme.typography.heading14SB,
        color = EbbingTheme.colors.textDisabled,
        modifier = Modifier
            .clickable {
                analyticsHelper.logEvent(
                    AnalyticsEvent.Click(
                        screenName = "NotificationNudge",
                        buttonName = "reset",
                    )
                )
                onResetClick()
            }
            .padding(top = 20.dp),
    )
}
