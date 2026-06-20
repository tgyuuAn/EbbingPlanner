package com.tgyuu.shared.ui.feature.repeatcycle

import com.tgyuu.shared.designsystem.foundation.LayoutConstants
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.tgyuu.shared.designsystem.component.EbbingDialog
import com.tgyuu.shared.designsystem.component.EbbingDialogBottom
import com.tgyuu.shared.designsystem.component.EbbingOutlinedButton
import com.tgyuu.shared.designsystem.component.EbbingSolidButton
import com.tgyuu.shared.designsystem.component.EbbingSubTopBar
import com.tgyuu.shared.designsystem.component.bottomsheet.EbbingBottomSheetListItemDefault
import com.tgyuu.shared.designsystem.foundation.EbbingTheme
import com.tgyuu.shared.ui.model.RepeatCycleUiModel

@Composable
fun RepeatCycleScreen(
    viewModel: RepeatCycleViewModel,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsState()
    var selectedRepeatCycle by remember { mutableStateOf<RepeatCycleUiModel?>(null) }
    var isShowDialog by remember { mutableStateOf(false) }

    selectedRepeatCycle?.let { selected ->
        if (isShowDialog) {
            DeleteRepeatCycleDialog(
                onConfirm = {
                    viewModel.onIntent(RepeatCycleIntent.OnDeleteClick(selected))
                    isShowDialog = false
                    selectedRepeatCycle = null
                },
                onDismiss = { isShowDialog = false },
            )
        }
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isWide = maxWidth > LayoutConstants.TABLET_BREAKPOINT

        Column(
            modifier = Modifier
                .fillMaxSize()
                .clickable { selectedRepeatCycle = null },
        ) {
            EbbingSubTopBar(
                title = "반복 주기 관리",
                onNavigationClick = { viewModel.onIntent(RepeatCycleIntent.OnBackClick) },
                rightComponent = {
                    Text(
                        text = "추가",
                        style = EbbingTheme.typography.bodyMM,
                        color = EbbingTheme.colors.primaryDefault,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .clickable { viewModel.onIntent(RepeatCycleIntent.OnAddClick) },
                    )
                },
                modifier = Modifier.padding(horizontal = 20.dp),
            )

            if (state.repeatCycleList.isEmpty()) {
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "등록된 반복 주기가 없어요.\n우측 상단 추가 버튼을 눌러 반복 주기를 추가해보세요.",
                        style = EbbingTheme.typography.bodySM,
                        textAlign = TextAlign.Center,
                        color = EbbingTheme.colors.dark3,
                    )
                }
            } else if (isWide) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    contentPadding = PaddingValues(bottom = 100.dp),
                    modifier = Modifier.padding(20.dp).imePadding(),
                ) {
                    items(items = state.repeatCycleList, key = { it.id }) { repeatCycle ->
                        EbbingBottomSheetListItemDefault(
                            label = repeatCycle.displayName,
                            checked = repeatCycle.id == selectedRepeatCycle?.id,
                            onChecked = { selectedRepeatCycle = repeatCycle },
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 100.dp),
                    modifier = Modifier.padding(20.dp).imePadding(),
                ) {
                    items(items = state.repeatCycleList, key = { it.id }) { repeatCycle ->
                        EbbingBottomSheetListItemDefault(
                            label = "- ${repeatCycle.displayName}",
                            checked = repeatCycle.id == selectedRepeatCycle?.id,
                            onChecked = { selectedRepeatCycle = repeatCycle },
                            modifier = Modifier.fillMaxWidth(),
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
                    onClick = { selectedRepeatCycle?.let { viewModel.onIntent(RepeatCycleIntent.OnEditClick(it)) } },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun DeleteRepeatCycleDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    EbbingDialog(onDismissRequest = onDismiss) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 20.dp),
        ) {
            Text(
                text = buildAnnotatedString {
                    append("선택하신 반복 주기를 ")
                    withStyle(style = SpanStyle(color = EbbingTheme.colors.primaryDefault)) {
                        append("삭제")
                    }
                    append(" 하시겠습니까?")
                },
                style = EbbingTheme.typography.headingMSB,
                color = EbbingTheme.colors.black,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 40.dp),
            )

            Text(
                text = "삭제한 반복 주기는 되돌릴 수 없으니 신중히 선택해 주세요.",
                style = EbbingTheme.typography.bodyMM,
                color = EbbingTheme.colors.dark1,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 16.dp, bottom = 24.dp),
            )

            EbbingDialogBottom(
                leftButtonText = "뒤로",
                rightButtonText = "삭제",
                onLeftButtonClick = onDismiss,
                onRightButtonClick = onConfirm,
            )
        }
    }
}
