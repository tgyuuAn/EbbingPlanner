package com.tgyuu.shared.ui.feature.memo

import com.tgyuu.shared.designsystem.foundation.LayoutConstants
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.tgyuu.shared.designsystem.component.EbbingCheck
import com.tgyuu.shared.designsystem.component.TodoListCard
import com.tgyuu.shared.ui.model.TodoScheduleUiModel
import com.tgyuu.shared.designsystem.component.EbbingDialog
import com.tgyuu.shared.designsystem.component.EbbingDialogBottom
import com.tgyuu.shared.designsystem.component.EbbingDialogDefaultTop
import com.tgyuu.shared.designsystem.component.EbbingSubTopBar
import com.tgyuu.shared.designsystem.component.EbbingTextInputDefault
import com.tgyuu.shared.designsystem.foundation.EbbingTheme
import com.tgyuu.shared.domain.model.TodoSchedule
import ebbingplanner.shared.generated.resources.Res
import ebbingplanner.shared.generated.resources.common_clear
import ebbingplanner.shared.generated.resources.memo_add_button
import ebbingplanner.shared.generated.resources.memo_add_headline_suffix
import ebbingplanner.shared.generated.resources.memo_add_title
import ebbingplanner.shared.generated.resources.memo_edit_button
import ebbingplanner.shared.generated.resources.memo_edit_headline_suffix
import ebbingplanner.shared.generated.resources.memo_edit_title
import ebbingplanner.shared.generated.resources.memo_input_hint
import ebbingplanner.shared.generated.resources.memo_label
import ebbingplanner.shared.generated.resources.memo_preview_label
import ebbingplanner.shared.generated.resources.memo_save_scope_all
import ebbingplanner.shared.generated.resources.memo_save_scope_single
import ebbingplanner.shared.generated.resources.memo_save_scope_subtext
import ebbingplanner.shared.generated.resources.memo_save_scope_title
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.painterResource
import ebbingplanner.shared.generated.resources.ic_delete_circle

@Composable
fun MemoScreen(
    viewModel: MemoViewModel,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsState()
    val addHeadlineSuffix = stringResource(Res.string.memo_add_headline_suffix)
    val editHeadlineSuffix = stringResource(Res.string.memo_edit_headline_suffix)
    val isEditMode = !state.originSchedule?.memo.isNullOrEmpty()

    if (state.showSaveDialog) {
        SaveMemoDialog(
            relatedCount = state.relatedScheduleCount,
            onDismissRequest = { viewModel.onIntent(MemoIntent.OnDismissSaveDialog) },
            onSaveToAllClick = { viewModel.onIntent(MemoIntent.OnSaveToAllRelatedClick) },
            onSaveToSingleClick = { viewModel.onIntent(MemoIntent.OnSaveToSingleClick) },
        )
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
    val isWide = maxWidth > LayoutConstants.TABLET_BREAKPOINT
    Column(modifier = Modifier.fillMaxSize()) {
        EbbingSubTopBar(
            title = if (isEditMode) stringResource(Res.string.memo_edit_title) else stringResource(Res.string.memo_add_title),
            onNavigationClick = { viewModel.onIntent(MemoIntent.OnBackClick) },
            rightComponent = {},
            modifier = Modifier.padding(horizontal = 20.dp),
        )

        if (isWide) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(20.dp)
                    .imePadding(),
                horizontalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    state.originSchedule?.let { schedule ->
                        Text(
                            text = buildAnnotatedString {
                                withStyle(SpanStyle(textDecoration = TextDecoration.Underline)) {
                                    append(schedule.title)
                                }
                                append(if (schedule.memo.isNullOrEmpty()) addHeadlineSuffix else editHeadlineSuffix)
                            },
                            style = EbbingTheme.typography.headingLSB,
                            color = EbbingTheme.colors.black,
                        )
                    }
                    MemoContent(
                        memo = state.memo,
                        onMemoChange = { viewModel.onIntent(MemoIntent.OnMemoChange(it)) },
                        onClearClick = { viewModel.onIntent(MemoIntent.OnMemoChange("")) },
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Spacer(modifier = Modifier.height(24.dp))
                    state.originSchedule?.let { schedule ->
                        PreviewContent(schedule = schedule, memo = state.memo)
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
                    .imePadding(),
            ) {
                state.originSchedule?.let { schedule ->
                    Text(
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(textDecoration = TextDecoration.Underline)) {
                                append(schedule.title)
                            }
                            append(if (schedule.memo.isNullOrEmpty()) addHeadlineSuffix else editHeadlineSuffix)
                        },
                        style = EbbingTheme.typography.headingLSB,
                        color = EbbingTheme.colors.black,
                    )
                }
                MemoContent(
                    memo = state.memo,
                    onMemoChange = { viewModel.onIntent(MemoIntent.OnMemoChange(it)) },
                    onClearClick = { viewModel.onIntent(MemoIntent.OnMemoChange("")) },
                )
                Spacer(modifier = Modifier.height(24.dp))
                state.originSchedule?.let { schedule ->
                    PreviewContent(schedule = schedule, memo = state.memo)
                }
                Spacer(modifier = Modifier.height(60.dp))
            }
        }

        com.tgyuu.shared.designsystem.component.EbbingSolidButton(
            label = if (isEditMode) stringResource(Res.string.memo_edit_button) else stringResource(Res.string.memo_add_button),
            onClick = { viewModel.onIntent(MemoIntent.OnSaveClick) },
            enabled = state.isSaveEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .background(EbbingTheme.colors.background)
                .padding(horizontal = 20.dp, vertical = 16.dp),
        )
    }
    } // BoxWithConstraints
}

