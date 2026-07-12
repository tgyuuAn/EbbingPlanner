package com.tgyuu.shared.ui.feature.sync

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.tgyuu.shared.common.toFormattedString
import com.tgyuu.shared.designsystem.component.EbbingDialog
import com.tgyuu.shared.designsystem.component.EbbingDialogBottom
import com.tgyuu.shared.designsystem.component.EbbingDialogDefaultTop
import com.tgyuu.shared.designsystem.component.EbbingSubTopBar
import com.tgyuu.shared.designsystem.component.bottomsheet.EbbingBottomSheetHeader
import com.tgyuu.shared.designsystem.component.bottomsheet.EbbingModalBottomSheet
import com.tgyuu.shared.designsystem.component.bottomsheet.rememberEbbingBottomSheetState
import com.tgyuu.shared.designsystem.foundation.EbbingTheme
import com.tgyuu.shared.designsystem.foundation.LayoutConstants
import com.tgyuu.shared.platform.CameraPermissionStatus
import com.tgyuu.shared.platform.QrCodeCameraPreview
import com.tgyuu.shared.platform.getCameraPermissionStatus
import com.tgyuu.shared.platform.openAppSettings
import com.tgyuu.shared.platform.requestCameraPermission
import ebbingplanner.shared.generated.resources.Res
import ebbingplanner.shared.generated.resources.ic_arrow_right
import ebbingplanner.shared.generated.resources.ic_close
import ebbingplanner.shared.generated.resources.ic_copy
import ebbingplanner.shared.generated.resources.ic_line_scan
import ebbingplanner.shared.generated.resources.ic_qr_code
import ebbingplanner.shared.generated.resources.sync_advanced_info
import ebbingplanner.shared.generated.resources.sync_allow
import ebbingplanner.shared.generated.resources.sync_back
import ebbingplanner.shared.generated.resources.sync_camera_permission_rationale
import ebbingplanner.shared.generated.resources.sync_camera_permission_rationale_settings
import ebbingplanner.shared.generated.resources.sync_camera_permission_title
import ebbingplanner.shared.generated.resources.sync_cancel
import ebbingplanner.shared.generated.resources.sync_confirm_disconnect_desc
import ebbingplanner.shared.generated.resources.sync_confirm_disconnect_title
import ebbingplanner.shared.generated.resources.sync_confirm_sync_desc_1
import ebbingplanner.shared.generated.resources.sync_confirm_sync_desc_2
import ebbingplanner.shared.generated.resources.sync_confirm_sync_desc_3
import ebbingplanner.shared.generated.resources.sync_confirm_sync_desc_highlight_1
import ebbingplanner.shared.generated.resources.sync_confirm_sync_desc_highlight_2
import ebbingplanner.shared.generated.resources.sync_confirm_sync_title
import ebbingplanner.shared.generated.resources.sync_connected_device
import ebbingplanner.shared.generated.resources.sync_copy_device_info
import ebbingplanner.shared.generated.resources.sync_device_id_label
import ebbingplanner.shared.generated.resources.sync_disconnect
import ebbingplanner.shared.generated.resources.sync_generate_qr
import ebbingplanner.shared.generated.resources.sync_generate_qr_desc
import ebbingplanner.shared.generated.resources.sync_go_settings
import ebbingplanner.shared.generated.resources.sync_last_sync
import ebbingplanner.shared.generated.resources.sync_manual_hint
import ebbingplanner.shared.generated.resources.sync_new_device_section
import ebbingplanner.shared.generated.resources.sync_no_record
import ebbingplanner.shared.generated.resources.sync_qr_code_title
import ebbingplanner.shared.generated.resources.sync_remaining_time
import ebbingplanner.shared.generated.resources.sync_restore_title
import ebbingplanner.shared.generated.resources.sync_scan_qr
import ebbingplanner.shared.generated.resources.sync_scan_qr_desc
import ebbingplanner.shared.generated.resources.sync_scan_qr_warning
import ebbingplanner.shared.generated.resources.sync_scan_this_qr
import ebbingplanner.shared.generated.resources.sync_sync
import ebbingplanner.shared.generated.resources.sync_title
import ebbingplanner.shared.generated.resources.sync_unknown_device
import io.github.alexzhirkevich.qrose.options.QrBallShape
import io.github.alexzhirkevich.qrose.options.QrFrameShape
import io.github.alexzhirkevich.qrose.options.QrPixelShape
import io.github.alexzhirkevich.qrose.options.roundCorners
import io.github.alexzhirkevich.qrose.rememberQrCodePainter
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SyncScreen(
    viewModel: SyncViewModel,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsState()
    val clipboardManager = LocalClipboardManager.current

    var isShowSyncDialog by remember { mutableStateOf(false) }
    var isShowDisconnectDialog by remember { mutableStateOf(false) }
    var isShowPermissionDialog by remember { mutableStateOf(false) }

    if (isShowSyncDialog) {
        ConfirmSyncUpDialog(
            onDismissRequest = { isShowSyncDialog = false },
            onAcceptClick = {
                viewModel.onIntent(SyncIntent.OnSyncUpClick)
                isShowSyncDialog = false
            },
        )
    }

    if (isShowDisconnectDialog) {
        ConfirmDisconnectDialog(
            onDismissRequest = { isShowDisconnectDialog = false },
            onAcceptClick = {
                viewModel.onIntent(SyncIntent.OnDisconnectClick)
                isShowDisconnectDialog = false
            },
        )
    }

    if (isShowPermissionDialog) {
        val permissionStatus = remember { getCameraPermissionStatus() }
        CameraPermissionDialog(
            isPermanentlyDenied = permissionStatus == CameraPermissionStatus.DENIED,
            onDismissRequest = { isShowPermissionDialog = false },
            onAcceptClick = {
                if (permissionStatus == CameraPermissionStatus.DENIED) {
                    openAppSettings()
                } else {
                    requestCameraPermission { granted ->
                        if (granted) viewModel.onIntent(SyncIntent.OnScanQrClick)
                    }
                }
                isShowPermissionDialog = false
            },
        )
    }

    // 내 QR 표시 바텀시트
    val qrSheetState = rememberEbbingBottomSheetState()
    LaunchedEffect(state.isQrSheetVisible) {
        if (state.isQrSheetVisible) qrSheetState.show() else qrSheetState.hide()
    }
    EbbingModalBottomSheet(
        sheetState = qrSheetState,
        onDismissRequest = { viewModel.onIntent(SyncIntent.OnDismissQrSheet) },
    ) {
        QrCodeBottomSheetContent(
            qrContent = state.qrContent,
            formattedRemainingTime = state.formattedRemainingTimeInSec,
        )
    }

    SyncScreenContent(
        state = state,
        onBackClick = { viewModel.onIntent(SyncIntent.OnBackClick) },
        onSyncUpClick = {
            if (state.isSyncUpEnabled) isShowSyncDialog = true
            else viewModel.onIntent(SyncIntent.OnSyncUpDisabledClick)
        },
        onDisconnectClick = { isShowDisconnectDialog = true },
        onClickGenerateCode = { viewModel.onIntent(SyncIntent.OnGenerateQrClick) },
        onScanQrClick = {
            if (getCameraPermissionStatus() == CameraPermissionStatus.GRANTED) {
                viewModel.onIntent(SyncIntent.OnScanQrClick)
            } else {
                isShowPermissionDialog = true
            }
        },
        onDismissScan = { viewModel.onIntent(SyncIntent.OnDismissScan) },
        onQrDetected = { viewModel.onIntent(SyncIntent.OnQrDetected(it)) },
        onCopyDeviceInfo = {
            clipboardManager.setText(AnnotatedString(state.displayDeviceInfo))
            viewModel.onIntent(SyncIntent.OnDeviceInfoCopied)
        },
        onRestoreClick = { viewModel.onIntent(SyncIntent.OnRestoreClick) },
        modifier = modifier,
    )
}

