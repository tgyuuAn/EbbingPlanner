package com.tgyuu.sync.graph.main

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowWidthSizeClass
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.tgyuu.common.event.EbbingEvent
import com.tgyuu.common.toFormattedString
import com.tgyuu.common.util.clickable
import com.tgyuu.designsystem.R
import com.tgyuu.designsystem.component.EbbingSolidButton
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
import java.time.ZonedDateTime

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
                viewModel.eventBus.sendEvent(EbbingEvent.ShowSnackBar("이미 데이터가 최신상태 입니다."))
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

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
    ) {
        EbbingSubTopBar(
            title = "동기화",
            onNavigationClick = if (state.isScanning) onDismissScan else onBackClick,
            modifier = Modifier.padding(bottom = 20.dp),
        )

        if (state.isScanning) {
            ScanQrBody(
                isLoading = state.isScanLoading,
                onQrDetected = onQrDetected,
                modifier = Modifier.weight(1f),
            )
        } else {
            val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
            if (windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.COMPACT) {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    SyncInfoBody(state = state)

                    SyncUpBody(
                        isConnected = state.linkedUuid != null,
                        isSyncUpEnabled = state.isSyncUpEnabled,
                        onSyncUpClick = {
                            if (state.isSyncUpEnabled) isShowSyncDialog = true
                            else showSyncedAlreadySnackBar()
                        },
                        onDisconnectClick = { isShowDisconnectDialog = true },
                    )

                    QrBody(
                        state = state,
                        onClickGenerateCode = onClickGenerateCode,
                        onScanQrClick = onScanQrClick,
                    )

                    DescriptionBody()
                }
            } else {
                Row(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .fillMaxWidth(),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f)
                            .padding(end = 20.dp),
                    ) {
                        SyncInfoBody(state = state)

                        SyncUpBody(
                            isConnected = state.linkedUuid != null,
                            isSyncUpEnabled = state.isSyncUpEnabled,
                            onSyncUpClick = {
                                if (state.isSyncUpEnabled) isShowSyncDialog = true
                                else showSyncedAlreadySnackBar()
                            },
                            onDisconnectClick = { isShowDisconnectDialog = true },
                        )

                        QrBody(
                            state = state,
                            onClickGenerateCode = onClickGenerateCode,
                            onScanQrClick = onScanQrClick,
                        )
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f)
                            .padding(start = 20.dp),
                    ) {
                        DescriptionBody()
                    }
                }
            }
        }
    }
}

@Composable
private fun SyncInfoBody(state: SyncMainState) {
    if (state.linkedUuid != null) {
        LinkedUuidBody(
            linkedUuid = state.linkedUuid,
            lastSyncedAt = state.localLastSyncedAt,
            lastUpdatedAt = state.serverLastUpdatedAt,
        )
    } else {
        UuidBody(
            uuid = state.uuid,
            lastSyncedAt = state.localLastSyncedAt,
            lastUpdatedAt = state.serverLastUpdatedAt,
        )
    }
}

@Composable
private fun UuidBody(
    uuid: String,
    lastSyncedAt: ZonedDateTime?,
    lastUpdatedAt: ZonedDateTime?,
) {
    Text(
        text = "해당 디바이스의 고유 ID :",
        style = EbbingTheme.typography.caption14R,
        color = EbbingTheme.colors.textOnBackground,
        modifier = Modifier.padding(bottom = 8.dp),
    )

    Text(
        text = uuid,
        style = EbbingTheme.typography.caption14R,
        color = EbbingTheme.colors.primaryNormal,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
    )

    SyncTimestamps(lastSyncedAt = lastSyncedAt, lastUpdatedAt = lastUpdatedAt)
}

@Composable
private fun LinkedUuidBody(
    linkedUuid: String,
    lastSyncedAt: ZonedDateTime?,
    lastUpdatedAt: ZonedDateTime?,
) {
    Text(
        text = "연동 되어있는 ID :",
        style = EbbingTheme.typography.heading14SB,
        color = EbbingTheme.colors.textOnBackground,
        modifier = Modifier.padding(bottom = 8.dp),
    )

    Text(
        text = linkedUuid,
        style = EbbingTheme.typography.caption14R,
        color = EbbingTheme.colors.primaryNormal,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
    )

    SyncTimestamps(lastSyncedAt = lastSyncedAt, lastUpdatedAt = lastUpdatedAt)
}

@Composable
private fun SyncTimestamps(
    lastSyncedAt: ZonedDateTime?,
    lastUpdatedAt: ZonedDateTime?,
) {
    Text(
        text = "해당 기기의 마지막 업데이트 시점 : ",
        style = EbbingTheme.typography.caption14R,
        color = EbbingTheme.colors.textOnBackground,
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
    )
    Text(
        text = lastSyncedAt?.toLocalDateTime()?.toFormattedString() ?: "기록 없음",
        style = EbbingTheme.typography.caption14R,
        color = EbbingTheme.colors.primaryNormal,
        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
    )
    Text(
        text = "서버에 저장된 해당 ID의 마지막 업데이트 시점 : ",
        style = EbbingTheme.typography.caption14R,
        color = EbbingTheme.colors.textOnBackground,
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
    )
    Text(
        text = lastUpdatedAt?.toLocalDateTime()?.toFormattedString() ?: "기록이 없거나 네트워크가 없음",
        style = EbbingTheme.typography.caption14R,
        color = EbbingTheme.colors.primaryNormal,
        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
    )
    HorizontalDivider(
        color = EbbingTheme.colors.fillTextfield,
        thickness = 1.dp,
        modifier = Modifier.padding(vertical = 16.dp),
    )
}

