package com.tgyuu.shared.ui.feature.tag.edittag

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.tgyuu.shared.designsystem.component.EbbingSubTopBar
import com.tgyuu.shared.designsystem.component.EbbingTextInputDefault
import com.tgyuu.shared.designsystem.foundation.EbbingTheme

@Composable
fun EditTagScreen(
    viewModel: EditTagViewModel,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = modifier.fillMaxSize(),
    ) {
        EbbingSubTopBar(
            title = "태그 수정",
            onNavigationClick = { viewModel.onIntent(EditTagIntent.OnBackClick) },
            rightComponent = {
                Text(
                    text = "저장",
                    style = EbbingTheme.typography.headingSSB,
                    color = if (state.isSaveEnabled) EbbingTheme.colors.primaryDefault
                    else EbbingTheme.colors.dark3,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .clickable(enabled = state.isSaveEnabled) {
                            viewModel.onIntent(EditTagIntent.OnUpdateClick)
                        },
                )
            },
            modifier = Modifier.padding(horizontal = 20.dp),
        )

        HorizontalDivider(
            color = EbbingTheme.colors.light2,
            thickness = 1.dp,
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .imePadding(),
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Name Input
            NameContent(
                name = state.name,
                onNameChange = { viewModel.onIntent(EditTagIntent.OnNameChange(it)) },
                onClearClick = { viewModel.onIntent(EditTagIntent.OnNameChange("")) },
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Color Selection
            ColorContent(
                colorValue = state.colorValue,
                onColorClick = { viewModel.onIntent(EditTagIntent.OnColorDropDownClick) },
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
            text = "이름",
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
                hint = "태그 이름을 입력하세요",
                modifier = Modifier.weight(1f),
            )

            if (name.isNotEmpty()) {
                Icon(
                    imageVector = Icons.Filled.Clear,
                    contentDescription = "지우기",
                    tint = EbbingTheme.colors.dark2,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .size(20.dp)
                        .clickable { onClearClick() },
                )
            }
        }

        Text(
            text = "${name.length}/20",
            style = EbbingTheme.typography.bodySR,
            color = EbbingTheme.colors.dark3,
            modifier = Modifier
                .align(Alignment.End)
                .padding(top = 4.dp),
        )
    }
}

@Composable
private fun ColorContent(
    colorValue: Int,
    onColorClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = "색상",
            style = EbbingTheme.typography.bodyMSB,
            color = EbbingTheme.colors.black,
            modifier = Modifier.padding(bottom = 8.dp),
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onColorClick() }
                .padding(vertical = 12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(25.dp)
                    .clip(CircleShape)
                    .background(Color(colorValue)),
            )

            Spacer(modifier = Modifier.weight(1f))

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = EbbingTheme.colors.dark3,
            )
        }
    }
}
