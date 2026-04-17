package com.tgyuu.shared.ui.feature.home.edittodo

import com.tgyuu.shared.designsystem.foundation.LayoutConstants
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.unit.dp
import com.tgyuu.shared.designsystem.component.EbbingPartialUnderlineText
import com.tgyuu.shared.designsystem.component.EbbingSolidButton
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

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isWide = maxWidth > LayoutConstants.TABLET_BREAKPOINT

        Column(modifier = Modifier.fillMaxSize()) {
            EbbingSubTopBar(
                title = "일정 수정",
                onNavigationClick = { viewModel.onIntent(EditTodoIntent.OnBackClick) },
                rightComponent = {
                    if (!state.isTreatment) {
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
                    }
                },
                modifier = Modifier.padding(horizontal = 20.dp),
            )

            if (isWide) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .imePadding(),
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .verticalScroll(scrollState)
                            .padding(20.dp),
                    ) {
                        EbbingPartialUnderlineText(
                            underlinedPart = "${state.selectedDate.monthNumber}월 ${state.selectedDate.dayOfMonth}일",
                            rest = " 에\n진행하는 걸로 바꿀래요",
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
                        Spacer(modifier = Modifier.height(40.dp))
                    }
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(20.dp),
                    ) {
                        TagContent(
                            tag = state.tag,
                            onTagDropDownClick = { viewModel.onIntent(EditTodoIntent.OnTagDropDownClick) },
                        )
                        PriorityContent(
                            priority = state.priority,
                            onPriorityChange = { viewModel.onIntent(EditTodoIntent.OnPriorityChange(it)) },
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(scrollState)
                        .padding(20.dp)
                        .imePadding(),
                ) {
                    EbbingPartialUnderlineText(
                        underlinedPart = "${state.selectedDate.monthNumber}월 ${state.selectedDate.dayOfMonth}일",
                        rest = " 에\n진행하는 걸로 바꿀래요",
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

            if (state.isTreatment) {
                EbbingSolidButton(
                    label = "저장",
                    onClick = { viewModel.onIntent(EditTodoIntent.OnSaveClick) },
                    enabled = state.isSaveEnabled,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(EbbingTheme.colors.background)
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                )
            }
        }
    }
}
