package com.tgyuu.shared.ui.feature.home.bottomsheet

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.tgyuu.shared.domain.model.SortType

@Composable
internal fun SortTypeBottomSheet(
    originSortType: SortType,
    onClickUpdate: (SortType) -> Unit,
) {
    var newSortType by remember(originSortType) { mutableStateOf(originSortType) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
    ) {
        EbbingBottomSheetHeader(title = "정렬 순서")

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, bottom = 8.dp),
        ) {
            SortType.entries.forEach { sortType ->
                EbbingBottomSheetListItemDefault(
                    label = sortType.displayName,
                    checked = sortType == newSortType,
                    onChecked = { newSortType = sortType },
                )
            }

            EbbingSolidButton(
                label = "적용하기",
                onClick = { onClickUpdate(newSortType) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(top = 12.dp, bottom = 10.dp),
            )
        }
    }
}
