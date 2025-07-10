package com.tgyuu.setting.graph.theme

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowWidthSizeClass
import com.tgyuu.common.ui.throttledClickable
import com.tgyuu.designsystem.BasePreview
import com.tgyuu.designsystem.EbbingPreview
import com.tgyuu.designsystem.component.EbbingSubTopBar
import com.tgyuu.designsystem.foundation.EbbingTheme
import com.tgyuu.domain.model.Theme
import com.tgyuu.setting.graph.theme.contract.ThemeIntent
import com.tgyuu.setting.graph.theme.contract.ThemeState

@Composable
internal fun ThemeRoute(viewModel: ThemeViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.loadTheme()
    }

    ThemeScreen(
        state = state,
        onBackClick = { viewModel.onIntent(ThemeIntent.OnBackClick) },
        onSaveClick = { viewModel.onIntent(ThemeIntent.OnUpdateClick) },
        onThemeChange = { viewModel.onIntent(ThemeIntent.OnThemeChange(it)) },
    )
}

@Composable
private fun ThemeScreen(
    state: ThemeState,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit,
    onThemeChange: (Theme) -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    var isDialogVisible by remember { mutableStateOf(false) }

    if (windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.COMPACT) {
        PhoneThemeLayout(
            state = state,
            scrollState = scrollState,
            focusManager = focusManager,
            onBackClick = onBackClick,
            onSaveClick = onSaveClick,
            modifier = modifier
        )
    } else {
        TabletThemeLayout(
            state = state,
            focusManager = focusManager,
            onBackClick = onBackClick,
            onSaveClick = onSaveClick,
            modifier = modifier
        )
    }
}

@Composable
private fun PhoneThemeLayout(
    state: ThemeState,
    scrollState: androidx.compose.foundation.ScrollState,
    focusManager: androidx.compose.ui.focus.FocusManager,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        EbbingSubTopBar(
            title = "테마 변경",
            onNavigationClick = onBackClick,
            rightComponent = {
                Text(
                    text = "적용",
                    style = if (state.isSaveEnabled) EbbingTheme.typography.bodyMSB else EbbingTheme.typography.bodyMM,
                    color = if (state.isSaveEnabled) EbbingTheme.colors.primaryDefault else EbbingTheme.colors.dark3,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .throttledClickable(
                            throttleTime = 1500L,
                            enabled = state.isSaveEnabled
                        ) {
                            onSaveClick()
                            focusManager.clearFocus()
                        },
                )
            },
            modifier = Modifier.padding(horizontal = 20.dp),
        )

        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .padding(20.dp)
                .imePadding(),
        ) {
            Text(
                text = "어플 테마를 변경해요.",
                style = EbbingTheme.typography.headingLSB,
                color = EbbingTheme.colors.black,
            )

            ThemeContent()
            Spacer(modifier = Modifier.height(16.dp))
            PreviewContent()
            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}

@Composable
private fun TabletThemeLayout(
    state: ThemeState,
    focusManager: androidx.compose.ui.focus.FocusManager,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
    ) {
        EbbingSubTopBar(
            title = "테마 변경",
            onNavigationClick = onBackClick,
            rightComponent = {
                Text(
                    text = "적용",
                    style = if (state.isSaveEnabled) EbbingTheme.typography.bodyMSB else EbbingTheme.typography.bodyMM,
                    color = if (state.isSaveEnabled) EbbingTheme.colors.primaryDefault else EbbingTheme.colors.dark3,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .throttledClickable(
                            throttleTime = 1500L,
                            enabled = state.isSaveEnabled
                        ) {
                            onSaveClick()
                            focusManager.clearFocus()
                        },
                )
            },
        )

        Row(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(20.dp),
            ) {
                Text(
                    text = "어플 테마를 변경해요.",
                    style = EbbingTheme.typography.headingLSB,
                    color = EbbingTheme.colors.black,
                )

                ThemeContent()
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(20.dp),
            ) {
                PreviewContent()
            }
        }
    }
}

@Composable
internal fun ThemeContent() {

}

@Composable
internal fun PreviewContent() {

}

@EbbingPreview
@Composable
private fun PreviewTheme() {
    BasePreview {
        ThemeScreen(
            state = ThemeState(),
            onBackClick = {},
            onSaveClick = {},
            onThemeChange = {},
        )
    }
}
