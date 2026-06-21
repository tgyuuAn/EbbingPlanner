package com.tgyuu.shared.ui.feature.setting.widget

import com.tgyuu.shared.designsystem.foundation.LayoutConstants
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt
import com.tgyuu.shared.designsystem.component.EbbingSubTopBar
import com.tgyuu.shared.designsystem.foundation.EbbingTheme
import com.tgyuu.shared.domain.model.Theme
import com.tgyuu.shared.ui.feature.setting.widget.WidgetState
import ebbingplanner.shared.generated.resources.Res
import ebbingplanner.shared.generated.resources.home_save
import ebbingplanner.shared.generated.resources.setting_background_alpha
import ebbingplanner.shared.generated.resources.setting_preview
import ebbingplanner.shared.generated.resources.widget_preview_sample
import ebbingplanner.shared.generated.resources.widget_setting_title
import ebbingplanner.shared.generated.resources.widget_text_alpha
import ebbingplanner.shared.generated.resources.widget_theme
import org.jetbrains.compose.resources.stringResource

@Composable
fun WidgetScreen(
    viewModel: WidgetViewModel,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
    val isWide = maxWidth > LayoutConstants.TABLET_BREAKPOINT
    Column(modifier = Modifier.fillMaxSize()) {
        EbbingSubTopBar(
            title = stringResource(Res.string.widget_setting_title),
            onNavigationClick = { viewModel.onIntent(WidgetIntent.OnBackClick) },
            rightComponent = {
                if (!state.isTreatment) {
                Text(
                    text = stringResource(Res.string.home_save),
                    style = if (state.isSaveEnabled) EbbingTheme.typography.bodyMSB
                    else EbbingTheme.typography.bodyMM,
                    color = if (state.isSaveEnabled) EbbingTheme.colors.primaryDefault
                    else EbbingTheme.colors.dark3,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .clickable(enabled = state.isSaveEnabled) {
                            viewModel.onIntent(WidgetIntent.OnSaveClick)
                        },
                )
                }
            },
            modifier = Modifier.padding(horizontal = 20.dp),
        )

        if (isWide) { Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
        Column(modifier = Modifier.weight(1f).verticalScroll(scrollState).padding(20.dp)) {
            WidgetControlSection(
                state = state,
                onThemeChange = { viewModel.onIntent(WidgetIntent.OnThemeChange(it)) },
                onBgAlphaChange = { viewModel.onIntent(WidgetIntent.OnBackgroundAlphaChange(it)) },
                onTextAlphaChange = { viewModel.onIntent(WidgetIntent.OnTextAlphaChange(it)) },
            )
        }
        if (isWide) {
            Column(modifier = Modifier.weight(1f).padding(20.dp)) {
                Spacer(modifier = Modifier.height(80.dp))
                WidgetPreviewSection(state = state)
            }
        } } // Row end
        } else {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .padding(20.dp),
            ) {
                WidgetControlSection(
                    state = state,
                    onThemeChange = { viewModel.onIntent(WidgetIntent.OnThemeChange(it)) },
                    onBgAlphaChange = { viewModel.onIntent(WidgetIntent.OnBackgroundAlphaChange(it)) },
                    onTextAlphaChange = { viewModel.onIntent(WidgetIntent.OnTextAlphaChange(it)) },
                )
                Spacer(modifier = Modifier.height(32.dp))
                WidgetPreviewSection(state = state)
                Spacer(modifier = Modifier.height(60.dp))
            }
        } // else

        if (state.isTreatment) {
            com.tgyuu.shared.designsystem.component.EbbingSolidButton(
                label = stringResource(Res.string.home_save),
                onClick = { viewModel.onIntent(WidgetIntent.OnSaveClick) },
                enabled = state.isSaveEnabled,
                modifier = Modifier.fillMaxWidth().background(EbbingTheme.colors.background).padding(horizontal = 20.dp, vertical = 16.dp),
            )
        }
    }
    } // BoxWithConstraints
}

@Composable
private fun WidgetControlSection(
    state: WidgetState,
    onThemeChange: (Theme) -> Unit,
    onBgAlphaChange: (Float) -> Unit,
    onTextAlphaChange: (Float) -> Unit,
) {
    Text(text = stringResource(Res.string.widget_theme), style = EbbingTheme.typography.bodyMSB, color = EbbingTheme.colors.black)
    Spacer(modifier = Modifier.height(16.dp))
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
        Theme.entries.forEach { theme ->
            val isSelected = theme == state.selectedTheme
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(theme.lightBg))
                    .then(
                        if (isSelected) Modifier.border(3.dp, EbbingTheme.colors.primaryDefault, CircleShape)
                        else Modifier.border(1.dp, EbbingTheme.colors.light2, CircleShape)
                    )
                    .clickable { onThemeChange(theme) },
            )
        }
    }
    Spacer(modifier = Modifier.height(32.dp))
    Text(text = stringResource(Res.string.setting_background_alpha), style = EbbingTheme.typography.bodyMSB, color = EbbingTheme.colors.black)
    Slider(
        value = state.selectedBackgroundAlpha ?: 1f,
        onValueChange = onBgAlphaChange,
        valueRange = 0f..1f,
        colors = SliderDefaults.colors(thumbColor = EbbingTheme.colors.primaryDefault, activeTrackColor = EbbingTheme.colors.primaryDefault),
        modifier = Modifier.fillMaxWidth(),
    )
    Text(text = "${((state.selectedBackgroundAlpha ?: 1f) * 100).roundToInt()}%", style = EbbingTheme.typography.bodySM, color = EbbingTheme.colors.dark2)
    Spacer(modifier = Modifier.height(24.dp))
    Text(text = stringResource(Res.string.widget_text_alpha), style = EbbingTheme.typography.bodyMSB, color = EbbingTheme.colors.black)
    Slider(
        value = state.selectedTextAlpha ?: 1f,
        onValueChange = onTextAlphaChange,
        valueRange = 0f..1f,
        colors = SliderDefaults.colors(thumbColor = EbbingTheme.colors.primaryDefault, activeTrackColor = EbbingTheme.colors.primaryDefault),
        modifier = Modifier.fillMaxWidth(),
    )
    Text(text = "${((state.selectedTextAlpha ?: 1f) * 100).roundToInt()}%", style = EbbingTheme.typography.bodySM, color = EbbingTheme.colors.dark2)
}

@Composable
private fun WidgetPreviewSection(state: WidgetState) {
    Text(text = stringResource(Res.string.setting_preview), style = EbbingTheme.typography.bodyMSB, color = EbbingTheme.colors.black, modifier = Modifier.padding(bottom = 12.dp))
    val previewTheme = state.selectedTheme ?: Theme.NORMAL
    val bgAlpha = state.selectedBackgroundAlpha ?: 1f
    val textAlpha = state.selectedTextAlpha ?: 1f
    Box(
        modifier = Modifier.fillMaxWidth().height(120.dp).clip(RoundedCornerShape(12.dp)).background(Color(previewTheme.lightBg).copy(alpha = bgAlpha)).border(1.dp, EbbingTheme.colors.light2, RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = stringResource(Res.string.widget_preview_sample), style = EbbingTheme.typography.bodyMM, color = Color.Black.copy(alpha = textAlpha))
    }
}
