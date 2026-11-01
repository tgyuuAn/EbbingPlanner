package com.tgyuu.tag.graph.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowWidthSizeClass
import com.tgyuu.common.util.clickable
import com.tgyuu.designsystem.BasePreview
import com.tgyuu.designsystem.EbbingPreview
import com.tgyuu.designsystem.R
import com.tgyuu.designsystem.component.bottomsheet.EbbingBottomSheetListItemDefault
import com.tgyuu.designsystem.component.EbbingOutlinedButton
import com.tgyuu.designsystem.component.EbbingSolidButton
import com.tgyuu.designsystem.component.EbbingSubTopBar
import com.tgyuu.designsystem.component.EbbingTextToggle
import com.tgyuu.designsystem.foundation.EbbingTheme
import com.tgyuu.designsystem.model.TodoTagUiModel
import com.tgyuu.tag.graph.main.contract.TagIntent
import com.tgyuu.tag.graph.main.contract.TagState
import com.tgyuu.tag.graph.main.ui.dialog.DeleteDialog
import kotlinx.collections.immutable.persistentListOf
import java.time.LocalDate

@Composable
internal fun TagRoute(
    viewModel: TagViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.loadTags()
    }

    TagScreen(
        state = state,
        onBackClick = { viewModel.onIntent(TagIntent.OnBackClick) },
        onAddClick = { viewModel.onIntent(TagIntent.OnAddClick) },
        onEditClick = { viewModel.onIntent(TagIntent.OnEditClick(it)) },
        onDeleteClick = { viewModel.onIntent(TagIntent.OnDeleteClick(it)) },
        onArchiveClick = { viewModel.onIntent(TagIntent.OnArchiveClick(it)) },
        onUnarchiveClick = { viewModel.onIntent(TagIntent.OnUnarchiveClick(it)) },
        onTabChange = { viewModel.onIntent(TagIntent.OnTabChange(it)) },
    )
}

@Composable
private fun TagScreen(
    state: TagState,
    onBackClick: () -> Unit,
    onAddClick: () -> Unit,
    onEditClick: (TodoTagUiModel) -> Unit,
    onDeleteClick: (TodoTagUiModel) -> Unit,
    onArchiveClick: (TodoTagUiModel) -> Unit,
    onUnarchiveClick: (TodoTagUiModel) -> Unit,
    onTabChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedTag by remember { mutableStateOf<TodoTagUiModel?>(null) }
    val listState = rememberLazyListState()
    val gridListState = rememberLazyGridState()
    var isShowDialog by remember { mutableStateOf(false) }
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass

    if (isShowDialog && selectedTag != null) {
        DeleteDialog(
            tag = selectedTag!!,
            onDismissRequest = { isShowDialog = false },
            onDeleteClick = {
                onDeleteClick(selectedTag!!)
                isShowDialog = false
                selectedTag = null
            },
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .clickable { selectedTag = null },
        ) {
            EbbingSubTopBar(
                title = stringResource(R.string.tag_manage_title),
                onNavigationClick = onBackClick,
                rightComponent = {
                    Text(
                        text = stringResource(R.string.tag_add_button),
                        style = EbbingTheme.typography.body16M,
                        color = EbbingTheme.colors.primaryNormal,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .clickable { onAddClick() },
                    )
                },
                modifier = Modifier.padding(horizontal = 20.dp),
            )
            EbbingTextToggle(
                firstLabel = stringResource(R.string.tag_tab_active),
                secondLabel = stringResource(R.string.tag_tab_archived),
                selectedFirst = !state.showArchived,
                onSelectedChange = { toActive ->
                    selectedTag = null
                    onTabChange(!toActive)
                },
                modifier = Modifier.padding(start = 20.dp, top = 12.dp),
            )
            if (state.visibleTagList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(
                            if (state.showArchived) R.string.tag_archived_empty_message
                            else R.string.tag_empty_message
                        ),
                        style = EbbingTheme.typography.body14M,
                        textAlign = TextAlign.Center,
                        color = EbbingTheme.colors.textDisabled,
                    )
                }
            } else if (windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.COMPACT) {
                LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(bottom = 100.dp),
                    modifier = Modifier
                        .padding(20.dp)
                        .imePadding(),
                ) {
                    items(
                        items = state.visibleTagList,
                        key = { it.id },
                    ) { tag ->
                        EbbingBottomSheetListItemDefault(
                            label = tag.name,
                            color = tag.color,
                            checked = tag.id == selectedTag?.id,
                            onChecked = { selectedTag = tag },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            } else {
                LazyVerticalGrid(
                    state = gridListState,
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    contentPadding = PaddingValues(bottom = 100.dp),
                    modifier = Modifier
                        .padding(20.dp)
                        .imePadding(),
                ) {
                    items(
                        items = state.visibleTagList,
                        key = { it.id },
                    ) { tag ->
                        EbbingBottomSheetListItemDefault(
                            label = tag.name,
                            color = tag.color,
                            checked = tag.id == selectedTag?.id,
                            onChecked = { selectedTag = tag },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp)
                        )
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = selectedTag != null && selectedTag?.id != 1,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 }),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .widthIn(max = 400.dp)
                .fillMaxWidth()
                .padding(20.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(top = 12.dp, bottom = 20.dp),
            ) {
                EbbingOutlinedButton(
                    label = stringResource(R.string.tag_delete),
                    onClick = { isShowDialog = true },
                    modifier = Modifier.weight(1f),
                )

                if (state.showArchived) {
                    EbbingSolidButton(
                        label = stringResource(R.string.tag_unarchive),
                        onClick = {
                            selectedTag?.let(onUnarchiveClick)
                            selectedTag = null
                        },
                        modifier = Modifier.weight(1f),
                    )
                } else {
                    EbbingOutlinedButton(
                        label = stringResource(R.string.tag_archive),
                        onClick = {
                            selectedTag?.let(onArchiveClick)
                            selectedTag = null
                        },
                        modifier = Modifier.weight(1f),
                    )

                    EbbingSolidButton(
                        label = stringResource(R.string.tag_edit),
                        onClick = { onEditClick(selectedTag!!) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@EbbingPreview
@Composable
private fun PreviewTag() {
    BasePreview {
        TagScreen(
            state = TagState(
                tagList = persistentListOf(
                    TodoTagUiModel(
                        id = 3,
                        name = "국어",
                        color = 0xFFFF6961.toInt(),
                        createdAt = LocalDate.now()
                    ),
                    TodoTagUiModel(
                        id = 2,
                        name = "영어",
                        color = 0xFF123298.toInt(),
                        createdAt = LocalDate.now()
                    ),
                )
            ),
            onBackClick = {},
            onAddClick = {},
            onEditClick = {},
            onDeleteClick = {},
            onArchiveClick = {},
            onUnarchiveClick = {},
            onTabChange = {},
        )
    }
}
