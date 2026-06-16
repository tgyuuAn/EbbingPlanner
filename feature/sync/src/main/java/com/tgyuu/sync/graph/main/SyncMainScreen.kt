package com.tgyuu.sync.graph.main

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.tgyuu.common.event.EbbingEvent
import com.tgyuu.common.toFormattedString
import com.tgyuu.common.util.clickable
import com.tgyuu.designsystem.R
import com.tgyuu.designsystem.component.EbbingSubTopBar
import com.tgyuu.designsystem.foundation.EbbingTheme
import com.tgyuu.sync.graph.connect.ui.QrCodeCameraPreview
import com.tgyuu.sync.graph.main.contract.SyncIntent
import com.tgyuu.sync.graph.main.contract.SyncMainState
import com.tgyuu.sync.graph.main.ui.bottomsheet.QrCodeBottomSheet
import com.tgyuu.sync.graph.main.ui.dialog.CameraPermissionDialog
import com.tgyuu.sync.graph.main.ui.dialog.ConfirmDisconnectDialog
import com.tgyuu.sync.graph.main.ui.dialog.ConfirmSyncUpDialog
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@Composable
internal fun SyncMainRoute(
    viewModel: SyncMainViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    var showPermissionDialog by remember { mutableStateOf(false) }
    val alreadyLatestMessage = stringResource(R.string.sync_already_latest)
    val clipboardManager = LocalClipboardManager.current
    val deviceInfoCopiedMessage = stringResource(R.string.sync_device_info_copied)

    LaunchedEffect(viewModel) {
        viewModel.loadInitData()
    }

    LaunchedEffect(cameraPermissionState.status) {
        if (cameraPermissionState.status.isGranted && showPermissionDialog) {
            showPermissionDialog = false
            viewModel.onIntent(SyncIntent.OnScanQrClick)
        }
    }

    if (showPermissionDialog) {
        CameraPermissionDialog(
            shouldShowRationale = cameraPermissionState.status.shouldShowRationale,
            onDismissRequest = { showPermissionDialog = false },
            onAcceptClick = {
                if (cameraPermissionState.status.shouldShowRationale) {
                    context.startActivity(
                        Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.fromParts("package", context.packageName, null),
                        )
                    )
                } else {
                    cameraPermissionState.launchPermissionRequest()
                }
                showPermissionDialog = false
            },
        )
    }

    SyncMainScreen(
        state = state,
        onBackClick = { viewModel.onIntent(SyncIntent.OnBackClick) },
        onSyncUpClick = { viewModel.onIntent(SyncIntent.OnSyncUpClick) },
        showSyncedAlreadySnackBar = {
            scope.launch {
                viewModel.eventBus.sendEvent(EbbingEvent.ShowSnackBar(alreadyLatestMessage))
            }
        },
        onDisconnectClick = { viewModel.onIntent(SyncIntent.OnDisconnectClick) },
        onSyncDialogBackClick = { viewModel.onIntent(SyncIntent.OnSyncDialogBackClick) },
        onSyncDialogSyncClick = { viewModel.onIntent(SyncIntent.OnSyncDialogSyncClick) },
        onDisconnectDialogBackClick = { viewModel.onIntent(SyncIntent.OnDisconnectDialogBackClick) },
        onDisconnectDialogDisconnectClick = { viewModel.onIntent(SyncIntent.OnDisconnectDialogDisconnectClick) },
        onClickGenerateCode = {
            val bottomSheetContent: @Composable () -> Unit = {
                QrCodeBottomSheet(
                    qrContent = state.qrContent,
                    formattedRemainingTime = state.formattedRemainingTimeInSec,
                )
            }

            if (state.connectCode.isNotEmpty()) {
                scope.launch {
                    viewModel.eventBus.sendEvent(
                        EbbingEvent.ShowBottomSheet(bottomSheetContent)
                    )
                }
            } else {
                viewModel.onIntent(SyncIntent.OnClickGenerateCode(bottomSheetContent))
            }
        },
        onScanQrClick = {
            if (cameraPermissionState.status.isGranted) {
                viewModel.onIntent(SyncIntent.OnScanQrClick)
            } else {
                showPermissionDialog = true
            }
        },
        onDismissScan = { viewModel.onIntent(SyncIntent.OnDismissScan) },
        onQrDetected = { viewModel.onIntent(SyncIntent.OnQrDetected(it)) },
        onCopyDeviceInfo = {
            clipboardManager.setText(AnnotatedString(state.displayDeviceInfo))
            scope.launch {
                viewModel.eventBus.sendEvent(EbbingEvent.ShowSnackBar(deviceInfoCopiedMessage))
            }
        },
    )
}

