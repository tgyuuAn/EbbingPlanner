package com.tgyuu.designsystem.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.tgyuu.common.ui.clickable
import com.tgyuu.designsystem.BasePreview
import com.tgyuu.designsystem.EbbingPreview
import com.tgyuu.designsystem.R
import com.tgyuu.designsystem.foundation.EbbingTheme

@Composable
fun EbbingTextInputDefault(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    hint: String = "",
    limit: Int? = null,
    readOnly: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    throttleTime: Long = 2000L,
    onDone: () -> Unit = {},
    onFocusChanged: (Boolean) -> Unit = {},
    rightComponent: @Composable () -> Unit = {},
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    var lastDoneTime by remember { mutableLongStateOf(0L) }
    var isFocused by remember { mutableStateOf(false) }

    BasicTextField(
        value = value,
        onValueChange = { input ->
            limit?.let { if (input.length <= limit) onValueChange(input) } ?: onValueChange(input)
        },
        singleLine = true,
        readOnly = readOnly,
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastDoneTime >= throttleTime) {
                    keyboardController?.hide()
                    onDone()
                    focusManager.moveFocus(FocusDirection.Down)
                    lastDoneTime = currentTime
                }
            }
        ),
        textStyle = EbbingTheme.typography.bodyMM.copy(color = EbbingTheme.colors.black),
        cursorBrush = SolidColor(EbbingTheme.colors.black),
        decorationBox = { innerTextField ->
            Row {
                Box(modifier = Modifier.weight(1f)) {
                    if (value.isEmpty() && !isFocused) {
                        Text(
                            text = hint,
                            style = EbbingTheme.typography.bodyMM,
                            color = EbbingTheme.colors.dark2,
                            modifier = Modifier.align(Alignment.CenterStart)
                        )
                    }

                    innerTextField()
                }

                rightComponent()
            }
        },
        modifier = modifier
            .height(52.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (readOnly) EbbingTheme.colors.light2
                else EbbingTheme.colors.light3
            )
            .padding(horizontal = 16.dp, vertical = 14.dp)
            .onFocusChanged { focusState ->
                isFocused = focusState.isFocused
                onFocusChanged(focusState.isFocused)
            },
    )
}

@Composable
fun EbbingTextInputDropDown(
    value: String,
    onDropDownClick: () -> Unit,
    modifier: Modifier = Modifier,
    hint: String = "",
    color: Int? = null,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .height(52.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(EbbingTheme.colors.light3)
            .padding(horizontal = 16.dp, vertical = 14.dp)
            .clickable { onDropDownClick() },
    ) {
        if (color != null) {
            Spacer(
                modifier = Modifier
                    .padding(end = 10.dp)
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(Color(color))
            )
        }

        Text(
            text = value.ifEmpty { hint },
            style = EbbingTheme.typography.bodyMM,
            color = if (value.isEmpty()) EbbingTheme.colors.dark2 else EbbingTheme.colors.black,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )

        Image(
            painter = painterResource(R.drawable.ic_arrow_down),
            contentDescription = null,
            colorFilter = ColorFilter.tint(EbbingTheme.colors.black),
            modifier = Modifier.size(24.dp),
        )
    }
}

@EbbingPreview
@Composable
private fun PreviewTextInput() {
    BasePreview {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            EbbingTextInputDefault(
                value = "Label",
                onValueChange = {},
                hint = "hint",
                rightComponent = {
                    Image(
                        painter = painterResource(R.drawable.ic_delete_circle),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            )

            EbbingTextInputDefault(
                value = "",
                onValueChange = {},
                hint = "hint",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            )
        }
    }
}

@EbbingPreview
@Composable
private fun Preview2() {
    BasePreview {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            EbbingTextInputDropDown(
                value = "",
                hint = "안내 문구",
                onDropDownClick = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            )

            EbbingTextInputDropDown(
                value = "Label",
                onDropDownClick = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            )
        }
    }
}
