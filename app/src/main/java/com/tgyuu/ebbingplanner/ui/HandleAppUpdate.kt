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
import com.tgyuu.common.util.clickable
import com.tgyuu.designsystem.component.EbbingDialog
import com.tgyuu.designsystem.component.EbbingDialogBottom
import com.tgyuu.designsystem.component.EbbingDialogDefaultTop
import com.tgyuu.designsystem.component.EbbingSolidButton
import com.tgyuu.designsystem.foundation.EbbingTheme
import com.tgyuu.domain.model.UpdateInfo
import com.tgyuu.domain.model.UpdateState
import com.tgyuu.designsystem.R as DesignR

@Composable
internal fun HandleAppUpdate(
    updateState: UpdateState,
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

@Composable
private fun SoftUpdateDialog(
    updateInfo: UpdateInfo,
    onDismissRequest: () -> Unit,
    onClickUpdateInfo: () -> Unit,
    onUpdateClick: () -> Unit,
) {
    EbbingDialog(
        onDismissRequest = onDismissRequest,
        dialogTop = {
            EbbingDialogDefaultTop(
                title = stringResource(DesignR.string.update_title),
                subText = updateInfo.noticeMsg,
                descriptionComposable = {
                    Text(
                        text = stringResource(DesignR.string.update_view_notes),
                        color = EbbingTheme.colors.primaryDeep,
                        style = EbbingTheme.typography.body14M,
                        textDecoration = TextDecoration.Underline,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.clickable { onClickUpdateInfo() },
                    )
                },
            )
        },
        dialogBottom = {
            EbbingDialogBottom(
                leftButtonText = stringResource(DesignR.string.update_close),
                rightButtonText = stringResource(DesignR.string.update_button),
                onLeftButtonClick = onDismissRequest,
                onRightButtonClick = onUpdateClick,
            )
        },
    )
}

@Composable
private fun HardUpdateDialog(
    updateInfo: UpdateInfo,
    onClickUpdateInfo: () -> Unit,
    onUpdateClick: () -> Unit,
) {
    EbbingDialog(
        onDismissRequest = {},
        dialogTop = {
            EbbingDialogDefaultTop(
                title = stringResource(DesignR.string.update_title),
                subText = updateInfo.noticeMsg,
                descriptionComposable = {
                    Text(
                        text = stringResource(DesignR.string.update_view_notes),
                        color = EbbingTheme.colors.primaryDeep,
                        style = EbbingTheme.typography.body14M,
                        textDecoration = TextDecoration.Underline,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.clickable { onClickUpdateInfo() },
                    )
                },
            )
        },
        dialogBottom = {
            EbbingSolidButton(
                label = stringResource(DesignR.string.update_button),
                onClick = onUpdateClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = 20.dp),
            )
        },
    )
}

private fun shouldUpdate(context: Context, info: UpdateInfo?): Boolean {
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
