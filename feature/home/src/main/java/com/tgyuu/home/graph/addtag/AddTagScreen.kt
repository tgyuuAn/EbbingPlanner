package com.tgyuu.home.graph.addtag

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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tgyuu.common.ui.EbbingVisibleAnimation
import com.tgyuu.common.ui.clickable
import com.tgyuu.common.ui.throttledClickable
import com.tgyuu.designsystem.R
import com.tgyuu.designsystem.component.BasePreview
import com.tgyuu.designsystem.component.EbbingSubTopBar
import com.tgyuu.designsystem.component.EbbingTextInputDefault
import com.tgyuu.designsystem.foundation.EbbingTheme
import com.tgyuu.home.graph.InputState
import com.tgyuu.home.graph.addtag.contract.AddTagIntent
import com.tgyuu.home.graph.addtag.ui.bottomsheet.ColorBottomSheet

@Composable
internal fun AddTagRoute(
    viewModel: AddTagViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    AddTagScreen(
        isSaveEnabled = state.isSaveEnabled,
        name = state.name,
        nameInputState = state.nameInputState,
        colorValue = state.colorValue,
        onBackClick = { viewModel.onIntent(AddTagIntent.OnBackClick) },
        onSaveClick = { viewModel.onIntent(AddTagIntent.OnSaveClick) },
        onNameChange = { viewModel.onIntent(AddTagIntent.OnNameChange(it)) },
        onColorDropDownClick = {
            viewModel.onIntent(
                AddTagIntent.OnColorDropDownClick({
                    ColorBottomSheet(
                        originColor = state.colorValue,
                        updateColor = { viewModel.onIntent(AddTagIntent.OnColorChange(it)) }
                    )
                })
            )
        },
    )
}

@Composable
private fun AddTagScreen(
    isSaveEnabled: Boolean,
    name: String,
    nameInputState: InputState,
    colorValue: Int,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit,
    onNameChange: (String) -> Unit,
    onColorDropDownClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current

    Column(modifier = modifier.fillMaxSize()) {
        EbbingSubTopBar(
            title = "태그 추가",
            onNavigationClick = onBackClick,
            rightComponent = {
                Text(
                    text = "저장",
                    style = if (isSaveEnabled) EbbingTheme.typography.bodyMSB else EbbingTheme.typography.bodyMM,
                    color = if (isSaveEnabled) EbbingTheme.colors.primaryDefault else EbbingTheme.colors.dark3,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .throttledClickable(
                        throttleTime = 1500L,
                        enabled = isSaveEnabled
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
                text = "새로운 태그를 추가해요",
                style = EbbingTheme.typography.headingLSB,
                color = EbbingTheme.colors.black,
            )

            NameContent(
                name = name,
                nameInputState = nameInputState,
                onNameChange = onNameChange,
            )

            ColorContent(
                colorValue = colorValue,
                onColorDropDownClick = onColorDropDownClick,
            )
        }
    }
}

@Composable
private fun NameContent(
    name: String,
    nameInputState: InputState,
    onNameChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var isInputFocused by remember { mutableStateOf(false) }
    val isSaveFailed = nameInputState == InputState.WARNING

    Text(
        text = "태그 이름",
        style = EbbingTheme.typography.bodyMSB,
        color = EbbingTheme.colors.dark1,
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

    EbbingVisibleAnimation(visible = isSaveFailed) {
        Text(
            text = "필수 항목을 입력해 주세요.",
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = EbbingTheme.typography.bodySM,
            color = EbbingTheme.colors.error,
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth(),
        )
    }
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
            color = EbbingTheme.colors.dark1,
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

@Preview
@Composable
private fun PreviewAddTag() {
    BasePreview {
        AddTagScreen(
            isSaveEnabled = true,
            name = "",
            nameInputState = InputState.WARNING,
            colorValue = 0xFFFF6961.toInt(),
            onSaveClick = {},
            onBackClick = {},
            onNameChange = {},
            onColorDropDownClick = {}
        )
    }
}
