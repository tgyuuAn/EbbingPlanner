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
import com.tgyuu.shared.designsystem.component.EbbingDialogDefaultTop
import com.tgyuu.shared.designsystem.component.EbbingOutlinedButton
import com.tgyuu.shared.designsystem.component.EbbingSolidButton
import com.tgyuu.shared.designsystem.component.EbbingSubTopBar
import com.tgyuu.shared.designsystem.component.bottomsheet.EbbingBottomSheetListItemDefault
import com.tgyuu.shared.designsystem.foundation.EbbingTheme
import com.tgyuu.shared.ui.model.RepeatCycleUiModel
import ebbingplanner.shared.generated.resources.Res
import ebbingplanner.shared.generated.resources.repeat_manage_title
import ebbingplanner.shared.generated.resources.repeat_add_button
import ebbingplanner.shared.generated.resources.repeat_empty_message
import ebbingplanner.shared.generated.resources.repeat_delete
import ebbingplanner.shared.generated.resources.repeat_edit
import ebbingplanner.shared.generated.resources.repeat_dialog_back
import ebbingplanner.shared.generated.resources.repeat_delete_dialog_prefix
import ebbingplanner.shared.generated.resources.repeat_delete_dialog_highlight
import ebbingplanner.shared.generated.resources.repeat_delete_dialog_suffix
import ebbingplanner.shared.generated.resources.repeat_delete_dialog_sub_text
import ebbingplanner.shared.generated.resources.repeat_add_title
import ebbingplanner.shared.generated.resources.repeat_edit_title
import ebbingplanner.shared.generated.resources.repeat_save
import ebbingplanner.shared.generated.resources.repeat_add_headline
import ebbingplanner.shared.generated.resources.repeat_edit_headline
import ebbingplanner.shared.generated.resources.repeat_cycle_label
import ebbingplanner.shared.generated.resources.repeat_cycle_input_hint
import ebbingplanner.shared.generated.resources.repeat_cycle_input_guide
import ebbingplanner.shared.generated.resources.repeat_cycle_preview_label
import ebbingplanner.shared.generated.resources.common_clear
import org.jetbrains.compose.resources.stringResource

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
                title = stringResource(Res.string.repeat_manage_title),
                onNavigationClick = { viewModel.onIntent(RepeatCycleIntent.OnBackClick) },
                rightComponent = {
                    Text(
                        text = stringResource(Res.string.repeat_add_button),
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
                        text = stringResource(Res.string.repeat_empty_message),
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
                    label = stringResource(Res.string.repeat_delete),
                    onClick = { isShowDialog = true },
                    modifier = Modifier.weight(1f),
                )

                EbbingSolidButton(
                    label = stringResource(Res.string.repeat_edit),
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
    // Android DeleteDialog와 동일: 공용 EbbingDialogDefaultTop(제목 강조 + 서브텍스트) + EbbingDialogBottom
    val deletePrefix = stringResource(Res.string.repeat_delete_dialog_prefix)
    val deleteHighlight = stringResource(Res.string.repeat_delete_dialog_highlight)
    val deleteSuffix = stringResource(Res.string.repeat_delete_dialog_suffix)
    EbbingDialog(
        dialogTop = {
            EbbingDialogDefaultTop(
                title = buildAnnotatedString {
                    append(deletePrefix)
                    withStyle(style = SpanStyle(color = EbbingTheme.colors.primaryDefault)) {
                        append(deleteHighlight)
                    }
                    append(deleteSuffix)
                },
                subText = stringResource(Res.string.repeat_delete_dialog_sub_text),
            )
        },
        dialogBottom = {
            EbbingDialogBottom(
                leftButtonText = stringResource(Res.string.repeat_dialog_back),
                rightButtonText = stringResource(Res.string.repeat_delete),
                onLeftButtonClick = onDismiss,
                onRightButtonClick = onConfirm,
            )
        },
        onDismissRequest = onDismiss,
    )
}