@Composable
private fun SyncScreenContent(
    state: SyncState,
    onBackClick: () -> Unit,
    onSyncUpClick: () -> Unit,
    onDisconnectClick: () -> Unit,
    onClickGenerateCode: () -> Unit,
    onScanQrClick: () -> Unit,
    onDismissScan: () -> Unit,
    onQrDetected: (String) -> Unit,
    onCopyDeviceInfo: () -> Unit,
    onRestoreClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isWide = maxWidth > LayoutConstants.TABLET_BREAKPOINT

        Column(modifier = Modifier.fillMaxSize()) {
            EbbingSubTopBar(
                title = stringResource(Res.string.sync_title),
                onNavigationClick = if (state.isScanning) onDismissScan else onBackClick,
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 20.dp),
            )

            when {
                state.isScanning -> ScanQrBody(
                    isLoading = state.isScanLoading,
                    onQrDetected = onQrDetected,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 20.dp),
                )

                state.isInitialLoading -> Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                ) {
                    CircularProgressIndicator(color = EbbingTheme.colors.primaryDefault)
                }

                else -> Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    if (isWide) {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.weight(1f)) {
                                MainSections(
                                    state = state,
                                    onSyncUpClick = onSyncUpClick,
                                    onDisconnectClick = onDisconnectClick,
                                    onClickGenerateCode = onClickGenerateCode,
                                    onScanQrClick = onScanQrClick,
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                                    AdvancedInfoSection(
                                        state = state,
                                        onCopyClick = onCopyDeviceInfo,
                                        onRestoreClick = onRestoreClick,
                                    )
                                }
                            }
                        }
                    } else {
                        MainSections(
                            state = state,
                            onSyncUpClick = onSyncUpClick,
                            onDisconnectClick = onDisconnectClick,
                            onClickGenerateCode = onClickGenerateCode,
                            onScanQrClick = onScanQrClick,
                        )

                        SectionDivider()

                        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                            AdvancedInfoSection(
                                state = state,
                                onCopyClick = onCopyDeviceInfo,
                                onRestoreClick = onRestoreClick,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MainSections(
    state: SyncState,
    onSyncUpClick: () -> Unit,
    onDisconnectClick: () -> Unit,
    onClickGenerateCode: () -> Unit,
    onScanQrClick: () -> Unit,
) {
    if (state.linkedUuid != null) {
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            ConnectedDeviceSection(
                state = state,
                onDisconnectClick = onDisconnectClick,
            )

            Spacer(modifier = Modifier.height(26.dp))

            LastSyncSection(
                state = state,
                onSyncUpClick = onSyncUpClick,
            )
        }
    } else {
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            QrCardSection(
                state = state,
                onClickGenerateCode = onClickGenerateCode,
                onScanQrClick = onScanQrClick,
            )
        }
    }
}

