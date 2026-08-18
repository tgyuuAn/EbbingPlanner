package com.tgyuu.shared.ui.feature.tag.edittag

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.tgyuu.shared.designsystem.component.EbbingSubTopBar
import com.tgyuu.shared.designsystem.component.EbbingTextInputDefault
import com.tgyuu.shared.designsystem.component.bottomsheet.EbbingModalBottomSheet
import com.tgyuu.shared.designsystem.component.bottomsheet.rememberEbbingBottomSheetState
import com.tgyuu.shared.designsystem.foundation.EbbingTheme
import com.tgyuu.shared.ui.feature.tag.bottomsheet.ColorBottomSheet
import kotlinx.coroutines.launch
import ebbingplanner.shared.generated.resources.Res
import ebbingplanner.shared.generated.resources.tag_manage_title
import ebbingplanner.shared.generated.resources.tag_add_button
import ebbingplanner.shared.generated.resources.tag_empty_message
import ebbingplanner.shared.generated.resources.tag_delete
import ebbingplanner.shared.generated.resources.tag_edit
import ebbingplanner.shared.generated.resources.tag_back
import ebbingplanner.shared.generated.resources.tag_delete_confirm_prefix
import ebbingplanner.shared.generated.resources.tag_delete_confirm_highlight
import ebbingplanner.shared.generated.resources.tag_delete_confirm_suffix
import ebbingplanner.shared.generated.resources.tag_delete_confirm_subtext
import ebbingplanner.shared.generated.resources.tag_add_title
import ebbingplanner.shared.generated.resources.tag_edit_title
import ebbingplanner.shared.generated.resources.tag_edit_button
import ebbingplanner.shared.generated.resources.tag_add_headline
import ebbingplanner.shared.generated.resources.tag_edit_headline
import ebbingplanner.shared.generated.resources.tag_name_label
import ebbingplanner.shared.generated.resources.tag_name_hint
import ebbingplanner.shared.generated.resources.tag_color
import ebbingplanner.shared.generated.resources.tag_color_select_title
import ebbingplanner.shared.generated.resources.tag_apply
import ebbingplanner.shared.generated.resources.common_clear
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.painterResource
import ebbingplanner.shared.generated.resources.ic_delete_circle
import ebbingplanner.shared.generated.resources.ic_arrow_right

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTagScreen(
    viewModel: EditTagViewModel,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsState()
    val scope = rememberCoroutineScope()
    val bottomSheetState = rememberEbbingBottomSheetState()

    EbbingModalBottomSheet(
        sheetState = bottomSheetState,
        onDismissRequest = { scope.launch { bottomSheetState.hide() } },
    )

    Column(
        modifier = modifier.fillMaxSize(),
    ) {
        EbbingSubTopBar(
            title = stringResource(Res.string.tag_edit_title),
            onNavigationClick = { viewModel.onIntent(EditTagIntent.OnBackClick) },
            rightComponent = {
                if (false) { // Android 정렬: 상단 저장 링크 미사용
                Text(
                    text = stringResource(Res.string.tag_edit_button),
                    style = EbbingTheme.typography.headingSSB,
                    color = if (state.isSaveEnabled) EbbingTheme.colors.primaryDefault
                    else EbbingTheme.colors.dark3,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .clickable(enabled = state.isSaveEnabled) {
                            viewModel.onIntent(EditTagIntent.OnUpdateClick)
                        },
                )
                }
            },
            modifier = Modifier.padding(horizontal = 20.dp),
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .imePadding(),
        ) {
            Text(
                text = stringResource(Res.string.tag_edit_headline, state.originTag?.name ?: ""),
                style = EbbingTheme.typography.headingLSB,
                color = EbbingTheme.colors.black,
                modifier = Modifier.padding(top = 12.dp, bottom = 32.dp),
            )

            NameContent(
                name = state.name,
                onNameChange = { viewModel.onIntent(EditTagIntent.OnNameChange(it)) },
                onClearClick = { viewModel.onIntent(EditTagIntent.OnNameChange("")) },
            )

            Spacer(modifier = Modifier.height(24.dp))

            ColorContent(
                colorValue = state.colorValue,
                onColorClick = {
                    scope.launch {
                        bottomSheetState.setBottomSheetContent {
                            ColorBottomSheet(
                                currentColor = state.colorValue,
                                onColorSelect = { color ->
                                    viewModel.onIntent(EditTagIntent.OnColorChange(color))
                                },
                                onDismiss = { scope.launch { bottomSheetState.hide() } },
                            )
                        }
                        bottomSheetState.show()
                    }
                },
            )
        }

        if (true) { // Android 정렬: 항상 하단 저장 버튼
            com.tgyuu.shared.designsystem.component.EbbingSolidButton(
                label = stringResource(Res.string.tag_edit_button),
                onClick = { viewModel.onIntent(EditTagIntent.OnUpdateClick) },
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
private fun NameContent(
    name: String,
    onNameChange: (String) -> Unit,
    onClearClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(Res.string.tag_name_label),
            style = EbbingTheme.typography.bodyMSB,
            color = EbbingTheme.colors.black,
            modifier = Modifier.padding(bottom = 8.dp),
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            EbbingTextInputDefault(
                value = name,
                onValueChange = onNameChange,
                hint = stringResource(Res.string.tag_name_hint),
                limit = 20,
                modifier = Modifier.weight(1f),
            )

            if (name.isNotEmpty()) {
                Icon(
                    painter = painterResource(Res.drawable.ic_delete_circle),
                    contentDescription = stringResource(Res.string.common_clear),
                    tint = EbbingTheme.colors.dark2,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .size(20.dp)
                        .clickable { onClearClick() },
                )
            }
        }
        // Android와 동일: 별도 글자수 카운터 없음 (limit=20으로 입력 제한)
    }
}

@Composable
private fun ColorContent(
    colorValue: Int,
    onColorClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Android ColorContent와 동일: 한 행에 라벨(좌) + 색상 원(우), 화살표 없음
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 32.dp)
            .clickable { onColorClick() },
    ) {
        Text(
            text = stringResource(Res.string.tag_color),
            style = EbbingTheme.typography.bodyMSB,
            color = EbbingTheme.colors.black,
        )

        Spacer(
            modifier = Modifier
                .padding(end = 5.dp)
                .size(25.dp)
                .clip(CircleShape)
                .background(Color(colorValue)),
        )
    }
}
