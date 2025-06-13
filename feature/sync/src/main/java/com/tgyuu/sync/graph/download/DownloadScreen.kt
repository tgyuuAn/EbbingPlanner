package com.tgyuu.sync.graph.download

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowWidthSizeClass
import com.tgyuu.designsystem.component.EbbingSubTopBar
import com.tgyuu.sync.graph.download.contract.DownloadIntent
import com.tgyuu.sync.graph.download.contract.DownloadState

@Composable
internal fun DownloadRoute(
    viewModel: DownloadViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    DownloadScreen(
        state = state,
        onBackClick = { viewModel.onIntent(DownloadIntent.OnBackClick) },
        onDownloadClick = { viewModel.onIntent(DownloadIntent.OnDownloadClick) },
    )
}

@Composable
internal fun DownloadScreen(
    state: DownloadState,
    onBackClick: () -> Unit,
    onDownloadClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass

    if (windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.COMPACT) {
        PhoneDownloadScreen(
            state = state,
            onBackClick = onBackClick,
            onDownloadClick = onDownloadClick,
            modifier = modifier,
        )
    } else {
        TabletUploadScreen(
            state = state,
            onBackClick = onBackClick,
            onDownloadClick = onDownloadClick,
            modifier = modifier,
        )
    }
}

@Composable
private fun PhoneDownloadScreen(
    state: DownloadState,
    onBackClick: () -> Unit,
    onDownloadClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        EbbingSubTopBar(
            title = "데이터 다운로드",
            onNavigationClick = onBackClick,
            modifier = Modifier.padding(bottom = 20.dp),
        )
    }
}

@Composable
private fun TabletUploadScreen(
    state: DownloadState,
    onBackClick: () -> Unit,
    onDownloadClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        EbbingSubTopBar(
            title = "데이터 다운로드",
            onNavigationClick = onBackClick,
            modifier = Modifier.padding(bottom = 20.dp),
        )
    }
}
