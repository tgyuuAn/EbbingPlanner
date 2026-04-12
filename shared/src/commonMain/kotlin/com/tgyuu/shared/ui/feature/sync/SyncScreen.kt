package com.tgyuu.shared.ui.feature.sync

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.tgyuu.shared.designsystem.component.EbbingDialog
import com.tgyuu.shared.designsystem.component.EbbingDialogBottom
import com.tgyuu.shared.designsystem.component.EbbingSubTopBar
import com.tgyuu.shared.designsystem.foundation.EbbingTheme

@Composable
fun SyncScreen(
    viewModel: SyncViewModel,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsState()
    var showSyncUpDialog by remember { mutableStateOf(false) }
    var showDisconnectDialog by remember { mutableStateOf(false) }

    if (showSyncUpDialog) {
        SyncUpConfirmDialog(
            onConfirm = {
                viewModel.onIntent(SyncIntent.OnSyncUpClick)
                showSyncUpDialog = false
            },
            onDismiss = { showSyncUpDialog = false },
        )
    }

    if (showDisconnectDialog) {
        DisconnectConfirmDialog(
            onConfirm = {
                viewModel.onIntent(SyncIntent.OnDisconnectClick)
                showDisconnectDialog = false
            },
            onDismiss = { showDisconnectDialog = false },
        )
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp)
    ) {
        EbbingSubTopBar(
            title = "동기화",
            onNavigationClick = { viewModel.onIntent(SyncIntent.OnBackClick) },
            modifier = Modifier.padding(bottom = 20.dp),
        )

        if (state.linkedUuid != null) {
            LinkedUuidBody(
                linkedUuid = state.linkedUuid ?: "",
                lastSyncedAt = state.localLastSyncedAt?.toString() ?: "기록 없음",
                lastUpdatedAt = state.serverLastUpdatedAt?.toString() ?: "기록이 없거나 네트워크가 없음",
            )
        } else {
            UuidBody(
                uuid = state.uuid,
                lastSyncedAt = state.localLastSyncedAt?.toString() ?: "기록 없음",
                lastUpdatedAt = state.serverLastUpdatedAt?.toString() ?: "기록이 없거나 네트워크가 없음",
            )
        }

        SyncUpBody(
            isConnected = state.linkedUuid != null,
            isSyncUpEnabled = state.isSyncUpEnabled,
            onSyncUpClick = {
                if (state.isSyncUpEnabled) {
                    showSyncUpDialog = true
                }
            },
            onConnectClick = { viewModel.onIntent(SyncIntent.OnConnectClick) },
            onDisconnectClick = { showDisconnectDialog = true },
        )

        DescriptionBody()
    }
}

@Composable
private fun UuidBody(
    uuid: String,
    lastSyncedAt: String,
    lastUpdatedAt: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = "해당 디바이스의 고유 ID :",
            style = EbbingTheme.typography.bodySR,
            color = EbbingTheme.colors.black,
            modifier = Modifier.padding(bottom = 8.dp),
        )

        Text(
            text = uuid.ifEmpty { "생성 중..." },
            style = EbbingTheme.typography.bodySR,
            color = EbbingTheme.colors.primaryDefault,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        )

        Text(
            text = "해당 기기의 마지막 업데이트 시점 : ",
            style = EbbingTheme.typography.bodySR,
            color = EbbingTheme.colors.black,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
        )

        Text(
            text = lastSyncedAt,
            style = EbbingTheme.typography.bodySR,
            color = EbbingTheme.colors.primaryDefault,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        )

        Text(
            text = "서버에 저장된 해당 ID의 마지막 업데이트 시점 : ",
            style = EbbingTheme.typography.bodySR,
            color = EbbingTheme.colors.black,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
        )

        Text(
            text = lastUpdatedAt,
            style = EbbingTheme.typography.bodySR,
            color = EbbingTheme.colors.primaryDefault,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        )

        HorizontalDivider(
            color = EbbingTheme.colors.light2,
            thickness = 1.dp,
            modifier = Modifier.padding(vertical = 16.dp)
        )
    }
}

@Composable
private fun LinkedUuidBody(
    linkedUuid: String,
    lastSyncedAt: String,
    lastUpdatedAt: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = "연동 되어있는 ID :",
            style = EbbingTheme.typography.bodySSB,
            color = EbbingTheme.colors.black,
            modifier = Modifier.padding(bottom = 8.dp),
        )

        Text(
            text = linkedUuid,
            style = EbbingTheme.typography.bodySR,
            color = EbbingTheme.colors.primaryDefault,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        )

        Text(
            text = "해당 기기의 마지막 업데이트 시점 : ",
            style = EbbingTheme.typography.bodySR,
            color = EbbingTheme.colors.black,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
        )

        Text(
            text = lastSyncedAt,
            style = EbbingTheme.typography.bodySR,
            color = EbbingTheme.colors.primaryDefault,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        )

        Text(
            text = "서버에 저장된 해당 ID의 마지막 업데이트 시점 : ",
            style = EbbingTheme.typography.bodySR,
            color = EbbingTheme.colors.black,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
        )

        Text(
            text = lastUpdatedAt,
            style = EbbingTheme.typography.bodySR,
            color = EbbingTheme.colors.primaryDefault,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        )

        HorizontalDivider(
            color = EbbingTheme.colors.light2,
            thickness = 1.dp,
            modifier = Modifier.padding(vertical = 16.dp)
        )
    }
}

