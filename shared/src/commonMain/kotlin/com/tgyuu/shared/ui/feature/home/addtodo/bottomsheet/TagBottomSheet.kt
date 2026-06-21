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
import com.tgyuu.shared.ui.model.TodoTagUiModel
import kotlinx.collections.immutable.ImmutableList
import ebbingplanner.shared.generated.resources.Res
import ebbingplanner.shared.generated.resources.home_apply
import ebbingplanner.shared.generated.resources.home_tag
import ebbingplanner.shared.generated.resources.tag_add_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun TagBottomSheetContent(
    tagList: ImmutableList<TodoTagUiModel>,
    selectedTag: TodoTagUiModel?,
    onTagSelected: (TodoTagUiModel) -> Unit,
    onAddTagClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    var newTag by remember(selectedTag) { mutableStateOf(selectedTag) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
    ) {
        EbbingBottomSheetHeader(
            title = stringResource(Res.string.home_tag),
            rightComponent = {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(Res.string.tag_add_title),
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
                .heightIn(max = 300.dp)
                .padding(top = 12.dp),
        ) {
            items(
                items = tagList,
                key = { it.id },
            ) { tag ->
                EbbingBottomSheetListItemDefault(
                    label = tag.name,
                    color = tag.color,
                    checked = tag.id == newTag?.id,
                    onChecked = { newTag = tag },
                )
            }
        }

        EbbingSolidButton(
            label = stringResource(Res.string.home_apply),
            onClick = { newTag?.let { onTagSelected(it) } },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, bottom = 10.dp),
        )
    }
}
