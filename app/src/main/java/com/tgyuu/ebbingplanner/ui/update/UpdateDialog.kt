package com.tgyuu.ebbingplanner.ui.update

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
internal fun UpdateDialog(updateInfo: UpdateInfo?) {
    val context = LocalContext.current
    var isDialogVisible by remember { mutableStateOf(false) }

    LaunchedEffect(updateInfo) {
        isDialogVisible = isShowForceUpdateDialog(context, updateInfo)
    }

    if (isDialogVisible) {
        EbbingDialog(
            onDismissRequest = { isDialogVisible = false },
            dialogTop = {
                EbbingDialogDefaultTop(
                    title = stringResource(R.string.update_title),
                    subText = updateInfo!!.noticeMsg,
                )
            },
            dialogBottom = {
                EbbingDialogBottom(
                    leftButtonText = "닫기",
                    rightButtonText = "업데이트",
                    onLeftButtonClick = { isDialogVisible = false },
                    onRightButtonClick = {
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            "https://play.google.com/store/apps/details?id=com.tgyuu.ebbingplanner".toUri(),
                        )

                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(intent)
                    }
                )
            },
        )
    }
}

private fun isShowForceUpdateDialog(
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
