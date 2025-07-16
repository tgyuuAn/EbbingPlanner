package com.tgyuu.ebbingplanner.ui

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.net.toUri
import com.tgyuu.designsystem.component.EbbingDialog
import com.tgyuu.designsystem.component.EbbingDialogBottom
import com.tgyuu.designsystem.component.EbbingDialogDefaultTop
import com.tgyuu.domain.model.UpdateInfo
import com.tgyuu.ebbingplanner.R

@Composable
internal fun HandleAppUpdate(
    softUpdateInfo: UpdateInfo?,
    hardUpdateInfo: UpdateInfo?
) {
    val context = LocalContext.current
    val isHardUpdateDialogVisible = shouldUpdate(context, hardUpdateInfo)
    var isSoftUpdateDialogVisible by remember(softUpdateInfo) {
        mutableStateOf(shouldUpdate(context, softUpdateInfo))
    }

    if (isHardUpdateDialogVisible && hardUpdateInfo != null) {
        UpdateDialog(
            updateInfo = hardUpdateInfo,
            onDismissRequest = {}, // 강제 업데이트는 닫기 불가
            onUpdateClick = {
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    "https://play.google.com/store/apps/details?id=com.tgyuu.ebbingplanner".toUri(),
                )
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            },
        )
    } else if (isSoftUpdateDialogVisible && softUpdateInfo != null) {
        UpdateDialog(
            updateInfo = softUpdateInfo,
            onDismissRequest = { isSoftUpdateDialogVisible = false },
            onUpdateClick = {
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

@Composable
private fun UpdateDialog(
    updateInfo: UpdateInfo,
    onDismissRequest: () -> Unit,
    onUpdateClick: () -> Unit,
) {
    EbbingDialog(
        onDismissRequest = onDismissRequest,
        dialogTop = {
            EbbingDialogDefaultTop(
                title = stringResource(R.string.update_title),
                subText = updateInfo.noticeMsg,
            )
        },
        dialogBottom = {
            EbbingDialogBottom(
                leftButtonText = "닫기",
                rightButtonText = "업데이트",
                onLeftButtonClick = onDismissRequest,
                onRightButtonClick = onUpdateClick,
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
    return current.zip(min).any { (cur, min) -> cur < min }
}

private fun normalizeVersion(version: String): List<Int> = version.split('.')
    .map { it.toIntOrNull() ?: 0 }
    .let { if (it.size == 2) it + 0 else it }
