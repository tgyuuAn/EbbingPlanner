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
import com.tgyuu.designsystem.component.EbbingSolidButton
import com.tgyuu.designsystem.component.EbbingSubTopBar
import com.tgyuu.designsystem.foundation.EbbingTheme
import com.tgyuu.designsystem.R
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
                title = stringResource(R.string.home_edit_todo_title),
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
                val monthDayText = stringResource(
                    R.string.home_month_day,
                    state.selectedDate.monthValue,
                    state.selectedDate.dayOfMonth,
                )
                val editTodoHeaderSuffix = stringResource(R.string.home_edit_todo_header_suffix)
                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            SpanStyle(
                                textDecoration = TextDecoration.Underline,
                                color = EbbingTheme.colors.textPrimary,
                            )
                        ) {
                            append(monthDayText)
                        }
                        append(editTodoHeaderSuffix)
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

            EbbingSolidButton(
                label = stringResource(R.string.home_edit_todo_button),
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
            modifier = modifier.fillMaxSize(),
        ) {
            EbbingSubTopBar(
                title = stringResource(R.string.home_edit_todo_title),
                onNavigationClick = onBackClick,
                rightComponent = {},
                modifier = Modifier.padding(horizontal = 20.dp),
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .padding(20.dp)
                    .imePadding(),
            ) {
                val monthDayText = stringResource(
                    R.string.home_month_day,
                    state.selectedDate.monthValue,
                    state.selectedDate.dayOfMonth,
                )
                val editTodoHeaderSuffix = stringResource(R.string.home_edit_todo_header_suffix)
                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(textDecoration = TextDecoration.Underline)) {
                            append(monthDayText)
                        }
                        append(editTodoHeaderSuffix)
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

            EbbingSolidButton(
                label = stringResource(R.string.home_edit_todo_button),
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