@Composable
private fun SaveMemoDialog(
    relatedCount: Int,
    onDismissRequest: () -> Unit,
    onSaveToAllClick: () -> Unit,
    onSaveToSingleClick: () -> Unit,
) {
    EbbingDialog(
        dialogTop = {
            EbbingDialogDefaultTop(
                title = stringResource(Res.string.memo_save_scope_title),
                subText = stringResource(Res.string.memo_save_scope_subtext),
            )
        },
        dialogBottom = {
            EbbingDialogBottom(
                leftButtonText = stringResource(Res.string.memo_save_scope_single),
                rightButtonText = stringResource(Res.string.memo_save_scope_all, relatedCount),
                onLeftButtonClick = onSaveToSingleClick,
                onRightButtonClick = onSaveToAllClick,
            )
        },
        onDismissRequest = onDismissRequest,
    )
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
            text = stringResource(Res.string.memo_label),
            style = EbbingTheme.typography.bodyMM,
            color = EbbingTheme.colors.black,
            // Android MemoContent와 동일: 헤드라인과 간격 확보 위해 top=32
            modifier = Modifier.padding(top = 32.dp, bottom = 8.dp),
        )

        EbbingTextInputDefault(
            value = memo,
            onValueChange = onMemoChange,
            hint = stringResource(Res.string.memo_input_hint),
            limit = 100,
            rightComponent = {
                if (memo.isNotEmpty()) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_delete_circle),
                        contentDescription = stringResource(Res.string.common_clear),
                        tint = EbbingTheme.colors.dark3,
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .size(20.dp)
                            .clickable { onClearClick() },
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun PreviewContent(
    schedule: TodoSchedule,
    memo: String,
    modifier: Modifier = Modifier,
) {
    // Android MemoContent와 동일: 홈/투두 리스트와 같은 공용 TodoListCard로 미리보기
    val previewTodo = TodoScheduleUiModel(
        id = schedule.id,
        infoId = schedule.infoId,
        title = schedule.title,
        tagId = schedule.tagId,
        name = schedule.name,
        color = schedule.color,
        date = schedule.date,
        memo = memo,
        isPinned = schedule.isPinned,
        isDone = schedule.isDone,
        createdAt = schedule.createdAt,
        infoCreatedAt = schedule.infoCreatedAt,
    )

    Column(modifier = modifier) {
        Text(
            text = stringResource(Res.string.memo_preview_label),
            style = EbbingTheme.typography.bodyMM,
            color = EbbingTheme.colors.black,
            modifier = Modifier.padding(bottom = 8.dp),
        )

        TodoListCard(
            todo = previewTodo,
            todosWithSameInfo = listOf(previewTodo),
            onCheckedChange = {},
            onEditScheduleClick = {},
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