@Composable
private fun QrCardSection(
    state: SyncState,
    onClickGenerateCode: () -> Unit,
    onScanQrClick: () -> Unit,
) {
    Text(
        text = stringResource(Res.string.sync_new_device_section),
        style = EbbingTheme.typography.bodySSB,
        color = EbbingTheme.colors.black,
        modifier = Modifier.padding(bottom = 16.dp),
    )

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        QrActionCard(
            label = stringResource(Res.string.sync_generate_qr),
            description = stringResource(Res.string.sync_generate_qr_desc),
            buttonLabel = stringResource(Res.string.sync_generate_qr),
            buttonIconRes = Res.drawable.ic_qr_code,
            onClick = onClickGenerateCode,
            timerText = if (!state.isGenerateButtonEnabled) state.formattedRemainingTimeInSec else null,
        )

        QrActionCard(
            label = stringResource(Res.string.sync_scan_qr),
            description = stringResource(Res.string.sync_scan_qr_desc),
            warningText = stringResource(Res.string.sync_scan_qr_warning),
            buttonLabel = stringResource(Res.string.sync_scan_qr),
            buttonIconRes = Res.drawable.ic_line_scan,
            onClick = onScanQrClick,
        )
    }
}

@Composable
private fun QrActionCard(
    label: String,
    description: String,
    buttonLabel: String,
    buttonIconRes: DrawableResource,
    onClick: () -> Unit,
    warningText: String? = null,
    timerText: String? = null,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = EbbingTheme.colors.light2,
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
                style = EbbingTheme.typography.bodySM,
                color = EbbingTheme.colors.dark2,
            )

            Text(
                text = description,
                style = EbbingTheme.typography.headingSSB,
                color = EbbingTheme.colors.black,
            )

            if (warningText != null) {
                Text(
                    text = warningText,
                    style = EbbingTheme.typography.captionR12,
                    color = EbbingTheme.colors.error,
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
                    color = EbbingTheme.colors.primaryDefault,
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
                style = EbbingTheme.typography.bodySSB,
                color = EbbingTheme.colors.primaryDefault,
            )

            if (timerText != null) {
                Spacer(modifier = Modifier.size(8.dp))

                Text(
                    text = timerText,
                    style = EbbingTheme.typography.bodySM,
                    color = EbbingTheme.colors.primaryDefault,
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
            .background(EbbingTheme.colors.light3),
    )
}

@Composable
private fun ConnectedDeviceSection(
    state: SyncState,
    onDisconnectClick: () -> Unit,
) {
    Text(
        text = stringResource(Res.string.sync_connected_device),
        style = EbbingTheme.typography.bodySM,
        color = EbbingTheme.colors.dark2,
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
                    color = EbbingTheme.colors.light3,
                    shape = RoundedCornerShape(6.dp),
                ),
        ) {
            Text(
                text = state.connectedDeviceEmoji,
                style = EbbingTheme.typography.headingSSB,
            )
        }

        Text(
            text = state.connectedDeviceName ?: stringResource(Res.string.sync_unknown_device),
            style = EbbingTheme.typography.bodySSB,
            color = EbbingTheme.colors.black,
        )

        Text(
            text = state.connectedDeviceUuidPrefix,
            style = EbbingTheme.typography.bodySM,
            color = EbbingTheme.colors.dark3,
        )

        Spacer(modifier = Modifier.weight(1f))

        Icon(
            painter = painterResource(Res.drawable.ic_close),
            contentDescription = stringResource(Res.string.sync_disconnect),
            tint = EbbingTheme.colors.dark2,
            modifier = Modifier
                .size(24.dp)
                .clickable { onDisconnectClick() },
        )
    }

    HorizontalDivider(
        thickness = 1.dp,
        color = EbbingTheme.colors.light2,
    )
}

