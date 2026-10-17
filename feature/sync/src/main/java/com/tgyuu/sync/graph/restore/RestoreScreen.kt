package com.tgyuu.sync.graph.restore

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tgyuu.designsystem.BasePreview
import com.tgyuu.designsystem.EbbingPreview
import com.tgyuu.designsystem.R
import com.tgyuu.designsystem.component.EbbingDialog
import com.tgyuu.designsystem.component.EbbingDialogBottom
import com.tgyuu.designsystem.component.EbbingDialogDefaultTop
import com.tgyuu.designsystem.component.EbbingSolidButton
import com.tgyuu.designsystem.component.EbbingSubTopBar
import com.tgyuu.designsystem.component.EbbingTextInputDefault
import com.tgyuu.designsystem.foundation.EbbingTheme
import com.tgyuu.sync.graph.restore.contract.RestoreIntent
import com.tgyuu.sync.graph.restore.contract.RestoreState

@Composable
internal fun RestoreByDeviceIdRoute(
    viewModel: RestoreViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    RestoreScreen(
        state = state,
        onBackClick = { viewModel.onIntent(RestoreIntent.OnBackClick) },
        onDeviceIdChange = { viewModel.onIntent(RestoreIntent.OnDeviceIdChange(it)) },
        onRestoreClick = { viewModel.onIntent(RestoreIntent.OnRestoreClick) },
    )
}

@Composable
private fun RestoreScreen(
    state: RestoreState,
    onBackClick: () -> Unit,
    onDeviceIdChange: (String) -> Unit,
    onRestoreClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current
    var isShowConfirmDialog by remember { mutableStateOf(false) }

    BackHandler(enabled = state.isRestoring) {}

    if (isShowConfirmDialog) {
        ConfirmRestoreDialog(
            onDismissRequest = { isShowConfirmDialog = false },
            onRestoreClick = {
                isShowConfirmDialog = false
                onRestoreClick()
                focusManager.clearFocus()
            },
        )
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding(),
        ) {
            EbbingSubTopBar(
                title = stringResource(R.string.sync_restore_title),
                onNavigationClick = { if (!state.isRestoring) onBackClick() },
                rightComponent = {},
                modifier = Modifier.padding(horizontal = 20.dp),
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(20.dp),
            ) {
                Text(
                    text = stringResource(R.string.sync_restore_headline),
                    style = EbbingTheme.typography.heading24B,
                    color = EbbingTheme.colors.textOnBackground,
                )

                Text(
                    text = stringResource(R.string.sync_restore_description),
                    style = EbbingTheme.typography.body14M,
                    color = EbbingTheme.colors.textSub,
                    modifier = Modifier.padding(top = 8.dp),
                )

                Text(
                    text = stringResource(R.string.sync_restore_input_label),
                    style = EbbingTheme.typography.body16M,
                    color = EbbingTheme.colors.textOnBackground,
                    modifier = Modifier.padding(top = 32.dp),
                )

                EbbingTextInputDefault(
                    value = state.deviceId,
                    hint = stringResource(R.string.sync_restore_input_hint),
                    keyboardType = KeyboardType.Text,
                    onValueChange = onDeviceIdChange,
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .fillMaxWidth(),
                )
            }

            EbbingSolidButton(
                label = stringResource(R.string.sync_restore_button),
                onClick = { isShowConfirmDialog = true },
                enabled = state.isRestoreEnabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
            )
        }

        if (state.isRestoring) {
            CircularProgressIndicator(
                color = EbbingTheme.colors.primaryNormal,
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(40.dp),
            )
        }
    }
}

@Composable
private fun ConfirmRestoreDialog(
    onDismissRequest: () -> Unit,
    onRestoreClick: () -> Unit,
) {
    EbbingDialog(
        dialogTop = {
            EbbingDialogDefaultTop(
                title = stringResource(R.string.sync_restore_confirm_title),
                subText = stringResource(R.string.sync_restore_confirm_desc),
            )
        },
        dialogBottom = {
            EbbingDialogBottom(
                leftButtonText = stringResource(R.string.sync_restore_confirm_cancel),
                rightButtonText = stringResource(R.string.sync_restore_button),
                onLeftButtonClick = onDismissRequest,
                onRightButtonClick = onRestoreClick,
            )
        },
        onDismissRequest = onDismissRequest,
    )
}

@EbbingPreview
@Composable
private fun PreviewRestoreScreen() {
    BasePreview {
        RestoreScreen(
            state = RestoreState(deviceId = "3f2a8b1c"),
            onBackClick = {},
            onDeviceIdChange = {},
            onRestoreClick = {},
        )
    }
}
