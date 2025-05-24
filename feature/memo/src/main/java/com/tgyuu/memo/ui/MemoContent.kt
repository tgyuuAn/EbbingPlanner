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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.tgyuu.common.ui.EbbingVisibleAnimation
import com.tgyuu.common.ui.InputState
import com.tgyuu.common.ui.clickable
import com.tgyuu.designsystem.component.EbbingCheck
import com.tgyuu.designsystem.component.EbbingTextInputDefault
import com.tgyuu.designsystem.foundation.EbbingTheme
import com.tgyuu.domain.model.TodoSchedule

@Composable
internal fun MemoContent(
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
internal fun PreviewContent(
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
                .padding(top = 20.dp)
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
    Column(
        modifier = modifier
            .border(
                color = EbbingTheme.colors.black,
                width = 0.5.dp,
                shape = RoundedCornerShape(12.dp)
            )
            .wrapContentHeight()
            .animateContentSize(
                animationSpec = tween(
                    durationMillis = 300,
                    easing = FastOutSlowInEasing,
                )
            )
            .padding(20.dp)
    ) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            VerticalDivider(
                thickness = 8.dp,
                color = Color(todo.color),
                modifier = Modifier
                    .fillMaxHeight()
                    .animateContentSize(
                        animationSpec = tween(
                            durationMillis = 300,
                            easing = FastOutSlowInEasing
                        )
                    )
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
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = todo.title,
                            style = EbbingTheme.typography.bodyMSB,
                            color = EbbingTheme.colors.black,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(bottom = 4.dp),
                        )

                        Text(
                            text = todo.name,
                            style = EbbingTheme.typography.bodyMM,
                            color = EbbingTheme.colors.dark1,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(bottom = 4.dp),
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp),
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
                        painter = painterResource(com.tgyuu.designsystem.R.drawable.ic_3dots),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(EbbingTheme.colors.dark1),
                        modifier = Modifier.size(20.dp)
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

        EbbingVisibleAnimation(visible = memo.isNotEmpty()) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(end = 32.dp, top = 4.dp, bottom = 4.dp),
            ) {
                Image(
                    painter = painterResource(com.tgyuu.designsystem.R.drawable.ic_memo),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                )

                Text(
                    text = memo,
                    style = EbbingTheme.typography.bodySSB,
                    color = EbbingTheme.colors.dark1,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}