@Composable
private fun LastSyncSection(
    state: SyncState,
    onSyncUpClick: () -> Unit,
) {
    Text(
        text = stringResource(Res.string.sync_last_sync),
        style = EbbingTheme.typography.bodySM,
        color = EbbingTheme.colors.dark2,
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
            text = state.localLastSyncedAt?.toFormattedString()
                ?: stringResource(Res.string.sync_no_record),
            style = EbbingTheme.typography.headingSSB,
            color = EbbingTheme.colors.black,
            modifier = Modifier.weight(1f),
        )

        Icon(
            painter = painterResource(Res.drawable.ic_arrow_right),
            contentDescription = null,
            tint = EbbingTheme.colors.dark3,
        )
    }

    Text(
        text = stringResource(Res.string.sync_manual_hint),
        style = EbbingTheme.typography.captionR12,
        color = EbbingTheme.colors.dark2,
        modifier = Modifier.padding(top = 8.dp),
    )
}

@Composable
private fun AdvancedInfoSection(
    state: SyncState,
    onCopyClick: () -> Unit,
    onRestoreClick: () -> Unit,
) {
    Text(
        text = stringResource(Res.string.sync_advanced_info),
        style = EbbingTheme.typography.bodySM,
        color = EbbingTheme.colors.dark2,
        modifier = Modifier.padding(bottom = 8.dp),
    )

    Text(
        text = stringResource(Res.string.sync_device_id_label),
        style = EbbingTheme.typography.bodySM,
        color = EbbingTheme.colors.dark2,
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
            style = EbbingTheme.typography.bodySR,
            color = EbbingTheme.colors.primaryDefault,
            modifier = Modifier.weight(1f),
        )

        Icon(
            painter = painterResource(Res.drawable.ic_copy),
            contentDescription = stringResource(Res.string.sync_copy_device_info),
            tint = EbbingTheme.colors.dark2,
            modifier = Modifier
                .size(20.dp)
                .clickable { onCopyClick() },
        )
    }

    // 데이터 복원 (deviceId) 진입
    if (state.linkedUuid == null) {
        HorizontalDivider(
            thickness = 1.dp,
            color = EbbingTheme.colors.light2,
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onRestoreClick() }
                .padding(vertical = 17.dp),
        ) {
            Text(
                text = stringResource(Res.string.sync_restore_title),
                style = EbbingTheme.typography.headingSSB,
                color = EbbingTheme.colors.dark1,
                modifier = Modifier.weight(1f),
            )

            Icon(
                painter = painterResource(Res.drawable.ic_arrow_right),
                contentDescription = null,
                tint = EbbingTheme.colors.dark3,
            )
        }
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
                color = EbbingTheme.colors.primaryDefault,
                modifier = Modifier.align(Alignment.Center),
            )
        }
    }
}

