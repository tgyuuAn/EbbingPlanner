package com.tgyuu.home.graph.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.tgyuu.designsystem.R
import com.tgyuu.designsystem.component.EbbingSelectDropdownAddItem
import com.tgyuu.designsystem.component.EbbingSelectDropdownItem
import kotlinx.collections.immutable.ImmutableList
import com.tgyuu.designsystem.model.RepeatCycleUiModel

@Composable
internal fun RepeatCycleContent(
    repeatCycle: RepeatCycleUiModel?,
    onRepeatCycleDropDownClick: () -> Unit,
    modifier: Modifier = Modifier,
    isFromLast: Boolean = false,
    dropdown: SelectDropdownContent? = null,
) {
    FieldLabel(
        label = stringResource(R.string.home_repeat_cycle),
        isFromLast = isFromLast,
        modifier = Modifier.padding(top = 32.dp),
    )

    SelectDropdownField(
        value = repeatCycle?.displayName ?: "",
        onClick = onRepeatCycleDropDownClick,
        dropdown = dropdown,
        modifier = modifier
            .padding(top = 8.dp)
            .fillMaxWidth(),
    )
}

/** 태블릿 반복 주기 드롭다운 항목: 반복 주기 목록 + 새 반복 주기 추가 */
@Composable
internal fun RepeatCycleDropdownItems(
    repeatCycleList: ImmutableList<RepeatCycleUiModel>,
    selectedRepeatCycle: RepeatCycleUiModel?,
    onRepeatCycleClick: (RepeatCycleUiModel) -> Unit,
    onAddRepeatCycleClick: () -> Unit,
) {
    repeatCycleList.forEach { cycle ->
        EbbingSelectDropdownItem(
            label = cycle.displayName,
            selected = cycle.id == selectedRepeatCycle?.id,
            onClick = { onRepeatCycleClick(cycle) },
        )
    }

    EbbingSelectDropdownAddItem(
        label = stringResource(R.string.home_dropdown_add_repeat_cycle),
        onClick = onAddRepeatCycleClick,
    )
}
