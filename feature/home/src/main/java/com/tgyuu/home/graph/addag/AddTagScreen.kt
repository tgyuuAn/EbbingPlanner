package com.tgyuu.home.graph.addag

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tgyuu.common.ui.EbbingVisibleAnimation
import com.tgyuu.common.ui.throttledClickable
import com.tgyuu.designsystem.R
import com.tgyuu.designsystem.component.BasePreview
import com.tgyuu.designsystem.component.EbbingSubTopBar
import com.tgyuu.designsystem.component.EbbingTextInputDefault
import com.tgyuu.designsystem.foundation.EbbingTheme
import com.tgyuu.home.graph.addtodo.ui.InputState

@Composable
internal fun AddTagRoute(
    viewModel: AddTagViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    AddTagScreen(
        isSaveEnabled = true,
        onBackClick = {},
        onSaveClick = {},
    )
}

@Composable
private fun AddTagScreen(
    isSaveEnabled: Boolean,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit,
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
                    modifier = Modifier.throttledClickable(
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
                text = "새로운 태그를 추가해요.",
                style = EbbingTheme.typography.headingLSB,
                color = EbbingTheme.colors.black,
            )

            NameContent(
                name = "",
                nameInputState = InputState.DEFAULT,
                onNameChange = {},
            )

            PreviewContent()
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
    color: Int,
    onColorChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Text(
        text = "태그 색상",
        style = EbbingTheme.typography.bodyMSB,
        color = EbbingTheme.colors.dark1,
        modifier = Modifier.padding(top = 32.dp),
    )
}

@Composable
private fun PreviewContent() {

}

@Preview
@Composable
private fun PreviewAddTag() {
    BasePreview {
        AddTagScreen(
            isSaveEnabled = true,
            onSaveClick = {},
            onBackClick = {},
        )
    }
}
