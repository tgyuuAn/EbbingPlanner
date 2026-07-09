package com.tgyuu.memo.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.tgyuu.common.util.EbbingVisibleAnimation
import com.tgyuu.common.util.clickable
import com.tgyuu.designsystem.component.EbbingCheck
import com.tgyuu.designsystem.component.EbbingTextInputDefault
import com.tgyuu.designsystem.component.TodoListCard
import com.tgyuu.designsystem.model.ClickableText
import com.tgyuu.designsystem.model.TodoScheduleUiModel
import com.tgyuu.designsystem.R
import com.tgyuu.designsystem.foundation.EbbingTheme
import com.tgyuu.domain.model.TodoSchedule

@Composable
internal fun MemoContent(
    memo: String,
    onMemoChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Text(
        text = stringResource(R.string.memo_label),
        style = EbbingTheme.typography.body16M,
        color = EbbingTheme.colors.textOnBackground,
        modifier = Modifier.padding(top = 32.dp),
    )

    EbbingTextInputDefault(
        value = memo,
        hint = stringResource(R.string.memo_input_hint),
        keyboardType = KeyboardType.Text,
        onValueChange = onMemoChange,
        limit = 100,
        rightComponent = {
            if (memo.isNotEmpty()) {
                Image(
                    painter = painterResource(com.tgyuu.designsystem.R.drawable.ic_delete_circle),
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
}

@Composable
internal fun PreviewContent(
    schedule: TodoSchedule?,
    memo: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = stringResource(R.string.memo_preview_label),
        style = EbbingTheme.typography.body16M,
        color = EbbingTheme.colors.textOnBackground,
        modifier = Modifier.padding(top = 32.dp),
    )

    if (schedule != null) {
        val previewTodo = TodoScheduleUiModel(
            id = schedule.id,
            infoId = schedule.infoId,
            title = ClickableText.from(schedule.title),
            tagId = schedule.tagId,
            name = schedule.name,
            color = schedule.color,
            date = schedule.date,
            memo = ClickableText.from(memo),
            isPinned = schedule.isPinned,
            isDone = schedule.isDone,
            createdAt = schedule.createdAt,
            infoCreatedAt = schedule.infoCreatedAt,
        )

        TodoListCard(
            todo = previewTodo,
            todosWithSameInfo = listOf(previewTodo),
            onCheckedChange = {},
            onEditScheduleClick = {},
            modifier = modifier
                .padding(top = 20.dp)
                .fillMaxWidth(),
        )
    }
}
