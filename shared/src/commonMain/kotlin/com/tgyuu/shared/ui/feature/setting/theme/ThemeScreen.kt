package com.tgyuu.shared.ui.feature.setting.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
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
import com.tgyuu.shared.designsystem.foundation.EbbingTheme
import com.tgyuu.shared.domain.model.Theme

@Composable
fun ThemeScreen(
    viewModel: ThemeViewModel,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()

    Column(modifier = modifier.fillMaxSize()) {
        EbbingSubTopBar(
            title = "테마",
            onNavigationClick = { viewModel.onIntent(ThemeIntent.OnBackClick) },
            rightComponent = {
                Text(
                    text = "적용",
                    style = if (state.isSaveEnabled) EbbingTheme.typography.bodyMSB
                    else EbbingTheme.typography.bodyMM,
                    color = if (state.isSaveEnabled) EbbingTheme.colors.primaryDefault
                    else EbbingTheme.colors.dark3,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .clickable(enabled = state.isSaveEnabled) {
                            viewModel.onIntent(ThemeIntent.OnUpdateClick)
                        },
                )
            },
            modifier = Modifier.padding(horizontal = 20.dp),
        )

        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .padding(20.dp),
        ) {
            Text(
                text = "테마 색상을 선택해요.",
                style = EbbingTheme.typography.headingLSB,
                color = EbbingTheme.colors.black,
            )

            Spacer(modifier = Modifier.height(32.dp))

            ThemeSelector(
                selectedTheme = state.selectTheme,
                onThemeSelected = { viewModel.onIntent(ThemeIntent.OnThemeChange(it)) },
            )

            Spacer(modifier = Modifier.height(32.dp))

            ThemePreview(theme = state.selectTheme ?: Theme.NORMAL)
        }
    }
}

@Composable
private fun ThemeSelector(
    selectedTheme: Theme?,
    onThemeSelected: (Theme) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier.fillMaxWidth(),
    ) {
        Theme.entries.forEach { theme ->
            val isSelected = theme == selectedTheme

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { onThemeSelected(theme) },
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(theme.lightBg))
                        .then(
                            if (isSelected) Modifier.border(3.dp, EbbingTheme.colors.primaryDefault, CircleShape)
                            else Modifier.border(1.dp, EbbingTheme.colors.light2, CircleShape)
                        ),
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = theme.displayName(),
                    style = if (isSelected) EbbingTheme.typography.bodySSB
                    else EbbingTheme.typography.bodySM,
                    color = if (isSelected) EbbingTheme.colors.primaryDefault
                    else EbbingTheme.colors.dark2,
                )
            }
        }
    }
}

@Composable
private fun ThemePreview(
    theme: Theme,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = "미리보기",
            style = EbbingTheme.typography.bodyMSB,
            color = EbbingTheme.colors.black,
            modifier = Modifier.padding(bottom = 12.dp),
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            PreviewCard(
                label = "라이트",
                backgroundColor = Color(theme.lightBg),
                modifier = Modifier.weight(1f),
            )

            PreviewCard(
                label = "다크",
                backgroundColor = Color(theme.darkBg),
                textColor = Color.White,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun PreviewCard(
    label: String,
    backgroundColor: Color,
    modifier: Modifier = Modifier,
    textColor: Color = Color.Black,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .height(120.dp)
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .border(
                1.dp,
                EbbingTheme.colors.light2,
                androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
            ),
    ) {
        Text(
            text = label,
            style = EbbingTheme.typography.bodyMM,
            color = textColor,
        )
    }
}

private fun Theme.displayName(): String = when (this) {
    Theme.NORMAL -> "기본"
    Theme.FOREST -> "숲"
    Theme.SUNSET -> "노을"
    Theme.MARINE -> "바다"
    Theme.LILAC -> "라일락"
}
