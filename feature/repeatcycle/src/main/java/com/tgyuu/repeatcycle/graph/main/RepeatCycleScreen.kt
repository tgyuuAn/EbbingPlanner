package com.tgyuu.repeatcycle.graph.main

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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import androidx.window.core.layout.WindowWidthSizeClass
import com.tgyuu.common.util.clickable
import com.tgyuu.designsystem.BasePreview
import com.tgyuu.designsystem.EbbingPreview
import com.tgyuu.designsystem.component.bottomsheet.EbbingBottomSheetListItemDefault
import com.tgyuu.designsystem.component.EbbingOutlinedButton
import com.tgyuu.designsystem.component.EbbingSolidButton
import com.tgyuu.designsystem.component.EbbingSubTopBar
import com.tgyuu.designsystem.R
import com.tgyuu.designsystem.foundation.EbbingTheme
import com.tgyuu.designsystem.model.RepeatCycleUiModel
import com.tgyuu.repeatcycle.graph.main.contract.RepeatCycleIntent
import com.tgyuu.repeatcycle.graph.main.contract.RepeatCycleState
import com.tgyuu.repeatcycle.graph.main.ui.dialog.DeleteDialog
import kotlinx.collections.immutable.persistentListOf

@Composable
internal fun RepeatCycleRoute(
    viewModel: RepeatCycleViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.loadTags()
    }

    RepeatCycleScreen(
        state = state,
        onBackClick = { viewModel.onIntent(RepeatCycleIntent.OnBackClick) },
        onAddClick = { viewModel.onIntent(RepeatCycleIntent.OnAddClick) },
        onEditClick = { viewModel.onIntent(RepeatCycleIntent.OnEditClick(it)) },
        onDeleteClick = { viewModel.onIntent(RepeatCycleIntent.OnDeleteClick(it)) },
    )
}

@Composable
private fun RepeatCycleScreen(
    state: RepeatCycleState,
    onBackClick: () -> Unit,
    onAddClick: () -> Unit,
    onEditClick: (RepeatCycleUiModel) -> Unit,
    onDeleteClick: (RepeatCycleUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedRepeatCycle by remember { mutableStateOf<RepeatCycleUiModel?>(null) }
    val listState = rememberLazyListState()
    val gridListState = rememberLazyGridState()
    var isShowDialog by remember { mutableStateOf(false) }
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass

    selectedRepeatCycle?.let { toDelete ->
        if (isShowDialog) {
            DeleteDialog(
                onDismissRequest = { isShowDialog = false },
                onDeleteClick = {
                    onDeleteClick(toDelete)
                    isShowDialog = false
                    selectedRepeatCycle = null
                },
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .clickable { selectedRepeatCycle = null },
        ) {
            EbbingSubTopBar(
                title = stringResource(R.string.repeat_manage_title),
                onNavigationClick = onBackClick,
                rightComponent = {
                    Text(
                        text = stringResource(R.string.repeat_add_button),
                        style = EbbingTheme.typography.body16M,
                        color = EbbingTheme.colors.primaryNormal,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .clickable { onAddClick() },
                    )
                },
                modifier = Modifier.padding(horizontal = 20.dp),
            )
            if (state.repeatCycleList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(R.string.repeat_empty_message),
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
                        items = state.repeatCycleList,
                        key = { it.id },
                    ) { repeatCycle ->
                        EbbingBottomSheetListItemDefault(
                            label = stringResource(R.string.repeat_cycle_list_item, repeatCycle.displayName),
                            checked = repeatCycle.id == selectedRepeatCycle?.id,
                            onChecked = { selectedRepeatCycle = repeatCycle },
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
                        items = state.repeatCycleList,
                        key = { it.id },
                    ) { tag ->
                        EbbingBottomSheetListItemDefault(
                            label = tag.displayName,
                            checked = tag.id == selectedRepeatCycle?.id,
                            onChecked = { selectedRepeatCycle = tag },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp)
                        )
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = selectedRepeatCycle != null,
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
                    label = stringResource(R.string.repeat_delete),
                    onClick = { isShowDialog = true },
                    modifier = Modifier.weight(1f),
                )

                EbbingSolidButton(
                    label = stringResource(R.string.repeat_edit),
                    onClick = { selectedRepeatCycle?.let(onEditClick) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@EbbingPreview
@Composable
private fun PreviewRepeatCycle() {
    BasePreview {
        RepeatCycleScreen(
            state = RepeatCycleState(
                repeatCycleList = persistentListOf(
                    RepeatCycleUiModel(id = 1, intervals = persistentListOf(1, 3, 7, 14, 30), displayName = "1일, 3일, 7일, 14일, 30일"),
                    RepeatCycleUiModel(id = 2, intervals = persistentListOf(1, 2, 4, 7, 14), displayName = "1일, 2일, 4일, 7일, 14일"),
                )
            ),
            onBackClick = {},
            onAddClick = {},
            onEditClick = {},
            onDeleteClick = {},
        )
    }
}
