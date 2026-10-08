package com.tgyuu.shared.ui.feature.sync.restore

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.tgyuu.shared.designsystem.component.EbbingDialog
import com.tgyuu.shared.designsystem.component.EbbingDialogBottom
import com.tgyuu.shared.designsystem.component.EbbingDialogDefaultTop
import com.tgyuu.shared.designsystem.component.EbbingSolidButton
import com.tgyuu.shared.designsystem.component.EbbingSubTopBar
import com.tgyuu.shared.designsystem.component.EbbingTextInputDefault
import com.tgyuu.shared.designsystem.foundation.EbbingTheme
import ebbingplanner.shared.generated.resources.Res
import ebbingplanner.shared.generated.resources.sync_restore_button
import ebbingplanner.shared.generated.resources.sync_restore_confirm_cancel
import ebbingplanner.shared.generated.resources.sync_restore_confirm_desc
import ebbingplanner.shared.generated.resources.sync_restore_confirm_title
import ebbingplanner.shared.generated.resources.sync_restore_description
import ebbingplanner.shared.generated.resources.sync_restore_headline
import ebbingplanner.shared.generated.resources.sync_restore_input_hint
import ebbingplanner.shared.generated.resources.sync_restore_input_label
import ebbingplanner.shared.generated.resources.sync_restore_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun RestoreScreen(
    viewModel: RestoreViewModel,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsState()

    RestoreScreenContent(
        state = state,
        onBackClick = { viewModel.onIntent(RestoreIntent.OnBackClick) },
        onDeviceIdChange = { viewModel.onIntent(RestoreIntent.OnDeviceIdChange(it)) },
        onRestoreClick = { viewModel.onIntent(RestoreIntent.OnRestoreClick) },
        modifier = modifier,
    )
}

@Composable
private fun RestoreScreenContent(
    state: RestoreState,
    onBackClick: () -> Unit,
    onDeviceIdChange: (String) -> Unit,
    onRestoreClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current
    var isShowConfirmDialog by remember { mutableStateOf(false) }

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
                title = stringResource(Res.string.sync_restore_title),
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
                    text = stringResource(Res.string.sync_restore_headline),
                    style = EbbingTheme.typography.headingLSB,
                    color = EbbingTheme.colors.black,
                )

                Text(
                    text = stringResource(Res.string.sync_restore_description),
                    style = EbbingTheme.typography.bodySM,
                    color = EbbingTheme.colors.dark2,
                    modifier = Modifier.padding(top = 8.dp),
                )

                Text(
                    text = stringResource(Res.string.sync_restore_input_label),
                    style = EbbingTheme.typography.bodyMM,
                    color = EbbingTheme.colors.black,
                    modifier = Modifier.padding(top = 32.dp),
                )

                EbbingTextInputDefault(
                    value = state.deviceId,
                    hint = stringResource(Res.string.sync_restore_input_hint),
                    keyboardType = KeyboardType.Text,
                    onValueChange = onDeviceIdChange,
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .fillMaxWidth(),
                )
            }

            EbbingSolidButton(
                label = stringResource(Res.string.sync_restore_button),
                onClick = { isShowConfirmDialog = true },
                enabled = state.isRestoreEnabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
            )
        }

        if (state.isRestoring) {
            CircularProgressIndicator(
                color = EbbingTheme.colors.primaryDefault,
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
                title = stringResource(Res.string.sync_restore_confirm_title),
                subText = stringResource(Res.string.sync_restore_confirm_desc),
            )
        },
        dialogBottom = {
            EbbingDialogBottom(
                leftButtonText = stringResource(Res.string.sync_restore_confirm_cancel),
                rightButtonText = stringResource(Res.string.sync_restore_button),
                onLeftButtonClick = onDismissRequest,
                onRightButtonClick = onRestoreClick,
            )
        },
        onDismissRequest = onDismissRequest,
    )
}
