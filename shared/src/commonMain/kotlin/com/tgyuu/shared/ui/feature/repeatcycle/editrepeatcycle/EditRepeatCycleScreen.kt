package com.tgyuu.shared.ui.feature.repeatcycle.editrepeatcycle

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.ui.platform.LocalFocusManager
import com.tgyuu.shared.ui.feature.repeatcycle.addrepeatcycle.RepeatCycleInputContent
import com.tgyuu.shared.ui.feature.repeatcycle.addrepeatcycle.RepeatCyclePreviewContent
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
import ebbingplanner.shared.generated.resources.repeat_edit_button
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
fun EditRepeatCycleScreen(
    viewModel: EditRepeatCycleViewModel,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current
    val isTablet = EbbingTheme.windowLayout.isTablet
    val onIntervalsChange: (String) -> Unit = { viewModel.onIntent(EditRepeatCycleIntent.OnIntervalsChange(it)) }
    val onSaveClick: () -> Unit = {
        viewModel.onIntent(EditRepeatCycleIntent.OnUpdateClick)
        focusManager.clearFocus()
    }

    if (!isTablet) {
        Column(modifier = modifier.fillMaxSize()) {
            EbbingSubTopBar(
                title = stringResource(Res.string.repeat_edit_title),
                onNavigationClick = { viewModel.onIntent(EditRepeatCycleIntent.OnBackClick) },
                rightComponent = {},
                modifier = Modifier.padding(horizontal = 20.dp),
            )

            Column(
                modifier = Modifier.weight(1f).verticalScroll(scrollState).padding(20.dp).imePadding(),
            ) {
                Text(text = stringResource(Res.string.repeat_edit_headline), style = EbbingTheme.typography.headingLSB, color = EbbingTheme.colors.textOnBackground)
                RepeatCycleInputContent(intervals = state.intervals, onIntervalsChange = onIntervalsChange)
                RepeatCyclePreviewContent(intervals = state.parsedIntervals)
                Spacer(modifier = Modifier.height(60.dp))
            }

            com.tgyuu.shared.designsystem.component.EbbingSolidButton(
                label = stringResource(Res.string.repeat_edit_button),
                onClick = onSaveClick,
                enabled = state.isSaveEnabled,
                modifier = Modifier.fillMaxWidth().background(EbbingTheme.colors.background).padding(horizontal = 20.dp, vertical = 16.dp),
            )
        }
    } else {
        // Android EditRepeatCycleScreen 태블릿 분기와 동일: 바깥 가로 20 + 컬럼 20+20(콘텐츠 60), 버튼 40
        Column(modifier = modifier.fillMaxSize().padding(horizontal = 20.dp)) {
            EbbingSubTopBar(
                title = stringResource(Res.string.repeat_edit_title),
                onNavigationClick = { viewModel.onIntent(EditRepeatCycleIntent.OnBackClick) },
                rightComponent = {},
            )

            Row(modifier = Modifier.weight(1f).fillMaxWidth().imePadding()) {
                Column(modifier = Modifier.weight(1f).padding(20.dp).padding(horizontal = 20.dp)) {
                    Text(text = stringResource(Res.string.repeat_edit_headline), style = EbbingTheme.typography.headingLSB, color = EbbingTheme.colors.textOnBackground)
                    RepeatCycleInputContent(intervals = state.intervals, onIntervalsChange = onIntervalsChange)
                }
                Column(modifier = Modifier.weight(1f).padding(20.dp).padding(horizontal = 20.dp)) {
                    RepeatCyclePreviewContent(intervals = state.parsedIntervals)
                }
            }

            com.tgyuu.shared.designsystem.component.EbbingSolidButton(
                label = stringResource(Res.string.repeat_edit_button),
                onClick = onSaveClick,
                enabled = state.isSaveEnabled,
                modifier = Modifier.fillMaxWidth().background(EbbingTheme.colors.background).padding(horizontal = 20.dp, vertical = 16.dp),
            )
        }
    }
}
