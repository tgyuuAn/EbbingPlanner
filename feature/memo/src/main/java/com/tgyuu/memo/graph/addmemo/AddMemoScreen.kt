package com.tgyuu.memo.graph.addmemo

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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowWidthSizeClass
import com.tgyuu.designsystem.BasePreview
import com.tgyuu.designsystem.EbbingPreview
import com.tgyuu.designsystem.R
import com.tgyuu.designsystem.component.EbbingSolidButton
import com.tgyuu.designsystem.component.EbbingSubTopBar
import com.tgyuu.designsystem.foundation.EbbingTheme
import com.tgyuu.memo.graph.addmemo.contract.AddMemoIntent
import com.tgyuu.memo.graph.addmemo.contract.AddMemoState
import com.tgyuu.memo.ui.MemoContent
import com.tgyuu.memo.ui.PreviewContent
import com.tgyuu.memo.ui.dialog.SaveMemoDialog

@Composable
internal fun AddMemoRoute(viewModel: AddMemoViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    if (state.showSaveDialog) {
        SaveMemoDialog(
            relatedCount = state.relatedScheduleCount,
            onDismissRequest = { viewModel.onIntent(AddMemoIntent.OnDismissSaveDialog) },
            onSaveToAllClick = { viewModel.onIntent(AddMemoIntent.OnSaveToAllRelatedClick) },
            onSaveToSingleClick = { viewModel.onIntent(AddMemoIntent.OnSaveToSingleClick) },
        )
    }

    AddMemoScreen(
        state = state,
        onBackClick = { viewModel.onIntent(AddMemoIntent.OnBackClick) },
        onSaveClick = { viewModel.onIntent(AddMemoIntent.OnSaveClick) },
        onMemoChange = { viewModel.onIntent(AddMemoIntent.OnMemoChange(it)) },
    )
}

@Composable
private fun AddMemoScreen(
    state: AddMemoState,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit,
    onMemoChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass

    if (windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.COMPACT) {
        Column(modifier = modifier.fillMaxSize().imePadding()) {
            EbbingSubTopBar(
                title = stringResource(R.string.memo_add_title),
                onNavigationClick = onBackClick,
                rightComponent = {},
                modifier = Modifier.padding(horizontal = 20.dp),
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .padding(20.dp),
            ) {
                val headlineSuffix = stringResource(R.string.memo_add_headline_suffix)
                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(textDecoration = TextDecoration.Underline)) {
                            append("${state.originSchedule?.title}")
                        }
                        append(headlineSuffix)
                    },
                    style = EbbingTheme.typography.heading24B,
                    color = EbbingTheme.colors.textOnBackground,
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

            EbbingSolidButton(
                label = stringResource(R.string.memo_add_button),
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
                title = stringResource(R.string.memo_add_title),
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
                    val headlineSuffix = stringResource(R.string.memo_add_headline_suffix)
                    Text(
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(textDecoration = TextDecoration.Underline)) {
                                append("${state.originSchedule?.title}")
                            }
                            append(headlineSuffix)
                        },
                        style = EbbingTheme.typography.heading24B,
                        color = EbbingTheme.colors.textOnBackground,
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

            EbbingSolidButton(
                label = stringResource(R.string.memo_add_button),
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
private fun PreviewMemo() {
    BasePreview {
        AddMemoScreen(
            state = AddMemoState(),
            onBackClick = {},
            onSaveClick = {},
            onMemoChange = {},
        )
    }
}
