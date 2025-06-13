package com.tgyuu.sync.graph.link

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
import com.tgyuu.sync.graph.link.contract.LinkIntent
import com.tgyuu.sync.graph.link.contract.LinkState

@Composable
internal fun LinkRoute(
    viewModel: LinkViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LinkScreen(
        state = state,
        onBackClick = { viewModel.onIntent(LinkIntent.OnBackClick) },
        onLinkClick = { viewModel.onIntent(LinkIntent.OnLinkClick) },
    )
}

@Composable
internal fun LinkScreen(
    state: LinkState,
    onBackClick: () -> Unit,
    onLinkClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass

    if (windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.COMPACT) {
        PhoneLinkScreen(
            state = state,
            onBackClick = onBackClick,
            onLinkClick = onLinkClick,
            modifier = modifier,
        )
    } else {
        TabletLinkScreen(
            state = state,
            onBackClick = onBackClick,
            onLinkClick = onLinkClick,
            modifier = modifier,
        )
    }
}

@Composable
private fun PhoneLinkScreen(
    state: LinkState,
    onBackClick: () -> Unit,
    onLinkClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        EbbingSubTopBar(
            title = "디바이스 연결",
            onNavigationClick = onBackClick,
            modifier = Modifier.padding(bottom = 20.dp),
        )
    }
}

@Composable
private fun TabletLinkScreen(
    state: LinkState,
    onBackClick: () -> Unit,
    onLinkClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        EbbingSubTopBar(
            title = "디바이스 연결",
            onNavigationClick = onBackClick,
            modifier = Modifier.padding(bottom = 20.dp),
        )
    }
}
