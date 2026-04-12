package com.tgyuu.shared.ui.feature.home.edittodo

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.tgyuu.shared.designsystem.component.EbbingSubTopBar
import com.tgyuu.shared.designsystem.foundation.EbbingTheme
import com.tgyuu.shared.ui.feature.home.addtodo.component.PriorityContent
import com.tgyuu.shared.ui.feature.home.addtodo.component.TagContent
import com.tgyuu.shared.ui.feature.home.addtodo.component.TitleContent

@Composable
fun EditTodoScreen(
    viewModel: EditTodoViewModel,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()

    Column(modifier = modifier.fillMaxSize()) {
        EbbingSubTopBar(
            title = "일정 수정",
            onNavigationClick = { viewModel.onIntent(EditTodoIntent.OnBackClick) },
            rightComponent = {
                Text(
                    text = "저장",
                    style = if (state.isSaveEnabled) EbbingTheme.typography.bodyMSB
                    else EbbingTheme.typography.bodyMM,
                    color = if (state.isSaveEnabled) EbbingTheme.colors.primaryDefault
                    else EbbingTheme.colors.dark3,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .clickable(enabled = state.isSaveEnabled) {
                            viewModel.onIntent(EditTodoIntent.OnSaveClick)
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
            // Header with date (clickable to change date)
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(textDecoration = TextDecoration.Underline)) {
                        append("${state.selectedDate.monthNumber}월 ${state.selectedDate.dayOfMonth}일")
                    }
                    append(" 에\n진행하는 걸로 바꿀래요")
                },
                style = EbbingTheme.typography.headingLSB,
                color = EbbingTheme.colors.black,
                modifier = Modifier.clickable {
                    viewModel.onIntent(EditTodoIntent.OnSelectedDateDropDownClick)
                },
            )

            TitleContent(
                title = state.title,
                onTitleChange = { viewModel.onIntent(EditTodoIntent.OnTitleChange(it)) },
            )

            TagContent(
                tag = state.tag,
                onTagDropDownClick = { viewModel.onIntent(EditTodoIntent.OnTagDropDownClick) },
            )

            PriorityContent(
                priority = state.priority,
                onPriorityChange = { viewModel.onIntent(EditTodoIntent.OnPriorityChange(it)) },
            )

            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}
