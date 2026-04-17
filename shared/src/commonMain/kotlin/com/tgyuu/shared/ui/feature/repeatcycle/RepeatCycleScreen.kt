package com.tgyuu.shared.ui.feature.repeatcycle

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.tgyuu.shared.designsystem.component.EbbingDialog
import com.tgyuu.shared.designsystem.component.EbbingDialogBottom
import com.tgyuu.shared.designsystem.component.EbbingSubTopBar
import com.tgyuu.shared.designsystem.foundation.EbbingTheme
import com.tgyuu.shared.ui.model.RepeatCycleUiModel

@Composable
fun RepeatCycleScreen(
    viewModel: RepeatCycleViewModel,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsState()
    var repeatCycleToDelete by remember { mutableStateOf<RepeatCycleUiModel?>(null) }

    if (repeatCycleToDelete != null) {
        DeleteRepeatCycleDialog(
            displayName = repeatCycleToDelete!!.displayName,
            onConfirm = {
                viewModel.onIntent(RepeatCycleIntent.OnDeleteClick(repeatCycleToDelete!!))
                repeatCycleToDelete = null
            },
            onDismiss = { repeatCycleToDelete = null },
        )
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
    val isWide = maxWidth > 600.dp
    Column(modifier = Modifier.fillMaxSize()) {
        EbbingSubTopBar(
            title = "반복 주기 관리",
            onNavigationClick = { viewModel.onIntent(RepeatCycleIntent.OnBackClick) },
            rightComponent = {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "반복 주기 추가",
                    tint = EbbingTheme.colors.primaryDefault,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .size(28.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(EbbingTheme.colors.light3)
                        .clickable { viewModel.onIntent(RepeatCycleIntent.OnAddClick) }
                        .padding(4.dp),
                )
            },
            modifier = Modifier.padding(horizontal = 20.dp),
        )

        HorizontalDivider(
            color = EbbingTheme.colors.light2,
            thickness = 1.dp,
        )

        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = EbbingTheme.colors.primaryDefault)
            }
        } else if (state.repeatCycleList.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "반복 주기가 없습니다.\n우측 상단 + 버튼을 눌러 추가해보세요.",
                    style = EbbingTheme.typography.bodyMM,
                    color = EbbingTheme.colors.dark2,
                )
            }
        } else if (isWide) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize().padding(20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(items = state.repeatCycleList, key = { it.id }) { repeatCycle ->
                    RepeatCycleItem(
                        repeatCycle = repeatCycle,
                        onClick = { viewModel.onIntent(RepeatCycleIntent.OnEditClick(repeatCycle)) },
                        onDeleteClick = { repeatCycleToDelete = repeatCycle },
                    )
                }
                item { Spacer(modifier = Modifier.height(24.dp)) }
                item { Spacer(modifier = Modifier.height(24.dp)) }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(
                    items = state.repeatCycleList,
                    key = { it.id },
                ) { repeatCycle ->
                    RepeatCycleItem(
                        repeatCycle = repeatCycle,
                        onClick = { viewModel.onIntent(RepeatCycleIntent.OnEditClick(repeatCycle)) },
                        onDeleteClick = { repeatCycleToDelete = repeatCycle },
                    )
                }

                item { Spacer(modifier = Modifier.height(24.dp)) }
            }
        }
    }
    } // BoxWithConstraints
}

@Composable
private fun RepeatCycleItem(
    repeatCycle: RepeatCycleUiModel,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(EbbingTheme.colors.light3)
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = repeatCycle.displayName,
            style = EbbingTheme.typography.bodyMSB,
            color = EbbingTheme.colors.black,
            modifier = Modifier.weight(1f),
        )

        IconButton(onClick = onDeleteClick) {
            Icon(
                Icons.Filled.Delete,
                contentDescription = "삭제",
                tint = EbbingTheme.colors.error,
            )
        }
    }
}

@Composable
private fun DeleteRepeatCycleDialog(
    displayName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    EbbingDialog(onDismissRequest = onDismiss) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 20.dp),
        ) {
            Text(
                text = "반복 주기 삭제",
                style = EbbingTheme.typography.headingMSB,
                color = EbbingTheme.colors.black,
                modifier = Modifier.padding(top = 40.dp),
            )

            Text(
                text = "'$displayName' 반복 주기를 삭제하시겠습니까?",
                style = EbbingTheme.typography.bodyMM,
                color = EbbingTheme.colors.dark1,
                modifier = Modifier.padding(top = 16.dp, bottom = 24.dp),
            )

            EbbingDialogBottom(
                leftButtonText = "취소",
                rightButtonText = "삭제",
                onLeftButtonClick = onDismiss,
                onRightButtonClick = onConfirm,
            )
        }
    }
}