@Composable
internal fun SyncMainScreen(
    state: SyncMainState,
    onBackClick: () -> Unit,
    onSyncUpClick: () -> Unit,
    onDisconnectClick: () -> Unit,
    onSyncDialogBackClick: () -> Unit,
    onSyncDialogSyncClick: () -> Unit,
    onDisconnectDialogBackClick: () -> Unit,
    onDisconnectDialogDisconnectClick: () -> Unit,
    onClickGenerateCode: () -> Unit,
    onScanQrClick: () -> Unit,
    onDismissScan: () -> Unit,
    onQrDetected: (String) -> Unit,
    showSyncedAlreadySnackBar: () -> Unit,
    onCopyDeviceInfo: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var isShowSyncDialog by remember { mutableStateOf(false) }
    var isShowDisconnectDialog by remember { mutableStateOf(false) }

    if (isShowSyncDialog) {
        ConfirmSyncUpDialog(
            onDismissRequest = {
                onSyncDialogBackClick()
                isShowSyncDialog = false
            },
            onAcceptClick = {
                onSyncDialogSyncClick()
                isShowSyncDialog = false
            },
        )
    }

    if (isShowDisconnectDialog) {
        ConfirmDisconnectDialog(
            onDismissRequest = {
                onDisconnectDialogBackClick()
                isShowDisconnectDialog = false
            },
            onAcceptClick = {
                onDisconnectDialogDisconnectClick()
                isShowDisconnectDialog = false
            },
        )
    }

    BackHandler(enabled = state.isScanning) {
        onDismissScan()
    }

    Column(
        modifier = modifier.fillMaxSize(),
    ) {
        EbbingSubTopBar(
            title = stringResource(R.string.sync_title),
            onNavigationClick = if (state.isScanning) onDismissScan else onBackClick,
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .padding(bottom = 20.dp),
        )

        if (state.isScanning) {
            ScanQrBody(
                isLoading = state.isScanLoading,
                onQrDetected = onQrDetected,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 20.dp),
            )
        } else if (state.isInitialLoading) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            ) {
                CircularProgressIndicator(color = EbbingTheme.colors.primaryNormal)
            }
        } else {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                if (state.linkedUuid != null) {
                    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                        ConnectedDeviceSection(
                            state = state,
                            onDisconnectClick = { isShowDisconnectDialog = true },
                        )

                        Spacer(modifier = Modifier.height(26.dp))

                        LastSyncSection(
                            state = state,
                            onSyncUpClick = {
                                if (state.isSyncUpEnabled) isShowSyncDialog = true
                                else showSyncedAlreadySnackBar()
                            },
                        )
                    }

                    SectionDivider()
                } else {
                    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                        QrCardSection(
                            state = state,
                            onClickGenerateCode = onClickGenerateCode,
                            onScanQrClick = onScanQrClick,
                        )
                    }

                    SectionDivider()
                }

                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    AdvancedInfoSection(
                        state = state,
                        onCopyClick = onCopyDeviceInfo,
                    )
                }
            }
        }
    }
}

@Composable
private fun QrCardSection(
    state: SyncMainState,
    onClickGenerateCode: () -> Unit,
    onScanQrClick: () -> Unit,
) {
    Text(
        text = stringResource(R.string.sync_new_device_section),
        style = EbbingTheme.typography.heading14SB,
        color = EbbingTheme.colors.textOnBackground,
        modifier = Modifier.padding(bottom = 16.dp),
    )

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        QrActionCard(
            label = stringResource(R.string.sync_generate_qr),
            description = stringResource(R.string.sync_generate_qr_desc),
            buttonLabel = stringResource(R.string.sync_generate_qr),
            buttonIconRes = R.drawable.ic_qr_code,
            onClick = onClickGenerateCode,
            timerText = if (!state.isGenerateButtonEnabled) state.formattedRemainingTimeInSec else null,
        )

        QrActionCard(
            label = stringResource(R.string.sync_scan_qr),
            description = stringResource(R.string.sync_scan_qr_desc),
            warningText = stringResource(R.string.sync_scan_qr_warning),
            buttonLabel = stringResource(R.string.sync_scan_qr),
            buttonIconRes = R.drawable.ic_line_scan,
            onClick = onScanQrClick,
        )
    }
}

@Composable
private fun QrActionCard(
    label: String,
    description: String,
    buttonLabel: String,
    buttonIconRes: Int,
    onClick: () -> Unit,
    warningText: String? = null,
    timerText: String? = null,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = EbbingTheme.colors.strokeOutline,
                shape = RoundedCornerShape(8.dp),
            )
            .padding(horizontal = 20.dp, vertical = 16.dp),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 20.dp),
        ) {
            Text(
                text = label,
                style = EbbingTheme.typography.body14M,
                color = EbbingTheme.colors.textSub,
            )

            Text(
                text = description,
                style = EbbingTheme.typography.heading16SB,
                color = EbbingTheme.colors.textOnBackground,
            )

            if (warningText != null) {
                Text(
                    text = warningText,
                    style = EbbingTheme.typography.caption12R,
                    color = EbbingTheme.colors.statusError,
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = EbbingTheme.colors.strokePrimary,
                    shape = RoundedCornerShape(6.dp),
                )
                .clickable { onClick() }
                .padding(vertical = 10.dp),
        ) {
            Image(
                painter = painterResource(buttonIconRes),
                contentDescription = buttonLabel,
                modifier = Modifier.size(20.dp),
            )

            Spacer(modifier = Modifier.size(4.dp))

            Text(
                text = buttonLabel,
                style = EbbingTheme.typography.heading14B,
                color = EbbingTheme.colors.textPrimary,
            )

            if (timerText != null) {
                Spacer(modifier = Modifier.size(8.dp))

                Text(
                    text = timerText,
                    style = EbbingTheme.typography.body14M,
                    color = EbbingTheme.colors.primaryNormal,
                )
            }
        }
    }
}

