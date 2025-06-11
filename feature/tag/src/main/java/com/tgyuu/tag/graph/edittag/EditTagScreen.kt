package com.tgyuu.tag.graph.edittag

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tgyuu.common.ui.clickable
import com.tgyuu.common.ui.throttledClickable
import com.tgyuu.designsystem.BasePreview
import com.tgyuu.designsystem.EbbingPreview
import com.tgyuu.designsystem.R
import com.tgyuu.designsystem.component.EbbingSubTopBar
import com.tgyuu.designsystem.component.EbbingTextInputDefault
import com.tgyuu.designsystem.foundation.EbbingTheme
import com.tgyuu.tag.graph.edittag.contract.EditTagIntent
import com.tgyuu.tag.graph.edittag.contract.EditTagState
import com.tgyuu.tag.ui.bottomsheet.ColorBottomSheet

@Composable
internal fun EditTagRoute(
    viewModel: EditTagViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    EditTagScreen(
        state = state,
        onBackClick = { viewModel.onIntent(EditTagIntent.OnBackClick) },
        onSaveClick = { viewModel.onIntent(EditTagIntent.OnSaveClick) },
        onNameChange = { viewModel.onIntent(EditTagIntent.OnNameChange(it)) },
        onColorDropDownClick = {
            viewModel.onIntent(
                EditTagIntent.OnColorDropDownClick({
                    ColorBottomSheet(
                        originColor = state.colorValue,
                        updateColor = { viewModel.onIntent(EditTagIntent.OnColorChange(it)) }
                    )
                })
            )
        },
    )
}

@Composable
private fun EditTagScreen(
    state: EditTagState,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit,
    onNameChange: (String) -> Unit,
    onColorDropDownClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current

    Column(modifier = modifier.fillMaxSize()) {
        EbbingSubTopBar(
            title = "태그 수정",
            onNavigationClick = onBackClick,
            rightComponent = {
                Text(
                    text = "저장",
                    style = if (state.isSaveEnabled) EbbingTheme.typography.bodyMSB else EbbingTheme.typography.bodyMM,
                    color = if (state.isSaveEnabled) EbbingTheme.colors.primaryDefault else EbbingTheme.colors.dark3,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .throttledClickable(
                            throttleTime = 1500L,
                            enabled = state.isSaveEnabled
                        ) {
                            onSaveClick()
                            focusManager.clearFocus()
                        },
                )
            },
            modifier = Modifier.padding(horizontal = 20.dp),
        )

        Column(
            modifier = Modifier
                .padding(20.dp)
                .imePadding(),
        ) {
            Text(
                text = "${state.originTag?.name} 태그를 수정해요",
                style = EbbingTheme.typography.headingLSB,
                color = EbbingTheme.colors.black,
            )

            NameContent(
                name = state.name,
                onNameChange = onNameChange,
            )

            ColorContent(
                colorValue = state.colorValue,
                onColorDropDownClick = onColorDropDownClick,
            )
        }
    }
}

@Composable
private fun NameContent(
    name: String,
    onNameChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var isInputFocused by remember { mutableStateOf(false) }

    Text(
        text = "태그 이름",
        style = EbbingTheme.typography.bodyMSB,
        color = EbbingTheme.colors.black,
        modifier = Modifier.padding(top = 32.dp),
    )

    EbbingTextInputDefault(
        value = name,
        hint = "태그의 이름은 무엇인가요?",
        keyboardType = KeyboardType.Text,
        onValueChange = onNameChange,
        limit = 20,
        rightComponent = {
            if (name.isNotEmpty()) {
                Image(
                    painter = painterResource(R.drawable.ic_delete_circle),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .size(20.dp)
                        .clickable { onNameChange("") },
                )
            }
        },
        modifier = modifier
            .padding(top = 8.dp)
            .fillMaxWidth()
            .onFocusChanged { isInputFocused = it.isFocused },
    )
}

@Composable
private fun ColorContent(
    colorValue: Int,
    onColorDropDownClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 32.dp)
            .clickable { onColorDropDownClick() },
    ) {
        Text(
            text = "색상",
            style = EbbingTheme.typography.headingSM,
            color = EbbingTheme.colors.black,
        )

        Spacer(
            modifier = Modifier
                .padding(end = 5.dp)
                .size(25.dp)
                .clip(CircleShape)
                .background(Color(colorValue))
        )
    }
}

@EbbingPreview
@Composable
private fun PreviewEditTag() {
    BasePreview {
        EditTagScreen(
            state = EditTagState(colorValue = 0xFFFF6961.toInt()),
            onSaveClick = {},
            onBackClick = {},
            onNameChange = {},
            onColorDropDownClick = {}
        )
    }
}
