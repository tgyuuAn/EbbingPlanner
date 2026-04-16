package com.tgyuu.repeatcycle.graph.addrepeatcycle

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowWidthSizeClass
import com.tgyuu.common.util.throttledClickable
import com.tgyuu.designsystem.BasePreview
import com.tgyuu.designsystem.EbbingPreview
import com.tgyuu.designsystem.component.EbbingSolidButton
import com.tgyuu.designsystem.component.EbbingSubTopBar
import com.tgyuu.designsystem.foundation.EbbingTheme
import com.tgyuu.memo.graph.addmemo.AddRepeatCycleViewModel
import com.tgyuu.repeatcycle.graph.addrepeatcycle.contract.AddRepeatCycleIntent
import com.tgyuu.repeatcycle.graph.addrepeatcycle.contract.AddRepeatCycleState
import com.tgyuu.repeatcycle.ui.PreviewContent
import com.tgyuu.repeatcycle.ui.RepeatCycleContent

@Composable
internal fun AddRepeatCycleRoute(viewModel: AddRepeatCycleViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    AddRepeatCycleScreen(
        state = state,
        onBackClick = { viewModel.onIntent(AddRepeatCycleIntent.OnBackClick) },
        onSaveClick = { viewModel.onIntent(AddRepeatCycleIntent.OnSaveClick) },
        onRepeatCycleChange = { viewModel.onIntent(AddRepeatCycleIntent.OnRepeatCycleChange(it)) },
    )
}

@Composable
private fun AddRepeatCycleScreen(
    state: AddRepeatCycleState,
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
                title = "반복 주기 추가",
                onNavigationClick = onBackClick,
                rightComponent = {
                    if (!state.isTreatment) {
                        Text(
                            text = "저장",
                            style = if (state.isSaveEnabled) EbbingTheme.typography.body16M else EbbingTheme.typography.body16M,
                            color = if (state.isSaveEnabled) EbbingTheme.colors.primaryNormal else EbbingTheme.colors.textDisabled,
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
                    }
                },
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
                    text = "나만의 반복 주기를 추가해요.",
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

            if (state.isTreatment) {
                EbbingSolidButton(
                    label = "저장",
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
    } else {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
        ) {
            EbbingSubTopBar(
                title = "반복 주기 추가",
                onNavigationClick = onBackClick,
                rightComponent = {
                    Text(
                        text = "저장",
                        style = if (state.isSaveEnabled) EbbingTheme.typography.body16M else EbbingTheme.typography.body16M,
                        color = if (state.isSaveEnabled) EbbingTheme.colors.primaryNormal else EbbingTheme.colors.textDisabled,
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
                        .padding(20.dp)
                        .padding(horizontal = 20.dp),
                ) {
                    Text(
                        text = "나만의 반복 주기를 추가해요.",
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
        }
    }
}

@EbbingPreview
@Composable
private fun PreviewRepeatCycle() {
    BasePreview {
        AddRepeatCycleScreen(
            state = AddRepeatCycleState(),
            onBackClick = {},
            onSaveClick = {},
            onRepeatCycleChange = {},
        )
    }
}
