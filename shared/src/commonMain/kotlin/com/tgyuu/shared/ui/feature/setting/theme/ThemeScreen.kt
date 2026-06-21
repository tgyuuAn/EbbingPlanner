package com.tgyuu.shared.ui.feature.setting.theme

import com.tgyuu.shared.designsystem.foundation.LayoutConstants
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import ebbingplanner.shared.generated.resources.Res
import ebbingplanner.shared.generated.resources.setting_apply
import ebbingplanner.shared.generated.resources.setting_dark
import ebbingplanner.shared.generated.resources.setting_light
import ebbingplanner.shared.generated.resources.setting_preview
import ebbingplanner.shared.generated.resources.setting_theme
import ebbingplanner.shared.generated.resources.theme_select_headline
import org.jetbrains.compose.resources.stringResource

@Composable
fun ThemeScreen(
    viewModel: ThemeViewModel,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
    val isWide = maxWidth > LayoutConstants.TABLET_BREAKPOINT
    Column(modifier = Modifier.fillMaxSize()) {
        EbbingSubTopBar(
            title = stringResource(Res.string.setting_theme),
            onNavigationClick = { viewModel.onIntent(ThemeIntent.OnBackClick) },
            rightComponent = {
                if (!state.isTreatment) {
                Text(
                    text = stringResource(Res.string.setting_apply),
                    style = EbbingTheme.typography.bodyMM,
                    color = if (state.isSaveEnabled) EbbingTheme.colors.primaryDefault
                    else EbbingTheme.colors.light1,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .clickable(enabled = state.isSaveEnabled) {
                            viewModel.onIntent(ThemeIntent.OnUpdateClick)
                        },
                )
                }
            },
            modifier = Modifier.padding(horizontal = 20.dp),
        )

        if (isWide) {
            Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f).verticalScroll(scrollState).padding(20.dp)) {
                    Text(text = stringResource(Res.string.theme_select_headline), style = EbbingTheme.typography.headingLSB, color = EbbingTheme.colors.black)
                    Spacer(modifier = Modifier.height(32.dp))
                    ThemeSelector(selectedTheme = state.selectTheme, onThemeSelected = { viewModel.onIntent(ThemeIntent.OnThemeChange(it)) })
                }
                Column(modifier = Modifier.weight(1f).padding(20.dp)) {
                    Spacer(modifier = Modifier.height(80.dp))
                    ThemePreview(theme = state.selectTheme ?: Theme.NORMAL)
                }
            }
        } else {
            Column(modifier = Modifier.weight(1f).verticalScroll(scrollState).padding(20.dp)) {
                Text(text = stringResource(Res.string.theme_select_headline), style = EbbingTheme.typography.headingLSB, color = EbbingTheme.colors.black)
                Spacer(modifier = Modifier.height(32.dp))
                ThemeSelector(selectedTheme = state.selectTheme, onThemeSelected = { viewModel.onIntent(ThemeIntent.OnThemeChange(it)) })
                Spacer(modifier = Modifier.height(32.dp))
                ThemePreview(theme = state.selectTheme ?: Theme.NORMAL)
            }
        }

        if (state.isTreatment) {
            com.tgyuu.shared.designsystem.component.EbbingSolidButton(
                label = stringResource(Res.string.setting_apply),
                onClick = { viewModel.onIntent(ThemeIntent.OnUpdateClick) },
                enabled = state.isSaveEnabled,
                modifier = Modifier.fillMaxWidth().background(EbbingTheme.colors.background).padding(horizontal = 20.dp, vertical = 16.dp),
            )
        }
    }
    } // BoxWithConstraints
}

@Composable
private fun ThemeSelector(
    selectedTheme: Theme?,
    onThemeSelected: (Theme) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier.fillMaxWidth(),
    ) {
        Theme.entries.forEach { theme ->
            val isSelected = theme == selectedTheme

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.clickable { onThemeSelected(theme) },
            ) {
                Spacer(
                    modifier = Modifier
                        .size(45.dp)
                        .clip(CircleShape)
                        .background(Color(theme.lightBg)),
                )

                androidx.compose.animation.AnimatedVisibility(
                    visible = isSelected,
                    enter = fadeIn(),
                    exit = fadeOut(),
                ) {
                    Text(
                        text = "\u2713",
                        style = EbbingTheme.typography.bodyMSB,
                        color = EbbingTheme.colors.white,
                    )
                }
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
            text = stringResource(Res.string.setting_preview),
            style = EbbingTheme.typography.bodyMM,
            color = EbbingTheme.colors.black,
            modifier = Modifier.padding(bottom = 12.dp),
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            PreviewCard(
                label = stringResource(Res.string.setting_light),
                backgroundColor = Color(theme.lightBg),
                modifier = Modifier.weight(1f),
            )

            PreviewCard(
                label = stringResource(Res.string.setting_dark),
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

