package com.tgyuu.shared.ui.feature.home.edittodo

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tgyuu.shared.designsystem.component.EbbingPartialUnderlineText
import com.tgyuu.shared.designsystem.component.EbbingSolidButton
import com.tgyuu.shared.designsystem.component.EbbingSubTopBar
import com.tgyuu.shared.designsystem.component.bottomsheet.EbbingModalBottomSheet
import com.tgyuu.shared.designsystem.component.bottomsheet.rememberEbbingBottomSheetState
import com.tgyuu.shared.designsystem.foundation.EbbingTheme
import com.tgyuu.shared.designsystem.foundation.LayoutConstants
import com.tgyuu.shared.ui.feature.home.addtodo.bottomsheet.SelectedDateBottomSheetContent
import com.tgyuu.shared.ui.feature.home.addtodo.bottomsheet.TagBottomSheetContent
import com.tgyuu.shared.ui.feature.home.addtodo.component.PinnedContent
import com.tgyuu.shared.ui.feature.home.addtodo.component.TagContent
import com.tgyuu.shared.ui.feature.home.addtodo.component.TitleContent
import ebbingplanner.shared.generated.resources.Res
import ebbingplanner.shared.generated.resources.home_edit_todo_button
import ebbingplanner.shared.generated.resources.home_edit_todo_header_suffix
import ebbingplanner.shared.generated.resources.home_edit_todo_title
import ebbingplanner.shared.generated.resources.home_month_day
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

private enum class EditTodoBottomSheetType {
    DATE, TAG
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTodoScreen(
    viewModel: EditTodoViewModel,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()

    val scope = rememberCoroutineScope()
    val bottomSheetState = rememberEbbingBottomSheetState()
    var currentBottomSheetType by remember { mutableStateOf<EditTodoBottomSheetType?>(null) }

    EbbingModalBottomSheet(
        sheetState = bottomSheetState,
        onDismissRequest = {
            scope.launch {
                bottomSheetState.hide()
                currentBottomSheetType = null
            }
        },
        content = when (currentBottomSheetType) {
            EditTodoBottomSheetType.DATE -> {
                {
                    SelectedDateBottomSheetContent(
                        originSelectedDate = state.selectedDate,
                        schedulesByDateMap = state.schedulesByDateMap,
                        onDateSelected = { date ->
                            viewModel.onIntent(EditTodoIntent.OnSelectedDateChange(date))
                            scope.launch {
                                bottomSheetState.hide()
                                currentBottomSheetType = null
                            }
                        },
                    )
                }
            }
            EditTodoBottomSheetType.TAG -> {
                {
                    TagBottomSheetContent(
                        tagList = state.tagList,
                        selectedTag = state.tag,
                        onTagSelected = { tag ->
                            viewModel.onIntent(EditTodoIntent.OnTagChange(tag))
                            scope.launch {
                                bottomSheetState.hide()
                                currentBottomSheetType = null
                            }
                        },
                        onAddTagClick = {
                            scope.launch {
                                bottomSheetState.hide()
                                currentBottomSheetType = null
                            }
                            viewModel.onIntent(EditTodoIntent.OnAddTagClick)
                        },
                    )
                }
            }
            null -> null
        },
    )

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isWide = maxWidth > LayoutConstants.TABLET_BREAKPOINT

        Column(
            modifier = Modifier
                .fillMaxSize()
                .then(if (isWide) Modifier else Modifier.imePadding()),
        ) {
            EbbingSubTopBar(
                title = stringResource(Res.string.home_edit_todo_title),
                onNavigationClick = { viewModel.onIntent(EditTodoIntent.OnBackClick) },
                rightComponent = {},
                modifier = Modifier.padding(horizontal = 20.dp),
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .padding(20.dp)
                    .then(if (isWide) Modifier.imePadding() else Modifier),
            ) {
                EbbingPartialUnderlineText(
                    underlinedPart = stringResource(
                        Res.string.home_month_day,
                        state.selectedDate.monthNumber,
                        state.selectedDate.dayOfMonth,
                    ),
                    rest = stringResource(Res.string.home_edit_todo_header_suffix),
                    style = EbbingTheme.typography.headingLSB,
                    color = EbbingTheme.colors.black,
                    highlightColor = EbbingTheme.colors.primaryDefault,
                    modifier = Modifier.clickable {
                        currentBottomSheetType = EditTodoBottomSheetType.DATE
                        scope.launch { bottomSheetState.show() }
                    },
                )

                TitleContent(
                    title = state.title,
                    onTitleChange = { viewModel.onIntent(EditTodoIntent.OnTitleChange(it)) },
                )

                TagContent(
                    tag = state.tag,
                    onTagDropDownClick = {
                        currentBottomSheetType = EditTodoBottomSheetType.TAG
                        scope.launch { bottomSheetState.show() }
                    },
                )

                PinnedContent(
                    isPinned = state.isPinned,
                    onPinnedChange = { viewModel.onIntent(EditTodoIntent.OnPinnedChange(it)) },
                )

                Spacer(modifier = Modifier.height(60.dp))
            }

            EbbingSolidButton(
                label = stringResource(Res.string.home_edit_todo_button),
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
