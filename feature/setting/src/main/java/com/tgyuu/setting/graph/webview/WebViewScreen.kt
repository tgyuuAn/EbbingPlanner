package com.puzzle.setting.graph.webview

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tgyuu.common.ui.EbbingWebView
import com.tgyuu.designsystem.component.EbbingSubTopBar
import com.tgyuu.navigation.NavigationEvent
import kotlinx.coroutines.launch

@Composable
internal fun WebViewRoute(
    title: String,
    url: String,
    viewModel: WebViewViewModel = hiltViewModel(),
) {
    val scope = rememberCoroutineScope()

    WebViewScreen(
        title = title,
        url = url,
        onBackClick = {
            scope.launch { viewModel.navigationBus.navigate(NavigationEvent.Up) }
        },
    )
}

@Composable
private fun WebViewScreen(
    title: String,
    url: String,
    onBackClick: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        EbbingSubTopBar(
            title = title,
            onNavigationClick = onBackClick,
            rightComponent = {},
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
        )

        EbbingWebView(
            url = url,
            modifier = Modifier
                .weight(1f)
                .imePadding(),
        )
    }
}