@Composable
private fun QrCodeBottomSheetContent(
    qrContent: String,
    formattedRemainingTime: String,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
    ) {
        EbbingBottomSheetHeader(title = stringResource(Res.string.sync_qr_code_title))

        val painter = rememberQrCodePainter(data = qrContent) {
            shapes {
                ball = QrBallShape.roundCorners(0.25f)
                darkPixel = QrPixelShape.roundCorners()
                frame = QrFrameShape.roundCorners(0.25f)
            }
        }

        Image(
            painter = painter,
            contentDescription = stringResource(Res.string.sync_qr_code_title),
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .aspectRatio(1f)
                .padding(vertical = 24.dp),
        )

        Text(
            text = stringResource(Res.string.sync_remaining_time, formattedRemainingTime),
            style = EbbingTheme.typography.bodySM,
            color = EbbingTheme.colors.primaryDefault,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 8.dp),
        )

        Text(
            text = stringResource(Res.string.sync_scan_this_qr),
            style = EbbingTheme.typography.bodySM,
            color = EbbingTheme.colors.dark3,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 24.dp),
        )
    }
}

@Composable
private fun ConfirmSyncUpDialog(
    onDismissRequest: () -> Unit,
    onAcceptClick: () -> Unit,
) {
    val desc1 = stringResource(Res.string.sync_confirm_sync_desc_1)
    val descHighlight1 = stringResource(Res.string.sync_confirm_sync_desc_highlight_1)
    val desc2 = stringResource(Res.string.sync_confirm_sync_desc_2)
    val descHighlight2 = stringResource(Res.string.sync_confirm_sync_desc_highlight_2)
    val desc3 = stringResource(Res.string.sync_confirm_sync_desc_3)
    val errorColor = EbbingTheme.colors.error

    EbbingDialog(
        dialogTop = {
            EbbingDialogDefaultTop(
                title = stringResource(Res.string.sync_confirm_sync_title),
                subText = buildAnnotatedString {
                    append(desc1)
                    withStyle(SpanStyle(color = errorColor)) { append(descHighlight1) }
                    append(desc2)
                    withStyle(SpanStyle(color = errorColor)) { append(descHighlight2) }
                    append(desc3)
                },
            )
        },
        dialogBottom = {
            EbbingDialogBottom(
                leftButtonText = stringResource(Res.string.sync_back),
                rightButtonText = stringResource(Res.string.sync_sync),
                onLeftButtonClick = onDismissRequest,
                onRightButtonClick = onAcceptClick,
            )
        },
        onDismissRequest = onDismissRequest,
    )
}

@Composable
private fun ConfirmDisconnectDialog(
    onDismissRequest: () -> Unit,
    onAcceptClick: () -> Unit,
) {
    EbbingDialog(
        dialogTop = {
            EbbingDialogDefaultTop(
                title = stringResource(Res.string.sync_confirm_disconnect_title),
                subText = stringResource(Res.string.sync_confirm_disconnect_desc),
            )
        },
        dialogBottom = {
            EbbingDialogBottom(
                leftButtonText = stringResource(Res.string.sync_back),
                rightButtonText = stringResource(Res.string.sync_disconnect),
                onLeftButtonClick = onDismissRequest,
                onRightButtonClick = onAcceptClick,
            )
        },
        onDismissRequest = onDismissRequest,
    )
}

@Composable
private fun CameraPermissionDialog(
    isPermanentlyDenied: Boolean,
    onDismissRequest: () -> Unit,
    onAcceptClick: () -> Unit,
) {
    EbbingDialog(
        dialogTop = {
            EbbingDialogDefaultTop(
                title = stringResource(Res.string.sync_camera_permission_title),
                subText = if (isPermanentlyDenied) {
                    stringResource(Res.string.sync_camera_permission_rationale_settings)
                } else {
                    stringResource(Res.string.sync_camera_permission_rationale)
                },
            )
        },
        dialogBottom = {
            EbbingDialogBottom(
                leftButtonText = stringResource(Res.string.sync_cancel),
                rightButtonText = if (isPermanentlyDenied) {
                    stringResource(Res.string.sync_go_settings)
                } else {
                    stringResource(Res.string.sync_allow)
                },
                onLeftButtonClick = onDismissRequest,
                onRightButtonClick = onAcceptClick,
            )
        },
        onDismissRequest = onDismissRequest,
    )
}
