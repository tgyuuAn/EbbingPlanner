package com.tgyuu.shared.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.tgyuu.shared.common.appVersionName
import com.tgyuu.shared.common.util.noRippleClickable
import com.tgyuu.shared.designsystem.component.EbbingDialog
import com.tgyuu.shared.designsystem.component.EbbingDialogBottom
import com.tgyuu.shared.designsystem.component.EbbingDialogDefaultTop
import com.tgyuu.shared.designsystem.component.EbbingSolidButton
import com.tgyuu.shared.designsystem.foundation.EbbingTheme
import com.tgyuu.shared.domain.AppUpdatePolicy
import com.tgyuu.shared.domain.model.UpdateInfo
import com.tgyuu.shared.domain.model.UpdateState
import ebbingplanner.shared.generated.resources.Res
import ebbingplanner.shared.generated.resources.update_button
import ebbingplanner.shared.generated.resources.update_close
import ebbingplanner.shared.generated.resources.update_title
import ebbingplanner.shared.generated.resources.update_view_notes
import org.jetbrains.compose.resources.stringResource

/**
 * 시작 시 앱 업데이트 안내 (Android app/ui/HandleAppUpdate 포팅).
 * 하드 업데이트는 닫을 수 없고, 소프트 업데이트는 닫기로 이번 실행 동안 숨긴다.
 *
 * @param onClickUpdateInfo '업데이트 내용 보러가기' (Android: 공지사항 웹뷰)
 * @param onUpdateClick '업데이트' (iOS: App Store 열기, Android: Play 스토어)
 */
@Composable
fun HandleAppUpdate(
    updateState: UpdateState,
    onClickUpdateInfo: () -> Unit,
    onUpdateClick: () -> Unit,
    currentVersion: String = appVersionName(),
) {
    val isHardUpdate = AppUpdatePolicy.shouldShowUpdateDialog(currentVersion, updateState.hard)
    var showSoftUpdate by remember(updateState.soft) {
        mutableStateOf(AppUpdatePolicy.shouldShowUpdateDialog(currentVersion, updateState.soft))
    }

    val updateInfo = when {
        isHardUpdate && updateState.hard != null -> updateState.hard
        showSoftUpdate && updateState.soft != null -> updateState.soft
        else -> null
    }

    updateInfo?.let { info ->
        if (isHardUpdate) {
            HardUpdateDialog(
                updateInfo = info,
                onClickUpdateInfo = onClickUpdateInfo,
                onUpdateClick = onUpdateClick,
            )
        } else {
            SoftUpdateDialog(
                updateInfo = info,
                onDismissRequest = { showSoftUpdate = false },
                onClickUpdateInfo = onClickUpdateInfo,
                onUpdateClick = onUpdateClick,
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
                title = stringResource(Res.string.update_title),
                subText = updateInfo.noticeMsg,
                descriptionComposable = { UpdateNotesLink(onClickUpdateInfo) },
            )
        },
        dialogBottom = {
            EbbingDialogBottom(
                leftButtonText = stringResource(Res.string.update_close),
                rightButtonText = stringResource(Res.string.update_button),
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
                title = stringResource(Res.string.update_title),
                subText = updateInfo.noticeMsg,
                descriptionComposable = { UpdateNotesLink(onClickUpdateInfo) },
            )
        },
        dialogBottom = {
            EbbingSolidButton(
                label = stringResource(Res.string.update_button),
                onClick = onUpdateClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = 20.dp),
            )
        },
    )
}

/** Android: primaryDeep · body14M · 밑줄 */
@Composable
private fun UpdateNotesLink(onClick: () -> Unit) {
    Text(
        text = stringResource(Res.string.update_view_notes),
        color = EbbingTheme.colors.primaryDeep,
        style = EbbingTheme.typography.bodySM,
        textDecoration = TextDecoration.Underline,
        textAlign = TextAlign.Center,
        modifier = Modifier.noRippleClickable(onClick = onClick),
    )
}
