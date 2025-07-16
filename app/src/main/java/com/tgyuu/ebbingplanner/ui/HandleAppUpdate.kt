package com.tgyuu.ebbingplanner.ui

import android.content.Context
import android.content.Intent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.core.net.toUri
import com.tgyuu.common.util.clickable
import com.tgyuu.designsystem.component.EbbingDialog
import com.tgyuu.designsystem.component.EbbingDialogBottom
import com.tgyuu.designsystem.component.EbbingDialogDefaultTop
import com.tgyuu.designsystem.foundation.EbbingTheme
import com.tgyuu.domain.model.UpdateInfo
import com.tgyuu.domain.model.UpdateState
import com.tgyuu.ebbingplanner.R

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
        UpdateDialog(
            updateInfo = info,
            onDismissRequest = { if (!isHardUpdate) showSoftUpdate = false },
            onClickUpdateInfo = onClickUpdateInfo,
            onUpdateClick = { context.startActivity(playStoreIntent) },
        )
    }
}

@Composable
private fun UpdateDialog(
    updateInfo: UpdateInfo,
    onDismissRequest: () -> Unit,
    onClickUpdateInfo: () -> Unit,
    onUpdateClick: () -> Unit,
) {
    EbbingDialog(
        onDismissRequest = onDismissRequest,
        dialogTop = {
            EbbingDialogDefaultTop(
                title = stringResource(R.string.update_title),
                subText = updateInfo.noticeMsg,
                descriptionComposable = {
                    Text(
                        text = "업데이트 내용 보러가기",
                        color = EbbingTheme.colors.primaryMiddle,
                        style = EbbingTheme.typography.bodySM,
                        textDecoration = TextDecoration.Underline,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.clickable { onClickUpdateInfo() },
                    )
                },
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
