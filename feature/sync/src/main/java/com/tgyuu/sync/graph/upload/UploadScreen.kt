package com.tgyuu.sync.graph.upload

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
import com.tgyuu.sync.graph.upload.contract.UploadIntent
import com.tgyuu.sync.graph.upload.contract.UploadState
import com.tgyuu.sync.ui.UuidBody

@Composable
internal fun UploadRoute(
    viewModel: UploadViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    UploadScreen(
        state = state,
        onBackClick = { viewModel.onIntent(UploadIntent.OnBackClick) },
        onUuidClick = { viewModel.onIntent(UploadIntent.OnUuidClick)},
        onUploadClick = { viewModel.onIntent(UploadIntent.OnUploadClick) },
    )
}

@Composable
internal fun UploadScreen(
    state: UploadState,
    onBackClick: () -> Unit,
    onUuidClick: () -> Unit,
    onUploadClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass

    if (windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.COMPACT) {
        PhoneUploadScreen(
            state = state,
            onBackClick = onBackClick,
            onUuidClick = onUuidClick,
            onUploadClick = onUploadClick,
            modifier = modifier,
        )
    } else {
        TabletUploadScreen(
            state = state,
            onBackClick = onBackClick,
            onUploadClick = onUploadClick,
            modifier = modifier,
        )
    }
}

@Composable
private fun PhoneUploadScreen(
    state: UploadState,
    onBackClick: () -> Unit,
    onUuidClick: () -> Unit,
    onUploadClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        EbbingSubTopBar(
            title = "데이터 업로드",
            onNavigationClick = onBackClick,
            modifier = Modifier.padding(bottom = 20.dp),
        )

        UuidBody(
            uuid = state.uuid,
            onUuidClick = onUuidClick,
        )
    }
}

@Composable
private fun TabletUploadScreen(
    state: UploadState,
    onBackClick: () -> Unit,
    onUploadClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        EbbingSubTopBar(
            title = "데이터 업로드",
            onNavigationClick = onBackClick,
            modifier = Modifier.padding(bottom = 20.dp),
        )
    }
}
