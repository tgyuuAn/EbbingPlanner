package com.tgyuu.sync.graph.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowWidthSizeClass
import com.tgyuu.common.ui.clickable
import com.tgyuu.designsystem.R
import com.tgyuu.designsystem.component.EbbingSubTopBar
import com.tgyuu.designsystem.foundation.EbbingTheme
import com.tgyuu.sync.graph.main.contract.SyncIntent
import com.tgyuu.sync.graph.main.contract.SyncMainState
import com.tgyuu.sync.ui.UuidBody

@Composable
internal fun SyncMainRoute(
    viewModel: SyncViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    SyncMainScreen(
        state = state,
        onBackClick = { viewModel.onIntent(SyncIntent.OnBackClick) },
        onUploadClick = { viewModel.onIntent(SyncIntent.OnUploadClick) },
        onDownloadClick = { viewModel.onIntent(SyncIntent.OnDownloadClick) },
        onLinkClick = { viewModel.onIntent(SyncIntent.OnLinkClick) },
    )
}

@Composable
internal fun SyncMainScreen(
    state: SyncMainState,
    onBackClick: () -> Unit,
    onUploadClick: () -> Unit,
    onDownloadClick: () -> Unit,
    onLinkClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var isShowQrDialog by remember { mutableStateOf(false) }
    if (isShowQrDialog) {

    }

    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    if (windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.COMPACT) {
        PhoneSyncMainScreen(
            state = state,
            onBackClick = onBackClick,
            onUuidClick = { isShowQrDialog = true },
            onUploadClick = onUploadClick,
            onDownloadClick = onDownloadClick,
            onLinkClick = onLinkClick,
            modifier = modifier,
        )
    } else {
        TabletSyncMainScreen(
            state = state,
            onBackClick = onBackClick,
            onUuidClick = { isShowQrDialog = true },
            onUploadClick = onUploadClick,
            onDownloadClick = onDownloadClick,
            onLinkClick = onLinkClick,
            modifier = modifier,
        )
    }
}

@Composable
private fun PhoneSyncMainScreen(
    state: SyncMainState,
    onBackClick: () -> Unit,
    onUuidClick: () -> Unit,
    onUploadClick: () -> Unit,
    onDownloadClick: () -> Unit,
    onLinkClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            EbbingSubTopBar(
                title = "동기화",
                onNavigationClick = onBackClick,
                modifier = Modifier.padding(bottom = 20.dp),
            )

            UuidBody(
                uuid = state.uuid,
                lastSyncedAt = state.localLastSyncedAt,
                lastUpdatedAt = state.serverLastUpdatedAt,
                onUuidClick = onUuidClick,
            )

            UpDownLoadBody(
                onUploadClick = onUploadClick,
                onDownloadClick = onDownloadClick,
            )

            RegisterBody(onRegisterClick = onLinkClick)
        }
    }
}

@Composable
private fun TabletSyncMainScreen(
    state: SyncMainState,
    onBackClick: () -> Unit,
    onUuidClick: () -> Unit,
    onUploadClick: () -> Unit,
    onDownloadClick: () -> Unit,
    onLinkClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
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

@Composable
private fun UpDownLoadBody(
    onUploadClick: () -> Unit,
    onDownloadClick: () -> Unit,
) {
    Text(
        text = "데이터 가져오기 / 보내기",
        style = EbbingTheme.typography.bodySM,
        color = EbbingTheme.colors.dark2,
        modifier = Modifier.padding(bottom = 8.dp),
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 17.dp)
            .clickable { onUploadClick() },
    ) {
        Text(
            text = "현재 기기 데이터를 서버에 저장",
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

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 17.dp)
            .clickable { onDownloadClick() },
    ) {
        Text(
            text = "서버 데이터를 이 기기로 불러오기",
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

    HorizontalDivider(
        color = EbbingTheme.colors.light2,
        thickness = 1.dp,
        modifier = Modifier.padding(vertical = 16.dp)
    )
}

@Composable
private fun RegisterBody(onRegisterClick: () -> Unit) {
    Text(
        text = "연동하기",
        style = EbbingTheme.typography.bodySM,
        color = EbbingTheme.colors.dark2,
        modifier = Modifier.padding(bottom = 8.dp),
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 17.dp)
            .clickable { onRegisterClick() },
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

    HorizontalDivider(
        color = EbbingTheme.colors.light2,
        thickness = 1.dp,
        modifier = Modifier.padding(vertical = 16.dp)
    )
}