@Composable
private fun SectionDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 26.dp)
            .height(6.dp)
            .background(EbbingTheme.colors.fillTextfield),
    )
}

@Composable
private fun ConnectedDeviceSection(
    state: SyncMainState,
    onDisconnectClick: () -> Unit,
) {
    Text(
        text = stringResource(R.string.sync_connected_device),
        style = EbbingTheme.typography.body14M,
        color = EbbingTheme.colors.textSub,
        modifier = Modifier.padding(bottom = 12.dp),
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(32.dp)
                .background(
                    color = EbbingTheme.colors.fillTextfield,
                    shape = RoundedCornerShape(6.dp),
                ),
        ) {
            Text(
                text = state.connectedDeviceEmoji,
                style = EbbingTheme.typography.heading16SB,
            )
        }

        Text(
            text = state.connectedDeviceName ?: stringResource(R.string.sync_unknown_device),
            style = EbbingTheme.typography.heading14SB,
            color = EbbingTheme.colors.textOnBackground,
        )

        Text(
            text = state.connectedDeviceUuidPrefix,
            style = EbbingTheme.typography.body14M,
            color = EbbingTheme.colors.textDisabled,
        )

        Spacer(modifier = Modifier.weight(1f))

        Image(
            painter = painterResource(R.drawable.ic_close),
            contentDescription = stringResource(R.string.sync_disconnect),
            colorFilter = ColorFilter.tint(EbbingTheme.colors.textSub),
            modifier = Modifier
                .size(24.dp)
                .clickable { onDisconnectClick() },
        )
    }

    HorizontalDivider(
        thickness = 1.dp,
        color = EbbingTheme.colors.strokeOutline,
    )
}

@Composable
private fun LastSyncSection(
    state: SyncMainState,
    onSyncUpClick: () -> Unit,
) {
    Text(
        text = stringResource(R.string.sync_last_sync),
        style = EbbingTheme.typography.body14M,
        color = EbbingTheme.colors.textSub,
        modifier = Modifier.padding(bottom = 8.dp),
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSyncUpClick() }
            .padding(vertical = 4.dp),
    ) {
        Text(
            text = state.localLastSyncedAt?.toLocalDateTime()?.toFormattedString()
                ?: stringResource(R.string.sync_no_record),
            style = EbbingTheme.typography.heading16SB,
            color = EbbingTheme.colors.textOnBackground,
            modifier = Modifier.weight(1f),
        )

        Image(
            painter = painterResource(R.drawable.ic_arrow_right),
            contentDescription = null,
        )
    }

    Text(
        text = stringResource(R.string.sync_manual_hint),
        style = EbbingTheme.typography.caption12R,
        color = EbbingTheme.colors.textSub,
        modifier = Modifier.padding(top = 8.dp),
    )
}

@Composable
private fun AdvancedInfoSection(
    state: SyncMainState,
    onCopyClick: () -> Unit,
) {
    Text(
        text = stringResource(R.string.sync_advanced_info),
        style = EbbingTheme.typography.body14M,
        color = EbbingTheme.colors.textSub,
        modifier = Modifier.padding(bottom = 8.dp),
    )

    Text(
        text = stringResource(R.string.sync_device_id_label),
        style = EbbingTheme.typography.body14M,
        color = EbbingTheme.colors.textSub,
        modifier = Modifier.padding(bottom = 8.dp),
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 20.dp),
    ) {
        Text(
            text = state.displayDeviceInfo,
            style = EbbingTheme.typography.caption14R,
            color = EbbingTheme.colors.primaryNormal,
            modifier = Modifier.weight(1f),
        )

        Image(
            painter = painterResource(R.drawable.ic_copy),
            contentDescription = stringResource(R.string.sync_copy_device_info),
            colorFilter = ColorFilter.tint(EbbingTheme.colors.textSub),
            modifier = Modifier
                .size(20.dp)
                .clickable { onCopyClick() },
        )
    }
}

@Composable
private fun ScanQrBody(
    isLoading: Boolean,
    onQrDetected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        QrCodeCameraPreview(
            onQrDetected = onQrDetected,
            modifier = Modifier.fillMaxSize(),
        )

        if (isLoading) {
            CircularProgressIndicator(
                color = EbbingTheme.colors.primaryNormal,
                modifier = Modifier.align(Alignment.Center),
            )
        }
    }
}
