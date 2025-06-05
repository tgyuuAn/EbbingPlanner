package com.tgyuu.repeatcycle.graph.addrepeatcycle

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
import com.tgyuu.common.ui.throttledClickable
import com.tgyuu.designsystem.BasePreview
import com.tgyuu.designsystem.EbbingPreview
import com.tgyuu.designsystem.component.EbbingSubTopBar
import com.tgyuu.designsystem.foundation.EbbingTheme
import com.tgyuu.memo.graph.addmemo.AddRepeatCycleViewModel
import com.tgyuu.repeatcycle.graph.addrepeatcycle.contract.AddRepeatCycleIntent
import com.tgyuu.repeatcycle.graph.addrepeatcycle.contract.AddRepeatCycleState
import com.tgyuu.repeatcycle.ui.RepeatCycleContent
import com.tgyuu.repeatcycle.ui.RestDayContent
import java.time.DayOfWeek

@Composable
internal fun AddRepeatCycleRoute(viewModel: AddRepeatCycleViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    AddRepeatCycleScreen(
        state = state,
        onBackClick = { viewModel.onIntent(AddRepeatCycleIntent.OnBackClick) },
        onSaveClick = { viewModel.onIntent(AddRepeatCycleIntent.OnSaveClick) },
        onRepeatCycleChange = { viewModel.onIntent(AddRepeatCycleIntent.OnRepeatCycleChange(it)) },
        onRestDayChange = { viewModel.onIntent(AddRepeatCycleIntent.OnRestDayChange(it)) },
    )
}

@Composable
private fun AddRepeatCycleScreen(
    state: AddRepeatCycleState,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit,
    onRepeatCycleChange: (String) -> Unit,
    onRestDayChange: (DayOfWeek) -> Unit,
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
                    Text(
                        text = "저장",
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
                    text = "나만의 반복 주기를 추가해요.",
                    style = EbbingTheme.typography.headingLSB,
                    color = EbbingTheme.colors.black,
                )

                RepeatCycleContent(
                    repeatCycle = state.intervals,
                    preview = state.previewRepeatCycle,
                    onRepeatCycleChange = onRepeatCycleChange,
                )

                RestDayContent(
                    restDays = state.restDays,
                    onRestDayChange = onRestDayChange,
                )

                Spacer(modifier = Modifier.height(60.dp))
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
                        .padding(20.dp)
                        .padding(horizontal = 20.dp),
                ) {
                    Text(
                        text = "나만의 반복 주기를 추가해요.",
                        style = EbbingTheme.typography.headingLSB,
                        color = EbbingTheme.colors.black,
                    )

                    RepeatCycleContent(
                        repeatCycle = state.intervals,
                        preview = state.previewRepeatCycle,
                        onRepeatCycleChange = onRepeatCycleChange,
                    )
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(20.dp)
                        .padding(horizontal = 20.dp),
                ) {
                    RestDayContent(
                        restDays = state.restDays,
                        onRestDayChange = onRestDayChange,
                    )
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
            onRestDayChange = {},
        )
    }
}
