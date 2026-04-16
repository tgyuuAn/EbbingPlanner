package com.tgyuu.home.graph.edittodo

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.LaunchedEffect
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
import com.tgyuu.common.util.throttledClickable
import com.tgyuu.designsystem.BasePreview
import com.tgyuu.designsystem.EbbingPreview
import com.tgyuu.designsystem.component.EbbingSolidButton
import com.tgyuu.designsystem.component.EbbingSubTopBar
import com.tgyuu.designsystem.foundation.EbbingTheme
import com.tgyuu.home.graph.ui.bottomsheet.SelectedDateBottomSheet
import com.tgyuu.home.graph.ui.bottomsheet.TagBottomSheet
import com.tgyuu.home.graph.edittodo.contract.EditTodoIntent
import com.tgyuu.home.graph.edittodo.contract.EditTodoState
import com.tgyuu.home.graph.ui.PriorityContent
import com.tgyuu.home.graph.ui.TagContent
import com.tgyuu.home.graph.ui.TitleContent
import java.time.LocalDate

@Composable
internal fun EditTodoRoute(
    viewModel: EditTodoViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.loadNewTag()
        viewModel.loadTags()
    }

    EditTodoScreen(
        state = state,
        onBackClick = { viewModel.onIntent(EditTodoIntent.OnBackClick) },
        onSelectedDateChangeClick = {
            viewModel.onIntent(
                EditTodoIntent.OnSelectedDataChangeClick(
                    {
                        SelectedDateBottomSheet(
                            originSelectedDate = state.selectedDate,
                            schedulesByDateMap = state.schedulesByDateMap,
                            startFromMonday = state.mondayStart,
                            updateSelectedDate = {
                                viewModel.onIntent(EditTodoIntent.OnSelectedDateChange(it))
                            },
                        )
                    }
                )
            )
        },
        onTitleChange = { viewModel.onIntent(EditTodoIntent.OnTitleChange(it)) },
        onPriorityChange = { viewModel.onIntent(EditTodoIntent.OnPriorityChange(it)) },
        onTagDropDownClick = {
            viewModel.onIntent(
                EditTodoIntent.OnTagDropDownClick(
                    {
                        TagBottomSheet(
                            originTag = state.tag,
                            tagList = state.tagList,
                            updateTag = { viewModel.onIntent(EditTodoIntent.OnTagChange(it)) },
                            onAddTagClick = { viewModel.onIntent(EditTodoIntent.OnAddTagClick) },
                        )
                    }
                )
            )
        },
        onSaveClick = { viewModel.onIntent(EditTodoIntent.OnSaveClick) },
    )
}

@Composable
private fun EditTodoScreen(
    state: EditTodoState,
    onBackClick: () -> Unit,
    onSelectedDateChangeClick: () -> Unit,
    onTitleChange: (String) -> Unit,
    onPriorityChange: (String) -> Unit,
    onTagDropDownClick: () -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass

    if (windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.COMPACT) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .imePadding(),
        ) {
            EbbingSubTopBar(
                title = "일정 수정",
                onNavigationClick = onBackClick,
                rightComponent = {
                    if (!state.isTreatment) {
                        Text(
                            text = "저장",
                            style = if (state.isSaveEnabled) EbbingTheme.typography.body16M else EbbingTheme.typography.body16M,
                            color = if (state.isSaveEnabled) EbbingTheme.colors.primaryNormal else EbbingTheme.colors.textDisabled,
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
                    }
                },
                modifier = Modifier.padding(horizontal = 20.dp),
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .padding(20.dp),
            ) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(textDecoration = TextDecoration.Underline)) {
                            append("${state.selectedDate.monthValue}월 ${state.selectedDate.dayOfMonth}일")
                        }
                        append(" 에\n진행하는 걸로 바꿀래요")
                    },
                    style = EbbingTheme.typography.heading24B,
                    color = EbbingTheme.colors.textOnBackground,
                    modifier = Modifier.clickable { onSelectedDateChangeClick() },
                )

                TitleContent(
                    scrollState = scrollState,
                    title = state.title,
                    onTitleChange = onTitleChange,
                )

                TagContent(
                    tag = state.tag,
                    onTagDropDownClick = onTagDropDownClick,
                )

                PriorityContent(
                    priority = state.priority,
                    onPriorityChange = onPriorityChange,
                )

                Spacer(modifier = Modifier.height(60.dp))
            }

            if (state.isTreatment) {
                EbbingSolidButton(
                    label = "저장",
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
    } else {
        Column(
            modifier = modifier.fillMaxSize(),
        ) {
            EbbingSubTopBar(
                title = "일정 수정",
                onNavigationClick = onBackClick,
                rightComponent = {
                    Text(
                        text = "저장",
                        style = if (state.isSaveEnabled) EbbingTheme.typography.body16M else EbbingTheme.typography.body16M,
                        color = if (state.isSaveEnabled) EbbingTheme.colors.primaryNormal else EbbingTheme.colors.textDisabled,
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
                            append("${state.selectedDate.monthValue}월 ${state.selectedDate.dayOfMonth}일")
                        }
                        append(" 에\n진행하는 걸로 바꿀래요")
                    },
                    style = EbbingTheme.typography.heading24B,
                    color = EbbingTheme.colors.textOnBackground,
                    modifier = Modifier.clickable { onSelectedDateChangeClick() },
                )

                TitleContent(
                    scrollState = scrollState,
                    title = state.title,
                    onTitleChange = onTitleChange,
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(40.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        TagContent(
                            tag = state.tag,
                            onTagDropDownClick = onTagDropDownClick,
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        PriorityContent(
                            priority = state.priority,
                            onPriorityChange = onPriorityChange,
                        )
                    }
                }
            }
        }
    }
}

@EbbingPreview
@Composable
private fun PreviewAddTodo() {
    BasePreview {
        EditTodoScreen(
            state = EditTodoState(
                selectedDate = LocalDate.now(),
                title = "토익",
                priority = "3",
            ),
            onSelectedDateChangeClick = {},
            onSaveClick = {},
            onBackClick = {},
            onTitleChange = {},
            onPriorityChange = {},
            onTagDropDownClick = {},
        )
    }
}
