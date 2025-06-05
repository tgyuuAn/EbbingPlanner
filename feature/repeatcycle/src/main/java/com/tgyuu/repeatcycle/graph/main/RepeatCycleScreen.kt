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
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowWidthSizeClass
import com.tgyuu.common.ui.clickable
import com.tgyuu.designsystem.BasePreview
import com.tgyuu.designsystem.EbbingPreview
import com.tgyuu.designsystem.component.EbbingBottomSheetListItemDefault
import com.tgyuu.designsystem.component.EbbingOutlinedButton
import com.tgyuu.designsystem.component.EbbingSolidButton
import com.tgyuu.designsystem.component.EbbingSubTopBar
import com.tgyuu.domain.model.DefaultRepeatCycles
import com.tgyuu.domain.model.RepeatCycle
import com.tgyuu.repeatcycle.graph.main.contract.RepeatCycleIntent
import com.tgyuu.repeatcycle.graph.main.contract.RepeatCycleState
import com.tgyuu.repeatcycle.graph.main.ui.dialog.DeleteDialog

@Composable
internal fun RepeatCycleRoute(
    viewModel: RepeatCycleViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadTags()
    }

    RepeatCycleScreen(
        state = state,
        onBackClick = { viewModel.onIntent(RepeatCycleIntent.OnBackClick) },
        onEditClick = { viewModel.onIntent(RepeatCycleIntent.OnEditClick(it)) },
        onDeleteClick = { viewModel.onIntent(RepeatCycleIntent.OnDeleteClick(it)) },
    )
}

@Composable
private fun RepeatCycleScreen(
    state: RepeatCycleState,
    onBackClick: () -> Unit,
    onEditClick: (RepeatCycle) -> Unit,
    onDeleteClick: (RepeatCycle) -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedRepeatCycle by remember { mutableStateOf<RepeatCycle?>(null) }
    val listState = rememberLazyListState()
    val gridListState = rememberLazyGridState()
    var isShowDialog by remember { mutableStateOf(false) }
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass

    if (isShowDialog && selectedRepeatCycle != null) {
        DeleteDialog(
            onDismissRequest = { isShowDialog = false },
            onDeleteClick = {
                onDeleteClick(selectedRepeatCycle!!)
                isShowDialog = false
                selectedRepeatCycle = null
            },
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .clickable { selectedRepeatCycle = null },
        ) {
            EbbingSubTopBar(
                title = "반복 주기 관리",
                onNavigationClick = onBackClick,
                modifier = Modifier.padding(horizontal = 20.dp),
            )
            if (windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.COMPACT) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .padding(20.dp)
                        .imePadding(),
                ) {
                    items(
                        items = state.repeatCycleList,
                        key = { it.id },
                    ) { repeatCycle ->
                        EbbingBottomSheetListItemDefault(
                            label = "- ${repeatCycle.toDisplayName()}",
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
                    modifier = Modifier
                        .padding(20.dp)
                        .imePadding(),
                ) {
                    items(
                        items = state.repeatCycleList,
                        key = { it.id },
                    ) { tag ->
                        EbbingBottomSheetListItemDefault(
                            label = tag.toDisplayName(),
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
                    label = "삭제",
                    onClick = { isShowDialog = true },
                    modifier = Modifier.weight(1f),
                )

                EbbingSolidButton(
                    label = "수정",
                    onClick = { onEditClick(selectedRepeatCycle!!) },
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
            state = RepeatCycleState(repeatCycleList = DefaultRepeatCycles),
            onBackClick = {},
            onEditClick = {},
            onDeleteClick = {},
        )
    }
}
