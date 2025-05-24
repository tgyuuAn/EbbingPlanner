package com.tgyuu.memo

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowWidthSizeClass
import com.tgyuu.common.ui.EbbingVisibleAnimation
import com.tgyuu.common.ui.InputState
import com.tgyuu.common.ui.clickable
import com.tgyuu.common.ui.throttledClickable
import com.tgyuu.designsystem.BasePreview
import com.tgyuu.designsystem.EbbingPreview
import com.tgyuu.designsystem.R
import com.tgyuu.designsystem.component.EbbingCheck
import com.tgyuu.designsystem.component.EbbingSubTopBar
import com.tgyuu.designsystem.component.EbbingTextInputDefault
import com.tgyuu.designsystem.foundation.EbbingTheme
import com.tgyuu.domain.model.TodoSchedule
import com.tgyuu.memo.contract.MemoIntent
import com.tgyuu.memo.contract.MemoState

@Composable
internal fun MemoRoute(viewModel: MemoViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    MemoScreen(
        state = state,
        onBackClick = { viewModel.onIntent(MemoIntent.OnBackClick) },
        onSaveClick = { viewModel.onIntent(MemoIntent.OnSaveClick) },
        onMemoChange = { viewModel.onIntent(MemoIntent.OnMemoChange(it)) },
    )
}

@Composable
private fun MemoScreen(
    state: MemoState,
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
                title = "메모 추가",
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
                        append(" 일정에\n메모를 추가해요")
                    },
                    style = EbbingTheme.typography.headingLSB,
                    color = EbbingTheme.colors.black,
                )

                MemoContent(
                    memo = state.memo,
                    memoInputState = state.memoInputState,
                    onMemoChange = onMemoChange,
                )

                PreviewContent(
                    schedule = state.originSchedule,
                    memo = state.memo,
                )

                Spacer(modifier = Modifier.height(60.dp))
            }
        }
    }
}

@Composable
private fun MemoContent(
    memo: String,
    memoInputState: InputState,
    onMemoChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isSaveFailed = memoInputState == InputState.WARNING

    Text(
        text = "메모",
        style = EbbingTheme.typography.bodyMSB,
        color = EbbingTheme.colors.black,
        modifier = Modifier.padding(top = 32.dp),
    )

    EbbingTextInputDefault(
        value = memo,
        hint = "어떤 메모를 남겨둘까요?",
        keyboardType = KeyboardType.Text,
        onValueChange = onMemoChange,
        limit = 30,
        rightComponent = {
            if (memo.isNotEmpty()) {
                Image(
                    painter = painterResource(R.drawable.ic_delete_circle),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .size(20.dp)
                        .clickable { onMemoChange("") },
                )
            }
        },
        modifier = modifier
            .padding(top = 8.dp)
            .fillMaxWidth(),
    )

    EbbingVisibleAnimation(visible = isSaveFailed) {
        Text(
            text = "필수 항목을 입력해 주세요.",
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = EbbingTheme.typography.bodySM,
            color = EbbingTheme.colors.error,
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth(),
        )
    }
}

@Composable
private fun PreviewContent(
    schedule: TodoSchedule?,
    memo: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = "미리보기",
        style = EbbingTheme.typography.bodyMSB,
        color = EbbingTheme.colors.black,
        modifier = Modifier.padding(top = 32.dp),
    )

    if (schedule != null) {
        TodoListCard(
            todo = schedule,
            memo = memo,
            modifier = modifier
                .padding(top = 8.dp)
                .fillMaxWidth(),
        )
    }
}

@Composable
private fun TodoListCard(
    todo: TodoSchedule,
    memo: String,
    modifier: Modifier = Modifier,
) {
    val isExpandCard by remember { mutableStateOf(true) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .height(IntrinsicSize.Min)
            .padding(horizontal = 12.dp)
    ) {
        VerticalDivider(
            thickness = 8.dp,
            color = Color(todo.color),
            modifier = Modifier
                .fillMaxHeight()
                .padding(end = 8.dp),
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(EbbingTheme.colors.light3)
                    .padding(vertical = 12.dp, horizontal = 16.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.weight(1f),
                ) {
                    Text(
                        text = todo.title,
                        style = EbbingTheme.typography.bodyMSB,
                        color = EbbingTheme.colors.black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )

                    Text(
                        text = todo.name,
                        style = EbbingTheme.typography.bodyMM,
                        color = EbbingTheme.colors.dark1,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )

                    if (todo.memo.isNotEmpty()) {
                        Text(
                            text = todo.memo,
                            style = EbbingTheme.typography.bodyMM,
                            color = EbbingTheme.colors.error,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        EbbingCheck(
                            checked = todo.isDone,
                            colorValue = todo.color,
                            onCheckedChange = {},
                            modifier = Modifier.size(16.dp),
                        )

                        Text(
                            text = "우선도 : ${todo.priority}",
                            style = EbbingTheme.typography.bodySSB,
                            color = EbbingTheme.colors.dark1,
                            maxLines = 1,
                            textAlign = TextAlign.End,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }

                Image(
                    painter = painterResource(R.drawable.ic_3dots),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(EbbingTheme.colors.dark1),
                    modifier = Modifier
                        .size(20.dp)
                )
            }

            EbbingCheck(
                checked = todo.isDone,
                colorValue = todo.color,
                onCheckedChange = { },
                modifier = Modifier.size(24.dp),
            )
        }
    }
}

@EbbingPreview
@Composable
private fun PreviewMemo() {
    BasePreview {
        MemoScreen(
            state = MemoState(),
            onBackClick = {},
            onSaveClick = {},
            onMemoChange = {},
        )
    }
}