@Composable
private fun SyncUpBody(
    isConnected: Boolean,
    isSyncUpEnabled: Boolean,
    onSyncUpClick: () -> Unit,
    onConnectClick: () -> Unit,
    onDisconnectClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = "데이터 동기화 / 다른 기기와 연동",
            style = EbbingTheme.typography.bodySM,
            color = EbbingTheme.colors.dark2,
            modifier = Modifier.padding(bottom = 8.dp),
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onSyncUpClick() }
                .padding(vertical = 17.dp),
        ) {
            Text(
                text = "서버와 내 기기 동기화하기",
                style = EbbingTheme.typography.headingSSB,
                color = if (isSyncUpEnabled) EbbingTheme.colors.dark1
                else EbbingTheme.colors.dark1.copy(alpha = 0.5f),
                modifier = Modifier.weight(1f),
            )

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = EbbingTheme.colors.dark3,
            )
        }

        if (isConnected) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onDisconnectClick() }
                    .padding(vertical = 17.dp),
            ) {
                Text(
                    text = "연동 해제하기",
                    style = EbbingTheme.typography.headingSSB,
                    color = EbbingTheme.colors.dark1,
                    modifier = Modifier.weight(1f),
                )

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = EbbingTheme.colors.dark3,
                )
            }
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onConnectClick() }
                    .padding(vertical = 17.dp),
            ) {
                Text(
                    text = "다른 기기와 연동하기",
                    style = EbbingTheme.typography.headingSSB,
                    color = EbbingTheme.colors.dark1,
                    modifier = Modifier.weight(1f),
                )

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = EbbingTheme.colors.dark3,
                )
            }
        }

        HorizontalDivider(
            color = EbbingTheme.colors.light2,
            thickness = 1.dp,
            modifier = Modifier.padding(vertical = 16.dp)
        )
    }
}

@Composable
private fun DescriptionBody(
    modifier: Modifier = Modifier,
) {
    Text(
        text = buildAnnotatedString {
            append("- 동기화는 기기의 변경 사항을 서버에 반영하고, 서버의 최신 데이터를 가져오는  양방향 동기화 방식입니다.\n")
            append("- ")
            withStyle(SpanStyle(color = EbbingTheme.colors.error)) {
                append("수정한 데이터")
            }
            append("는 이 과정을 거쳐야 다른 기기와 공유됩니다.\n")
            append("- 동기화 시 서로 다른 기기에서 수정한 내용이 있는 경우 ")
            withStyle(SpanStyle(color = EbbingTheme.colors.error)) {
                append("최근 수정된 데이터로 반영")
            }
            append("됩니다.\n")
            append("- 동기화 전 반드시 중요한 데이터를 백업하거나 최신 상태를 확인해주세요.")
        },
        textAlign = TextAlign.Start,
        style = EbbingTheme.typography.bodyMM,
        color = EbbingTheme.colors.dark1,
        modifier = modifier,
    )
}

@Composable
private fun SyncUpConfirmDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    EbbingDialog(onDismissRequest = onDismiss) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 20.dp),
        ) {
            Text(
                text = "동기화",
                style = EbbingTheme.typography.headingMSB,
                color = EbbingTheme.colors.black,
                modifier = Modifier.padding(top = 40.dp),
            )

            Text(
                text = "서버와 동기화를 진행합니다.\n충돌 시 최근 데이터가 우선됩니다.",
                style = EbbingTheme.typography.bodyMM,
                color = EbbingTheme.colors.dark1,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 16.dp, bottom = 24.dp),
            )

            EbbingDialogBottom(
                leftButtonText = "취소",
                rightButtonText = "동기화",
                onLeftButtonClick = onDismiss,
                onRightButtonClick = onConfirm,
            )
        }
    }
}

@Composable
private fun DisconnectConfirmDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    EbbingDialog(onDismissRequest = onDismiss) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 20.dp),
        ) {
            Text(
                text = "연결 해제",
                style = EbbingTheme.typography.headingMSB,
                color = EbbingTheme.colors.black,
                modifier = Modifier.padding(top = 40.dp),
            )

            Text(
                text = "연결을 해제하시겠습니까?\n기존 데이터는 유지됩니다.",
                style = EbbingTheme.typography.bodyMM,
                color = EbbingTheme.colors.dark1,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 16.dp, bottom = 24.dp),
            )

            EbbingDialogBottom(
                leftButtonText = "취소",
                rightButtonText = "해제",
                onLeftButtonClick = onDismiss,
                onRightButtonClick = onConfirm,
            )
        }
    }
}
