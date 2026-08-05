package com.tgyuu.shared.ui.feature.repeatcycle.addrepeatcycle

import com.tgyuu.shared.designsystem.foundation.LayoutConstants
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tgyuu.shared.designsystem.component.EbbingSubTopBar
import com.tgyuu.shared.designsystem.component.EbbingTextInputDefault
import com.tgyuu.shared.designsystem.foundation.EbbingTheme
import com.tgyuu.shared.domain.model.RepeatCycle
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
import ebbingplanner.shared.generated.resources.repeat_register_button
import ebbingplanner.shared.generated.resources.repeat_add_headline
import ebbingplanner.shared.generated.resources.repeat_edit_headline
import ebbingplanner.shared.generated.resources.repeat_cycle_label
import ebbingplanner.shared.generated.resources.repeat_cycle_input_hint
import ebbingplanner.shared.generated.resources.repeat_cycle_input_guide
import ebbingplanner.shared.generated.resources.repeat_cycle_preview_label
import ebbingplanner.shared.generated.resources.common_clear
import org.jetbrains.compose.resources.stringResource
import com.tgyuu.shared.designsystem.model.toRepeatPreviewDisplay
import org.jetbrains.compose.resources.painterResource
import ebbingplanner.shared.generated.resources.ic_delete_circle

@Composable
fun AddRepeatCycleScreen(
    viewModel: AddRepeatCycleViewModel,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
    val isWide = maxWidth > LayoutConstants.TABLET_BREAKPOINT
    Column(modifier = Modifier.fillMaxSize()) {
        EbbingSubTopBar(
            title = stringResource(Res.string.repeat_add_title),
            onNavigationClick = { viewModel.onIntent(AddRepeatCycleIntent.OnBackClick) },
            rightComponent = {
                if (!state.isTreatment) {
                Text(
                    text = stringResource(Res.string.repeat_register_button),
                    style = if (state.isSaveEnabled) EbbingTheme.typography.bodyMSB
                    else EbbingTheme.typography.bodyMM,
                    color = if (state.isSaveEnabled) EbbingTheme.colors.primaryDefault
                    else EbbingTheme.colors.dark3,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .clickable(enabled = state.isSaveEnabled) {
                            viewModel.onIntent(AddRepeatCycleIntent.OnSaveClick)
                        },
                )
                }
            },
            modifier = Modifier.padding(horizontal = 20.dp),
        )

        if (isWide) {
            Row(modifier = Modifier.weight(1f).fillMaxWidth().imePadding()) {
                Column(
                    modifier = Modifier.weight(1f).verticalScroll(scrollState).padding(20.dp),
                ) {
                    Text(text = stringResource(Res.string.repeat_add_headline), style = EbbingTheme.typography.headingLSB, color = EbbingTheme.colors.black)
                    RepeatCycleInputContent(
                        intervals = state.intervals,
                        onIntervalsChange = { viewModel.onIntent(AddRepeatCycleIntent.OnIntervalsChange(it)) },
                    )
                    Spacer(modifier = Modifier.height(40.dp))
                }
                Column(modifier = Modifier.weight(1f).padding(20.dp)) {
                    Spacer(modifier = Modifier.height(80.dp))
                    PreviewContent(intervals = state.parsedIntervals)
                }
            }
        } else {
            Column(
                modifier = Modifier.weight(1f).verticalScroll(scrollState).padding(20.dp).imePadding(),
            ) {
                Text(text = stringResource(Res.string.repeat_add_headline), style = EbbingTheme.typography.headingLSB, color = EbbingTheme.colors.black)
                RepeatCycleInputContent(
                    intervals = state.intervals,
                    onIntervalsChange = { viewModel.onIntent(AddRepeatCycleIntent.OnIntervalsChange(it)) },
                )
                PreviewContent(intervals = state.parsedIntervals)
                Spacer(modifier = Modifier.height(60.dp))
            }
        }

        if (state.isTreatment) {
            com.tgyuu.shared.designsystem.component.EbbingSolidButton(
                label = stringResource(Res.string.repeat_register_button),
                onClick = { viewModel.onIntent(AddRepeatCycleIntent.OnSaveClick) },
                enabled = state.isSaveEnabled,
                modifier = Modifier.fillMaxWidth().background(EbbingTheme.colors.background).padding(horizontal = 20.dp, vertical = 16.dp),
            )
        }
    }
    } // BoxWithConstraints
}

@Composable
private fun RepeatCycleInputContent(
    intervals: String,
    onIntervalsChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(Res.string.repeat_cycle_label),
            style = EbbingTheme.typography.bodyMSB,
            color = EbbingTheme.colors.black,
            modifier = Modifier.padding(top = 32.dp),
        )

        EbbingTextInputDefault(
            value = intervals,
            hint = stringResource(Res.string.repeat_cycle_input_hint),
            onValueChange = onIntervalsChange,
            limit = 60,
            rightComponent = {
                if (intervals.isNotEmpty()) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_delete_circle),
                        contentDescription = stringResource(Res.string.common_clear),
                        tint = EbbingTheme.colors.dark2,
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .size(20.dp)
                            .clickable { onIntervalsChange("") },
                    )
                }
            },
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth(),
        )

        Text(
            text = stringResource(Res.string.repeat_cycle_input_guide),
            style = EbbingTheme.typography.bodySM,
            color = EbbingTheme.colors.dark2,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .padding(top = 8.dp, start = 8.dp)
                .fillMaxWidth(),
        )
    }
}

@Composable
private fun PreviewContent(
    intervals: List<Int>,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(Res.string.repeat_cycle_preview_label),
            style = EbbingTheme.typography.bodyMSB,
            color = EbbingTheme.colors.black,
            modifier = Modifier.padding(top = 32.dp),
        )

        Text(
            text = intervals.toRepeatPreviewDisplay(),
            style = EbbingTheme.typography.bodySSB,
            color = if (intervals.isEmpty()) EbbingTheme.colors.error
            else EbbingTheme.colors.dark2,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth(),
        )
    }
}
