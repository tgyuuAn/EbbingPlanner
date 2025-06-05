package com.tgyuu.home.graph.addtodo.ui.bottomsheet

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.tgyuu.common.ui.clickable
import com.tgyuu.designsystem.component.EbbingBottomSheetHeader
import com.tgyuu.designsystem.component.EbbingBottomSheetListItemDefault
import com.tgyuu.designsystem.component.EbbingSolidButton
import com.tgyuu.designsystem.foundation.EbbingTheme
import com.tgyuu.domain.model.RepeatCycle

@Composable
internal fun RepeatCycleBottomSheet(
    repeatCycleList: List<RepeatCycle>,
    originRepeatCycle: RepeatCycle,
    updateRepeatCycle: (RepeatCycle) -> Unit,
    onAddRepeatCycleClick: () -> Unit,
) {
    var newRepeatCycle by remember(originRepeatCycle) { mutableStateOf(originRepeatCycle) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
    ) {
        EbbingBottomSheetHeader(
            title = "반복 주기",
            rightComponent = {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = EbbingTheme.colors.black,
                    modifier = Modifier
                        .size(32.dp)
                        .clickable { onAddRepeatCycleClick() },
                )
            })

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 400.dp)
                .padding(top = 12.dp)
        ) {
            repeatCycleList.forEach { cycle ->
                EbbingBottomSheetListItemDefault(
                    label = cycle.toDisplayName(),
                    checked = cycle == newRepeatCycle,
                    onChecked = { newRepeatCycle = cycle },
                )
            }
        }

        EbbingSolidButton(
            label = "적용하기",
            onClick = { updateRepeatCycle(newRepeatCycle) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, bottom = 10.dp),
        )
    }
}
