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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import com.tgyuu.common.ui.clickable
import com.tgyuu.designsystem.BasePreview
import com.tgyuu.designsystem.EbbingPreview
import com.tgyuu.designsystem.component.EbbingBottomSheetListItemDefault
import com.tgyuu.designsystem.component.EbbingOutlinedButton
import com.tgyuu.designsystem.component.EbbingSolidButton
import com.tgyuu.designsystem.component.EbbingSubTopBar
import com.tgyuu.domain.model.TodoTag
import com.tgyuu.tag.graph.main.contract.TagIntent
import com.tgyuu.tag.graph.main.contract.TagState
import com.tgyuu.tag.graph.main.ui.dialog.DeleteDialog
import java.time.LocalDate

@Composable
internal fun TagRoute(
    viewModel: TagViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadTags()
    }

    TagScreen(
        state = state,
        onBackClick = { viewModel.onIntent(TagIntent.OnBackClick) },
        onEditClick = { viewModel.onIntent(TagIntent.OnEditClick(it)) },
        onDeleteClick = { viewModel.onIntent(TagIntent.OnDeleteClick(it)) },
    )
}

@Composable
private fun TagScreen(
    state: TagState,
    onBackClick: () -> Unit,
    onEditClick: (TodoTag) -> Unit,
    onDeleteClick: (TodoTag) -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedTag by remember { mutableStateOf<TodoTag?>(null) }
    val listState = rememberLazyListState()
    var isShowDialog by remember { mutableStateOf(false) }

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
                title = "태그 관리",
                onNavigationClick = onBackClick,
                modifier = Modifier.padding(horizontal = 20.dp),
            )

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .padding(20.dp)
                    .imePadding(),
            ) {
                items(
                    items = state.tagList,
                    key = { it.id },
                ) { tag ->
                    if (tag.id != 1) {
                        EbbingBottomSheetListItemDefault(
                            label = tag.name,
                            color = tag.color,
                            checked = tag.id == selectedTag?.id,
                            onChecked = { selectedTag = tag },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = selectedTag != null,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 }),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(20.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = 20.dp),
            ) {
                EbbingOutlinedButton(
                    label = "삭제",
                    onClick = { isShowDialog = true },
                    modifier = Modifier.weight(1f),
                )

                EbbingSolidButton(
                    label = "수정",
                    onClick = { onEditClick(selectedTag!!) },
                    modifier = Modifier.weight(1f),
                )
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
                tagList = listOf(
                    TodoTag(
                        id = 3,
                        name = "국어",
                        color = 0xFFFF6961.toInt(),
                        createdAt = LocalDate.now()
                    ),
                    TodoTag(
                        id = 2,
                        name = "영어",
                        color = 0xFF123298.toInt(),
                        createdAt = LocalDate.now()
                    ),
                )
            ),
            onBackClick = {},
            onEditClick = {},
            onDeleteClick = {},
        )
    }
}
