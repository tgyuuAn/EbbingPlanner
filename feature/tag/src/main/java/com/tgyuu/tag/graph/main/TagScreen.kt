package com.tgyuu.tag.graph.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tgyuu.designsystem.BasePreview
import com.tgyuu.designsystem.EbbingPreview
import com.tgyuu.designsystem.component.EbbingSubTopBar
import com.tgyuu.designsystem.foundation.EbbingTheme
import com.tgyuu.tag.graph.main.contract.TagIntent
import com.tgyuu.tag.graph.main.contract.TagState

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
    )
}

@Composable
private fun TagScreen(
    state: TagState,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()

    Column(modifier = modifier.fillMaxSize()) {
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
                Text(
                    text = tag.name,
                    style = EbbingTheme.typography.bodyMSB,
                    color = Color(tag.color),
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
            state = TagState(),
            onBackClick = {},
        )
    }
}
