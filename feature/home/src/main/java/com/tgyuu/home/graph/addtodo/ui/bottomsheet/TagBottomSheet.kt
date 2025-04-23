package com.tgyuu.home.graph.addtodo.ui.bottomsheet

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
import com.tgyuu.common.ui.clickable
import com.tgyuu.designsystem.component.EbbingBottomSheetHeader
import com.tgyuu.designsystem.component.EbbingBottomSheetListItemDefault
import com.tgyuu.designsystem.component.EbbingSolidButton
import com.tgyuu.designsystem.foundation.EbbingTheme
import com.tgyuu.domain.model.TodoTag

@Composable
internal fun TagBottomSheet(
    originTag: TodoTag,
    tagList: List<TodoTag>,
    updateTag: (TodoTag) -> Unit,
    onAddTagClick: () -> Unit,
) {
    var newTag by remember(originTag) { mutableStateOf(originTag) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
    ) {
        EbbingBottomSheetHeader(
            title = "태그",
            rightComponent = {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = EbbingTheme.colors.black,
                    modifier = Modifier
                        .size(32.dp)
                        .clickable { onAddTagClick() },
                )
            }
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 400.dp)
                .padding(top = 12.dp),
        ) {
            items(
                items = tagList,
                key = { it.id },
            ) { tag ->
                EbbingBottomSheetListItemDefault(
                    label = tag.name,
                    color = tag.color,
                    checked = tag == newTag,
                    onChecked = { newTag = tag },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        EbbingSolidButton(
            label = "적용하기",
            onClick = { updateTag(newTag) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, bottom = 10.dp),
        )
    }
}
