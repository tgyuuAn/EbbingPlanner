package com.tgyuu.sync.graph.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowWidthSizeClass
import com.tgyuu.common.event.EbbingEvent
import com.tgyuu.common.toFormattedString
import com.tgyuu.common.ui.clickable
import com.tgyuu.designsystem.R
import com.tgyuu.designsystem.component.EbbingSubTopBar
import com.tgyuu.designsystem.foundation.EbbingTheme
import com.tgyuu.sync.graph.main.contract.SyncIntent
import com.tgyuu.sync.graph.main.contract.SyncMainState
import com.tgyuu.sync.graph.main.ui.dialog.ConfirmSyncUpDialog
import kotlinx.coroutines.launch
import java.time.ZonedDateTime

@Composable
internal fun SyncMainRoute(
    viewModel: SyncMainViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    LaunchedEffect(viewModel) {
        viewModel.loadInitData()
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
        onConnectClick = { viewModel.onIntent(SyncIntent.OnLinkClick) },
        onDisconnectClick = {},
    )
}

@Composable
internal fun SyncMainScreen(
    state: SyncMainState,
    onBackClick: () -> Unit,
    onSyncUpClick: () -> Unit,
    onConnectClick: () -> Unit,
    onDisconnectClick: () -> Unit,
    showSyncedAlreadySnackBar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var isShowDialog by remember { mutableStateOf(false) }

    if (isShowDialog) {
        ConfirmSyncUpDialog(
            onDismissRequest = { isShowDialog = false },
            onAcceptClick = {
                onSyncUpClick()
                isShowDialog = false
            },
        )
    }

    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    if (windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.COMPACT) {
        PhoneSyncMainScreen(
            state = state,
            onBackClick = onBackClick,
            onSyncUpClick = {
                if (state.isSyncUpEnabled) {
                    isShowDialog = true
                } else {
                    showSyncedAlreadySnackBar()
                }
            },
            onConnectClick = onConnectClick,
            onDisconnectClick = onDisconnectClick,
            modifier = modifier,
        )
    } else {
        TabletSyncMainScreen(
            state = state,
            onBackClick = onBackClick,
            onSyncUpClick = {
                if (state.isSyncUpEnabled) {
                    isShowDialog = true
                } else {
                    showSyncedAlreadySnackBar()
                }
            },
            onLinkClick = onConnectClick,
            modifier = modifier,
        )
    }
}

@Composable
private fun PhoneSyncMainScreen(
    state: SyncMainState,
    onBackClick: () -> Unit,
    onSyncUpClick: () -> Unit,
    onConnectClick: () -> Unit,
    onDisconnectClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp)
        ) {
            EbbingSubTopBar(
                title = "동기화",
                onNavigationClick = onBackClick,
                modifier = Modifier.padding(bottom = 20.dp),
            )

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

            SyncUpBody(
                isConnected = state.linkedUuid != null,
                isSyncUpEnabled = state.isSyncUpEnabled,
                onSyncUpClick = onSyncUpClick,
                onConnectClick = onConnectClick,
                onDisconnectClick = onDisconnectClick,
            )

            DescriptionBody()
        }
    }
}

@Composable
private fun TabletSyncMainScreen(
    state: SyncMainState,
    onBackClick: () -> Unit,
    onSyncUpClick: () -> Unit,
    onLinkClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            EbbingSubTopBar(
                title = "동기화",
                onNavigationClick = onBackClick,
                modifier = Modifier.padding(bottom = 20.dp),
            )
        }
    }
}

@Composable
internal fun UuidBody(
    uuid: String,
    lastSyncedAt: ZonedDateTime?,
    lastUpdatedAt: ZonedDateTime?,
) {
    Text(
        text = "해당 디바이스의 고유 ID :",
        style = EbbingTheme.typography.bodySR,
        color = EbbingTheme.colors.black,
        modifier = Modifier.padding(bottom = 8.dp),
    )

    Text(
        text = uuid,
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
        text = lastSyncedAt?.toLocalDateTime()?.toFormattedString() ?: "기록 없음",
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
        text = lastUpdatedAt?.toLocalDateTime()?.toFormattedString() ?: "기록이 없거나 네트워크가 없음",
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

@Composable
internal fun LinkedUuidBody(
    linkedUuid: String,
    lastSyncedAt: ZonedDateTime?,
    lastUpdatedAt: ZonedDateTime?,
) {
    Text(
        text = "연동 되어있는 ID :",
        style = EbbingTheme.typography.bodySR,
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
        text = lastSyncedAt?.toLocalDateTime()?.toFormattedString() ?: "기록 없음",
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
        text = lastUpdatedAt?.toLocalDateTime()?.toFormattedString() ?: "기록이 없거나 네트워크가 없음",
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

@Composable
private fun SyncUpBody(
    isConnected: Boolean,
    isSyncUpEnabled: Boolean,
    onSyncUpClick: () -> Unit,
    onConnectClick: () -> Unit,
    onDisconnectClick: () -> Unit,
) {
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
            .padding(vertical = 17.dp)
            .clickable { onSyncUpClick() },
    ) {
        Text(
            text = "서버와 내 기기 동기화하기",
            style = EbbingTheme.typography.headingSSB,
            color = if (isSyncUpEnabled) EbbingTheme.colors.dark1
            else EbbingTheme.colors.dark1.copy(alpha = 0.5f),
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
                style = EbbingTheme.typography.headingSSB,
                color = EbbingTheme.colors.dark1,
                modifier = Modifier.weight(1f),
            )

            Image(
                painter = painterResource(R.drawable.ic_arrow_right),
                contentDescription = "상세 내용",
                modifier = Modifier.padding(start = 4.dp),
            )
        }
    } else {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 17.dp)
                .clickable { onConnectClick() },
        ) {
            Text(
                text = "다른 기기와 연동하기",
                style = EbbingTheme.typography.headingSSB,
                color = EbbingTheme.colors.dark1,
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
        color = EbbingTheme.colors.light2,
        thickness = 1.dp,
        modifier = Modifier.padding(vertical = 16.dp)
    )
}

@Composable
internal fun DescriptionBody() {
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
        color = EbbingTheme.colors.dark3,
    )
}
