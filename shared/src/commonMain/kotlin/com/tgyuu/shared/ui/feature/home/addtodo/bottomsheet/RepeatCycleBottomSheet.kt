package com.tgyuu.shared.ui.feature.home.addtodo.bottomsheet

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tgyuu.shared.designsystem.component.EbbingSolidButton
import com.tgyuu.shared.designsystem.component.bottomsheet.EbbingBottomSheetHeader
import com.tgyuu.shared.designsystem.component.bottomsheet.EbbingBottomSheetListItemDefault
import com.tgyuu.shared.designsystem.foundation.EbbingTheme
import com.tgyuu.shared.ui.model.RepeatCycleUiModel
import kotlinx.collections.immutable.ImmutableList

@Composable
fun RepeatCycleBottomSheetContent(
    repeatCycleList: ImmutableList<RepeatCycleUiModel>,
    selectedRepeatCycle: RepeatCycleUiModel?,
    onRepeatCycleSelected: (RepeatCycleUiModel) -> Unit,
    onAddRepeatCycleClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    var newRepeatCycle by remember(selectedRepeatCycle) { mutableStateOf(selectedRepeatCycle) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
    ) {
        EbbingBottomSheetHeader(
            title = "반복 주기",
            rightComponent = {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "반복 주기 추가",
                    tint = EbbingTheme.colors.black,
                    modifier = Modifier
                        .size(32.dp)
                        .clickable { onAddRepeatCycleClick() },
                )
            }
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 300.dp)
                .padding(top = 12.dp),
        ) {
            items(
                items = repeatCycleList,
                key = { it.id },
            ) { cycle ->
                EbbingBottomSheetListItemDefault(
                    label = cycle.displayName,
                    checked = cycle.id == newRepeatCycle?.id,
                    onChecked = { newRepeatCycle = cycle },
                )
            }
        }

        EbbingSolidButton(
            label = "적용하기",
            onClick = { newRepeatCycle?.let { onRepeatCycleSelected(it) } },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, bottom = 10.dp),
        )
    }
}
