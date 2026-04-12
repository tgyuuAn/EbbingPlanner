package com.tgyuu.shared.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.tgyuu.shared.designsystem.foundation.EbbingTheme

@Composable
fun EbbingTextInputDefault(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    hint: String = "",
    limit: Int? = null,
    readOnly: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    onDone: () -> Unit = {},
    onFocusChanged: (Boolean) -> Unit = {},
    rightComponent: @Composable () -> Unit = {},
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
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
            imeAction = ImeAction.Done,
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                keyboardController?.hide()
                onDone()
                focusManager.clearFocus()
            },
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
                            modifier = Modifier.align(Alignment.CenterStart),
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
                else EbbingTheme.colors.light3,
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
                    .background(Color(color)),
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

        Icon(
            imageVector = Icons.Default.KeyboardArrowDown,
            contentDescription = null,
            tint = EbbingTheme.colors.black,
            modifier = Modifier.size(24.dp),
        )
    }
}
