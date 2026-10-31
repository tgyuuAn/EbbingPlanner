package com.tgyuu.home.graph.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.tgyuu.designsystem.BasePreview
import com.tgyuu.designsystem.EbbingPreview
import com.tgyuu.designsystem.R
import com.tgyuu.designsystem.foundation.EbbingTheme
import com.tgyuu.designsystem.model.TodoTagUiModel
import com.tgyuu.home.graph.addtodo.contract.ProgressSuggestion
import java.time.LocalDate

@Composable
internal fun NextProgressSuggestionContent(
    suggestions: List<ProgressSuggestion>,
    onSuggestionClick: (ProgressSuggestion) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (suggestions.isEmpty()) return

    Column(
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = modifier.padding(top = 12.dp),
    ) {
        Text(
            text = stringResource(R.string.home_next_progress_title),
            style = EbbingTheme.typography.heading12SB,
            color = EbbingTheme.colors.textSub,
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
        ) {
            suggestions.forEach { suggestion ->
                SuggestionChip(
                    label = suggestion.title,
                    color = suggestion.tag.color,
                    onClick = { onSuggestionClick(suggestion) },
                )
            }
        }
    }
}

@Composable
private fun SuggestionChip(
    label: String,
    color: Int,
    onClick: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .clip(CircleShape)
            .background(EbbingTheme.colors.fillNormal)
            .border(1.dp, EbbingTheme.colors.strokeOutline, CircleShape)
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp),
    ) {
        Spacer(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(Color(color)),
        )

        Text(
            text = label,
            style = EbbingTheme.typography.heading14SB,
            color = EbbingTheme.colors.textSub,
            maxLines = 1,
        )
    }
}

/** 지난 일정에서 복사해 온 값이면 필드 라벨 오른쪽에 출처를 표시해요. */
@Composable
internal fun FieldLabel(
    label: String,
    isFromLast: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier.fillMaxWidth(),
    ) {
        Text(
            text = label,
            style = EbbingTheme.typography.heading14SB,
            color = EbbingTheme.colors.textOnBackground,
        )

        if (isFromLast) {
            Text(
                text = stringResource(R.string.home_from_last_todo),
                style = EbbingTheme.typography.heading12SB,
                color = EbbingTheme.colors.textDisabled,
            )
        }
    }
}

@EbbingPreview
@Composable
private fun PreviewNextProgressSuggestion() {
    BasePreview {
        NextProgressSuggestionContent(
            suggestions = listOf(
                "경제원론 51~" to 0xFFBBE1FA.toInt(),
                "민법총론 3강" to 0xFFFF8FAB.toInt(),
                "영단어" to 0xFFFFD66B.toInt(),
            ).map { (title, color) ->
                ProgressSuggestion(
                    title = title,
                    tag = TodoTagUiModel(id = 1, name = "", color = color, createdAt = LocalDate.now()),
                    repeatCycle = null,
                )
            },
            onSuggestionClick = {},
        )
    }
}
