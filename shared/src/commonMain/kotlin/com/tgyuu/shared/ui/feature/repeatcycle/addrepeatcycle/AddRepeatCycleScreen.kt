package com.tgyuu.shared.ui.feature.repeatcycle.addrepeatcycle

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
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

@Composable
fun AddRepeatCycleScreen(
    viewModel: AddRepeatCycleViewModel,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()

    Column(modifier = modifier.fillMaxSize()) {
        EbbingSubTopBar(
            title = "반복 주기 추가",
            onNavigationClick = { viewModel.onIntent(AddRepeatCycleIntent.OnBackClick) },
            rightComponent = {
                if (!state.isTreatment) {
                Text(
                    text = "저장",
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

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
                .padding(20.dp)
                .imePadding(),
        ) {
            Text(
                text = "나만의 반복 주기를 추가해요.",
                style = EbbingTheme.typography.headingLSB,
                color = EbbingTheme.colors.black,
            )

            RepeatCycleInputContent(
                intervals = state.intervals,
                onIntervalsChange = { viewModel.onIntent(AddRepeatCycleIntent.OnIntervalsChange(it)) },
            )

            PreviewContent(preview = state.previewRepeatCycle)

            Spacer(modifier = Modifier.height(60.dp))
        }

        if (state.isTreatment) {
            com.tgyuu.shared.designsystem.component.EbbingSolidButton(
                label = "저장",
                onClick = { viewModel.onIntent(AddRepeatCycleIntent.OnSaveClick) },
                enabled = state.isSaveEnabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(EbbingTheme.colors.background)
                    .padding(horizontal = 20.dp, vertical = 16.dp),
            )
        }
    }
}

@Composable
private fun RepeatCycleInputContent(
    intervals: String,
    onIntervalsChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = "반복 주기",
            style = EbbingTheme.typography.bodyMSB,
            color = EbbingTheme.colors.black,
            modifier = Modifier.padding(top = 32.dp),
        )

        EbbingTextInputDefault(
            value = intervals,
            hint = "어떤 주기로 일정을 반복할까요?",
            onValueChange = onIntervalsChange,
            limit = 60,
            rightComponent = {
                if (intervals.isNotEmpty()) {
                    Icon(
                        imageVector = Icons.Filled.Clear,
                        contentDescription = "지우기",
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
            text = "' , '를 기준으로 숫자를 분리해주세요\n당일을 포함하려면 0을 기입해주세요\n1000 미만의 숫자만 입력하실 수 있습니다.\n ex) 0, 1, 3, 7, 15",
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
    preview: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = "예상 반복 주기",
            style = EbbingTheme.typography.bodyMSB,
            color = EbbingTheme.colors.black,
            modifier = Modifier.padding(top = 32.dp),
        )

        Text(
            text = preview,
            style = EbbingTheme.typography.bodySSB,
            color = if (preview == RepeatCycle.DISPLAY_ERROR) EbbingTheme.colors.error
            else EbbingTheme.colors.dark2,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth(),
        )
    }
}
