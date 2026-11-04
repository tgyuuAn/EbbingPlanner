package com.tgyuu.ebbingplanner.ui

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.tgyuu.analytics.AnalyticsEvent
import com.tgyuu.analytics.LocalAnalyticsHelper
import com.tgyuu.analytics.TrackScreenViewEvent
import com.tgyuu.common.util.clickable
import com.tgyuu.designsystem.component.EbbingDialog
import com.tgyuu.designsystem.component.EbbingDialogBottom
import com.tgyuu.designsystem.component.EbbingDialogDefaultTop
import com.tgyuu.designsystem.component.EbbingSolidButton
import com.tgyuu.designsystem.foundation.EbbingTheme
import com.tgyuu.designsystem.model.UpdateInfoUiModel
import com.tgyuu.designsystem.model.UpdateStateUiModel
import com.tgyuu.designsystem.R as DesignR

@Composable
internal fun HandleAppUpdate(
    updateState: UpdateStateUiModel,
    onClickUpdateInfo: () -> Unit,
) {
    val context = LocalContext.current
    val isHardUpdate = shouldUpdate(context, updateState.hard)
    var showSoftUpdate by remember(updateState.soft) {
        mutableStateOf(shouldUpdate(context, updateState.soft))
    }

    val updateInfo = when {
        isHardUpdate && updateState.hard != null -> updateState.hard
        showSoftUpdate && updateState.soft != null -> updateState.soft
        else -> null
    }

    val playStoreIntent = remember {
        Intent(
            Intent.ACTION_VIEW,
            "https://play.google.com/store/apps/details?id=com.tgyuu.ebbingplanner".toUri()
        ).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }

    updateInfo?.let { info ->
        if (isHardUpdate) {
            HardUpdateDialog(
                updateInfo = info,
                onClickUpdateInfo = onClickUpdateInfo,
                onUpdateClick = { context.startActivity(playStoreIntent) },
            )
        } else {
            SoftUpdateDialog(
                updateInfo = info,
                onDismissRequest = { showSoftUpdate = false },
                onClickUpdateInfo = onClickUpdateInfo,
                onUpdateClick = { context.startActivity(playStoreIntent) },
            )
        }
    }
}

private const val SOFT_UPDATE_SCREEN_NAME = "SoftUpdateDialog"
private const val HARD_UPDATE_SCREEN_NAME = "HardUpdateDialog"

@Composable
private fun SoftUpdateDialog(
    updateInfo: UpdateInfoUiModel,
    onDismissRequest: () -> Unit,
    onClickUpdateInfo: () -> Unit,
    onUpdateClick: () -> Unit,
) {
    val analyticsHelper = LocalAnalyticsHelper.current
    val logProperties = mapOf("min_version" to updateInfo.minVersion)
    TrackScreenViewEvent(key = Unit, screenName = SOFT_UPDATE_SCREEN_NAME, properties = logProperties)

    EbbingDialog(
        onDismissRequest = {
            analyticsHelper.logEvent(
                AnalyticsEvent.Action(
                    screenName = SOFT_UPDATE_SCREEN_NAME,
                    actionName = "dismiss",
                    properties = logProperties,
                )
            )
            onDismissRequest()
        },
        dialogTop = {
            EbbingDialogDefaultTop(
                title = stringResource(DesignR.string.update_title),
                subText = updateInfo.localizedNoticeMsg(),
                descriptionComposable = {
                    Text(
                        text = stringResource(DesignR.string.update_view_notes),
                        color = EbbingTheme.colors.primaryDeep,
                        style = EbbingTheme.typography.body14M,
                        textDecoration = TextDecoration.Underline,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.clickable {
                            analyticsHelper.logEvent(
                                AnalyticsEvent.Click(
                                    screenName = SOFT_UPDATE_SCREEN_NAME,
                                    buttonName = "UpdateNotes",
                                    properties = logProperties,
                                )
                            )
                            onClickUpdateInfo()
                        },
                    )
                },
            )
        },
        dialogBottom = {
            EbbingDialogBottom(
                leftButtonText = stringResource(DesignR.string.update_close),
                rightButtonText = stringResource(DesignR.string.update_button),
                onLeftButtonClick = {
                    analyticsHelper.logEvent(
                        AnalyticsEvent.Click(
                            screenName = SOFT_UPDATE_SCREEN_NAME,
                            buttonName = "Close",
                            properties = logProperties,
                        )
                    )
                    onDismissRequest()
                },
                onRightButtonClick = {
                    analyticsHelper.logEvent(
                        AnalyticsEvent.Click(
                            screenName = SOFT_UPDATE_SCREEN_NAME,
                            buttonName = "Update",
                            properties = logProperties,
                        )
                    )
                    onUpdateClick()
                },
            )
        },
    )
}

@Composable
private fun HardUpdateDialog(
    updateInfo: UpdateInfoUiModel,
    onClickUpdateInfo: () -> Unit,
    onUpdateClick: () -> Unit,
) {
    val analyticsHelper = LocalAnalyticsHelper.current
    val logProperties = mapOf("min_version" to updateInfo.minVersion)
    TrackScreenViewEvent(key = Unit, screenName = HARD_UPDATE_SCREEN_NAME, properties = logProperties)

    EbbingDialog(
        onDismissRequest = {},
        dialogTop = {
            EbbingDialogDefaultTop(
                title = stringResource(DesignR.string.update_title),
                subText = updateInfo.localizedNoticeMsg(),
                descriptionComposable = {
                    Text(
                        text = stringResource(DesignR.string.update_view_notes),
                        color = EbbingTheme.colors.primaryDeep,
                        style = EbbingTheme.typography.body14M,
                        textDecoration = TextDecoration.Underline,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.clickable {
                            analyticsHelper.logEvent(
                                AnalyticsEvent.Click(
                                    screenName = HARD_UPDATE_SCREEN_NAME,
                                    buttonName = "UpdateNotes",
                                    properties = logProperties,
                                )
                            )
                            onClickUpdateInfo()
                        },
                    )
                },
            )
        },
        dialogBottom = {
            EbbingSolidButton(
                label = stringResource(DesignR.string.update_button),
                onClick = {
                    analyticsHelper.logEvent(
                        AnalyticsEvent.Click(
                            screenName = HARD_UPDATE_SCREEN_NAME,
                            buttonName = "Update",
                            properties = logProperties,
                        )
                    )
                    onUpdateClick()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = 20.dp),
            )
        },
    )
}

private fun shouldUpdate(context: Context, info: UpdateInfoUiModel?): Boolean {
    if (info == null) return false

    val currentVersion = context.packageManager.getPackageInfo(context.packageName, 0)
        .versionName ?: return false

    return checkShouldUpdate(currentVersion, info.minVersion)
}

private fun checkShouldUpdate(currentVersion: String, minVersion: String): Boolean {
    val current = normalizeVersion(currentVersion)
    val min = normalizeVersion(minVersion)
    for ((cur, m) in current.zip(min)) {
        if (cur < m) return true
        if (cur > m) return false
    }
    return false
}

private fun normalizeVersion(version: String): List<Int> = version.split('.')
    .map { it.toIntOrNull() ?: 0 }
    .let { if (it.size == 2) it + 0 else it }
