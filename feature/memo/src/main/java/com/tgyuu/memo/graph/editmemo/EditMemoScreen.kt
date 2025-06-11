package com.tgyuu.memo.graph.editmemo

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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowWidthSizeClass
import com.tgyuu.common.ui.throttledClickable
import com.tgyuu.designsystem.BasePreview
import com.tgyuu.designsystem.EbbingPreview
import com.tgyuu.designsystem.component.EbbingSubTopBar
import com.tgyuu.designsystem.foundation.EbbingTheme
import com.tgyuu.memo.graph.editmemo.contract.EditMemoIntent
import com.tgyuu.memo.graph.editmemo.contract.EditMemoState
import com.tgyuu.memo.ui.MemoContent
import com.tgyuu.memo.ui.PreviewContent

@Composable
internal fun EditMemoRoute(viewModel: EditMemoViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    EditMemoScreen(
        state = state,
        onBackClick = { viewModel.onIntent(EditMemoIntent.OnBackClick) },
        onSaveClick = { viewModel.onIntent(EditMemoIntent.OnUpdateClick) },
        onMemoChange = { viewModel.onIntent(EditMemoIntent.OnMemoChange(it)) },
    )
}

@Composable
private fun EditMemoScreen(
    state: EditMemoState,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit,
    onMemoChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass

    if (windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.COMPACT) {
        Column(modifier = modifier.fillMaxSize()) {
            EbbingSubTopBar(
                title = "메모 수정",
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
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(textDecoration = TextDecoration.Underline)) {
                            append("${state.originSchedule?.title}")
                        }
                        append(" 일정에\n메모를 수정해요")
                    },
                    style = EbbingTheme.typography.headingLSB,
                    color = EbbingTheme.colors.black,
                )

                MemoContent(
                    memo = state.memo,
                    onMemoChange = onMemoChange,
                )

                PreviewContent(
                    schedule = state.originSchedule,
                    memo = state.memo,
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
                title = "메모 수정",
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
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(textDecoration = TextDecoration.Underline)) {
                                append("${state.originSchedule?.title}")
                            }
                            append(" 일정에\n메모를 수정해요")
                        },
                        style = EbbingTheme.typography.headingLSB,
                        color = EbbingTheme.colors.black,
                    )

                    MemoContent(
                        memo = state.memo,
                        onMemoChange = onMemoChange,
                    )
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(20.dp)
                        .padding(horizontal = 20.dp),
                ) {
                    PreviewContent(
                        schedule = state.originSchedule,
                        memo = state.memo,
                    )
                }
            }
        }
    }
}

@EbbingPreview
@Composable
private fun PreviewMemo() {
    BasePreview {
        EditMemoScreen(
            state = EditMemoState(),
            onBackClick = {},
            onSaveClick = {},
            onMemoChange = {},
        )
    }
}