@Composable
private fun SyncUpBody(
    isConnected: Boolean,
    isSyncUpEnabled: Boolean,
    onSyncUpClick: () -> Unit,
    onDisconnectClick: () -> Unit,
) {
    Text(
        text = "데이터 동기화",
        style = EbbingTheme.typography.body14M,
        color = EbbingTheme.colors.textDisabled,
        modifier = Modifier.padding(bottom = 8.dp),
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 17.dp)
            .clickable { onSyncUpClick() },
    ) {
        Text(
            text = "서버와 내 기기 동기화하기",
            style = EbbingTheme.typography.heading18B,
            color = if (isSyncUpEnabled) EbbingTheme.colors.textSub
            else EbbingTheme.colors.textSub.copy(alpha = 0.5f),
            modifier = Modifier.weight(1f),
        )

        Image(
            painter = painterResource(R.drawable.ic_arrow_right),
            contentDescription = "상세 내용",
            modifier = Modifier.padding(start = 4.dp),
        )
    }

    if (isConnected) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 17.dp)
                .clickable { onDisconnectClick() },
        ) {
            Text(
                text = "연동 해제하기",
                style = EbbingTheme.typography.heading18B,
                color = EbbingTheme.colors.textSub,
                modifier = Modifier.weight(1f),
            )

            Image(
                painter = painterResource(R.drawable.ic_arrow_right),
                contentDescription = "상세 내용",
                modifier = Modifier.padding(start = 4.dp),
            )
        }
    }

    HorizontalDivider(
        color = EbbingTheme.colors.fillTextfield,
        thickness = 1.dp,
        modifier = Modifier.padding(vertical = 16.dp),
    )
}

@Composable
private fun QrBody(
    state: SyncMainState,
    onClickGenerateCode: () -> Unit,
    onScanQrClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = "다른 기기와 연동",
            style = EbbingTheme.typography.body14M,
            color = EbbingTheme.colors.textDisabled,
            modifier = Modifier.padding(bottom = 8.dp),
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 17.dp)
                .clickable { onClickGenerateCode() },
        ) {
            Text(
                text = "내 기기 QR 생성",
                style = EbbingTheme.typography.heading18B,
                color = EbbingTheme.colors.textSub,
                modifier = Modifier.weight(1f),
            )

            if (!state.isGenerateButtonEnabled) {
                Text(
                    text = state.formattedRemainingTimeInSec,
                    style = EbbingTheme.typography.body14M,
                    color = EbbingTheme.colors.primaryNormal,
                    modifier = Modifier.padding(end = 4.dp),
                )
            }

            Image(
                painter = painterResource(R.drawable.ic_arrow_right),
                contentDescription = "상세 내용",
                modifier = Modifier.padding(start = 4.dp),
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 17.dp)
                .clickable { onScanQrClick() },
        ) {
            Text(
                text = "다른 기기 QR 스캔",
                style = EbbingTheme.typography.heading18B,
                color = EbbingTheme.colors.textSub,
                modifier = Modifier.weight(1f),
            )

            Image(
                painter = painterResource(R.drawable.ic_arrow_right),
                contentDescription = "상세 내용",
                modifier = Modifier.padding(start = 4.dp),
            )
        }

        HorizontalDivider(
            color = EbbingTheme.colors.fillTextfield,
            thickness = 1.dp,
            modifier = Modifier.padding(vertical = 16.dp),
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

@Composable
private fun DescriptionBody() {
    Text(
        text = buildAnnotatedString {
            append("- 동기화는 기기의 변경 사항을 서버에 반영하고, 서버의 최신 데이터를 가져오는 양방향 동기화 방식입니다.\n")
            append("- ")
            withStyle(SpanStyle(color = EbbingTheme.colors.statusError)) {
                append("수정한 데이터")
            }
            append("는 이 과정을 거쳐야 다른 기기와 공유됩니다.\n")
            append("- 동기화 시 서로 다른 기기에서 수정한 내용이 있는 경우 ")
            withStyle(SpanStyle(color = EbbingTheme.colors.statusError)) {
                append("최근 수정된 데이터로 반영")
            }
            append("됩니다.\n")
            append("- QR 코드는 ")
            withStyle(SpanStyle(color = EbbingTheme.colors.statusError)) {
                append("10분간 유효")
            }
            append("하며, 스캔하는 기기의 ")
            withStyle(SpanStyle(color = EbbingTheme.colors.statusError)) {
                append("기존 데이터는 덮어씌워집니다.")
            }
        },
        textAlign = TextAlign.Start,
        style = EbbingTheme.typography.body16M,
        color = EbbingTheme.colors.textSub,
    )
}
