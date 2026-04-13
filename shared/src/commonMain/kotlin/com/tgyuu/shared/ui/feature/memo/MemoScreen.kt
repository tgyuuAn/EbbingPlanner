package com.tgyuu.shared.ui.feature.memo

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.tgyuu.shared.designsystem.component.EbbingCheck
import com.tgyuu.shared.designsystem.component.EbbingSubTopBar
import com.tgyuu.shared.designsystem.component.EbbingTextInputDefault
import com.tgyuu.shared.designsystem.foundation.EbbingTheme
import com.tgyuu.shared.domain.model.TodoSchedule

@Composable
fun MemoScreen(
    viewModel: MemoViewModel,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = modifier.fillMaxSize(),
    ) {
        EbbingSubTopBar(
            title = "메모 ${if (state.originSchedule?.memo.isNullOrEmpty()) "추가" else "수정"}",
            onNavigationClick = { viewModel.onIntent(MemoIntent.OnBackClick) },
            rightComponent = {
                Text(
                    text = "저장",
                    style = EbbingTheme.typography.headingSSB,
                    color = if (state.isSaveEnabled) EbbingTheme.colors.primaryDefault
                    else EbbingTheme.colors.dark3,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .clickable(enabled = state.isSaveEnabled) {
                            viewModel.onIntent(MemoIntent.OnSaveClick)
                        },
                )
            },
            modifier = Modifier.padding(horizontal = 20.dp),
        )

        HorizontalDivider(
            color = EbbingTheme.colors.light2,
            thickness = 1.dp,
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .imePadding(),
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Memo Input
            MemoContent(
                memo = state.memo,
                onMemoChange = { viewModel.onIntent(MemoIntent.OnMemoChange(it)) },
                onClearClick = { viewModel.onIntent(MemoIntent.OnMemoChange("")) },
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Preview Content
            state.originSchedule?.let { schedule ->
                PreviewContent(
                    schedule = schedule,
                    memo = state.memo,
                )
            }

            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}

@Composable
private fun MemoContent(
    memo: String,
    onMemoChange: (String) -> Unit,
    onClearClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = "메모",
            style = EbbingTheme.typography.bodyMSB,
            color = EbbingTheme.colors.black,
            modifier = Modifier.padding(bottom = 8.dp),
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            EbbingTextInputDefault(
                value = memo,
                onValueChange = onMemoChange,
                hint = "어떤 메모를 남겨둘까요?",
                modifier = Modifier.weight(1f),
            )

            if (memo.isNotEmpty()) {
                Icon(
                    imageVector = Icons.Filled.Clear,
                    contentDescription = "지우기",
                    tint = EbbingTheme.colors.dark2,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .size(20.dp)
                        .clickable { onClearClick() },
                )
            }
        }

        Text(
            text = "${memo.length}/100",
            style = EbbingTheme.typography.bodySR,
            color = EbbingTheme.colors.dark3,
            modifier = Modifier
                .align(Alignment.End)
                .padding(top = 4.dp),
        )
    }
}

@Composable
private fun PreviewContent(
    schedule: TodoSchedule,
    memo: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = "미리보기",
            style = EbbingTheme.typography.bodyMSB,
            color = EbbingTheme.colors.black,
            modifier = Modifier.padding(bottom = 8.dp),
        )

        TodoListCard(
            schedule = schedule,
            memo = memo,
        )
    }
}

@Composable
private fun TodoListCard(
    schedule: TodoSchedule,
    memo: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(EbbingTheme.colors.light3)
            .padding(16.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            VerticalDivider(
                thickness = 8.dp,
                color = Color(schedule.color),
                modifier = Modifier
                    .height(48.dp)
                    .padding(end = 8.dp),
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = schedule.title,
                    style = EbbingTheme.typography.bodyMSB,
                    color = EbbingTheme.colors.black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                Text(
                    text = schedule.name,
                    style = EbbingTheme.typography.bodyMM,
                    color = EbbingTheme.colors.dark1,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            EbbingCheck(
                checked = schedule.isDone,
                colorValue = schedule.color,
                onCheckedChange = {},
                modifier = Modifier.size(24.dp),
            )
        }

        if (memo.isNotEmpty()) {
            Row(
                modifier = Modifier.padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(EbbingTheme.colors.light1),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "M",
                        style = EbbingTheme.typography.captionM,
                        color = EbbingTheme.colors.dark2,
                    )
                }

                Text(
                    text = memo,
                    style = EbbingTheme.typography.bodySSB,
                    color = EbbingTheme.colors.dark1,
                    modifier = Modifier.padding(start = 4.dp),
                )
            }
        }
    }
}
