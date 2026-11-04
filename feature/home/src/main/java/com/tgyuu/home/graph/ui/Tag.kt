package com.tgyuu.home.graph.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.tgyuu.designsystem.R
import com.tgyuu.designsystem.component.EbbingSelectDropdownAddItem
import com.tgyuu.designsystem.component.EbbingSelectDropdownItem
import com.tgyuu.designsystem.foundation.EbbingTheme
import com.tgyuu.designsystem.model.TodoTagUiModel
import kotlinx.collections.immutable.ImmutableList

@Composable
internal fun TagContent(
    tag: TodoTagUiModel?,
    onTagDropDownClick: () -> Unit,
    modifier: Modifier = Modifier,
    dropdown: SelectDropdownContent? = null,
) {
    Text(
        text = stringResource(R.string.home_tag),
        style = EbbingTheme.typography.heading14SB,
        color = EbbingTheme.colors.textOnBackground,
        modifier = Modifier.padding(top = 32.dp),
    )

    SelectDropdownField(
        value = tag?.name ?: "",
        color = tag?.color,
        onClick = onTagDropDownClick,
        dropdown = dropdown,
        modifier = modifier
            .padding(top = 8.dp)
            .fillMaxWidth(),
    )
}

/** 태블릿 태그 드롭다운 항목: 태그 목록 + 새 태그 추가 */
@Composable
internal fun TagDropdownItems(
    tagList: ImmutableList<TodoTagUiModel>,
    selectedTag: TodoTagUiModel?,
    onTagClick: (TodoTagUiModel) -> Unit,
    onAddTagClick: () -> Unit,
) {
    tagList.forEach { tag ->
        EbbingSelectDropdownItem(
            label = tag.name,
            color = tag.color,
            selected = tag.id == selectedTag?.id,
            onClick = { onTagClick(tag) },
        )
    }

    EbbingSelectDropdownAddItem(
        label = stringResource(R.string.home_dropdown_add_tag),
        onClick = onAddTagClick,
    )
}
