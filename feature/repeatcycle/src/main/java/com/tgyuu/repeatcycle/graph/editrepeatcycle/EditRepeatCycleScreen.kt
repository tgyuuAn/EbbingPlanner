package com.tgyuu.repeatcycle.graph.editrepeatcycle

import androidx.compose.foundation.background
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import androidx.window.core.layout.WindowWidthSizeClass
import com.tgyuu.designsystem.BasePreview
import com.tgyuu.designsystem.EbbingPreview
import com.tgyuu.designsystem.component.EbbingSolidButton
import com.tgyuu.designsystem.component.EbbingSubTopBar
import com.tgyuu.designsystem.foundation.EbbingTheme
import com.tgyuu.designsystem.R
import com.tgyuu.repeatcycle.graph.editrepeatcycle.contract.EditRepeatCycleIntent
import com.tgyuu.repeatcycle.graph.editrepeatcycle.contract.EditRepeatCycleState
import com.tgyuu.repeatcycle.ui.PreviewContent
import com.tgyuu.repeatcycle.ui.RepeatCycleContent

@Composable
internal fun EditRepeatCycleRoute(viewModel: EditRepeatCycleViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    EditRepeatCycleScreen(
        state = state,
        onBackClick = { viewModel.onIntent(EditRepeatCycleIntent.OnBackClick) },
        onSaveClick = { viewModel.onIntent(EditRepeatCycleIntent.OnUpdateClick) },
        onRepeatCycleChange = { viewModel.onIntent(EditRepeatCycleIntent.OnRepeatCycleChange(it)) },
    )
}

@Composable
private fun EditRepeatCycleScreen(
    state: EditRepeatCycleState,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit,
    onRepeatCycleChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass

    if (windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.COMPACT) {
        Column(modifier = modifier.fillMaxSize()) {
            EbbingSubTopBar(
                title = stringResource(R.string.repeat_edit_title),
                onNavigationClick = onBackClick,
                rightComponent = {},
                modifier = Modifier.padding(horizontal = 20.dp),
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .padding(20.dp)
                    .imePadding(),
            ) {
                Text(
                    text = stringResource(R.string.repeat_edit_headline),
                    style = EbbingTheme.typography.heading24B,
                    color = EbbingTheme.colors.textOnBackground,
                )

                RepeatCycleContent(
                    repeatCycle = state.intervals,
                    onRepeatCycleChange = onRepeatCycleChange,
                )

                PreviewContent(preview = state.previewRepeatCycle)

                Spacer(modifier = Modifier.height(60.dp))
            }

            EbbingSolidButton(
                label = stringResource(R.string.repeat_edit_button),
                onClick = {
                    onSaveClick()
                    focusManager.clearFocus()
                },
                enabled = state.isSaveEnabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(EbbingTheme.colors.background)
                    .padding(horizontal = 20.dp, vertical = 16.dp),
            )
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
        ) {
            EbbingSubTopBar(
                title = stringResource(R.string.repeat_edit_title),
                onNavigationClick = onBackClick,
                rightComponent = {},
            )

            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .imePadding()
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(20.dp)
                        .padding(horizontal = 20.dp),
                ) {
                    Text(
                        text = stringResource(R.string.repeat_edit_headline),
                        style = EbbingTheme.typography.heading24B,
                        color = EbbingTheme.colors.textOnBackground,
                    )

                    RepeatCycleContent(
                        repeatCycle = state.intervals,
                        onRepeatCycleChange = onRepeatCycleChange,
                    )
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(20.dp)
                        .padding(horizontal = 20.dp),
                ) {
                    PreviewContent(preview = state.previewRepeatCycle)
                }
            }

            EbbingSolidButton(
                label = stringResource(R.string.repeat_edit_button),
                onClick = {
                    onSaveClick()
                    focusManager.clearFocus()
                },
                enabled = state.isSaveEnabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(EbbingTheme.colors.background)
                    .padding(horizontal = 20.dp, vertical = 16.dp),
            )
        }
    }
}

@EbbingPreview
@Composable
private fun PreviewRepeatCycle() {
    BasePreview {
        EditRepeatCycleScreen(
            state = EditRepeatCycleState(),
            onBackClick = {},
            onSaveClick = {},
            onRepeatCycleChange = {},
        )
    }
}
