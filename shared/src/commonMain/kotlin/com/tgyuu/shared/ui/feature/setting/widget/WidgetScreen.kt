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
import ebbingplanner.shared.generated.resources.setting_apply
import ebbingplanner.shared.generated.resources.setting_background_alpha
import ebbingplanner.shared.generated.resources.setting_preview
import ebbingplanner.shared.generated.resources.widget_preview_sample
import ebbingplanner.shared.generated.resources.setting_widget_theme_change
import ebbingplanner.shared.generated.resources.setting_content_alpha
import ebbingplanner.shared.generated.resources.setting_change_widget_theme
import ebbingplanner.shared.generated.resources.widget_theme
import org.jetbrains.compose.resources.stringResource
import ebbingplanner.shared.generated.resources.setting_no_schedule_today
import ebbingplanner.shared.generated.resources.setting_widget_today_todo
import ebbingplanner.shared.generated.resources.ic_plus
import org.jetbrains.compose.resources.painterResource

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
            title = stringResource(Res.string.setting_widget_theme_change),
            onNavigationClick = { viewModel.onIntent(WidgetIntent.OnBackClick) },
            rightComponent = {},
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
                // Android와 동일: 미리보기 먼저 → 컨트롤 → 말미 Spacer(60)
                WidgetPreviewSection(state = state)
                Spacer(modifier = Modifier.height(32.dp))
                WidgetControlSection(
                    state = state,
                    onThemeChange = { viewModel.onIntent(WidgetIntent.OnThemeChange(it)) },
                    onBgAlphaChange = { viewModel.onIntent(WidgetIntent.OnBackgroundAlphaChange(it)) },
                    onTextAlphaChange = { viewModel.onIntent(WidgetIntent.OnTextAlphaChange(it)) },
                )
                Spacer(modifier = Modifier.height(60.dp))
            }
        } // else

        // Android와 동일: 항상 하단 풀폭 '적용' 버튼
        com.tgyuu.shared.designsystem.component.EbbingSolidButton(
            label = stringResource(Res.string.setting_apply),
            onClick = { viewModel.onIntent(WidgetIntent.OnSaveClick) },
            enabled = state.isSaveEnabled,
            modifier = Modifier.fillMaxWidth().background(EbbingTheme.colors.background).padding(horizontal = 20.dp, vertical = 16.dp),
        )
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
    Text(text = stringResource(Res.string.setting_change_widget_theme), style = EbbingTheme.typography.headingLSB, color = EbbingTheme.colors.black)
    Spacer(modifier = Modifier.height(32.dp))
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
    Text(text = stringResource(Res.string.setting_content_alpha), style = EbbingTheme.typography.bodyMSB, color = EbbingTheme.colors.black)
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
    val contentColor = Color.Black.copy(alpha = textAlpha)
    // Android WidgetCard와 동일: 실제 위젯 모양(오늘 할 일 배지+plus, 빈 일정 문구)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(previewTheme.lightBg).copy(alpha = bgAlpha))
            .border(1.dp, EbbingTheme.colors.light2, RoundedCornerShape(16.dp))
            .padding(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text(
                text = stringResource(Res.string.setting_widget_today_todo),
                style = EbbingTheme.typography.bodyMSB,
                color = contentColor,
            )
            Text(
                text = "0",
                style = EbbingTheme.typography.bodyMSB,
                color = EbbingTheme.colors.primaryDefault.copy(alpha = textAlpha),
            )
            Text(
                text = " /0",
                style = EbbingTheme.typography.bodyMSB,
                color = contentColor,
                modifier = Modifier.weight(1f),
            )
            androidx.compose.foundation.Image(
                painter = painterResource(Res.drawable.ic_plus),
                contentDescription = null,
                modifier = Modifier.size(20.dp),
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = stringResource(Res.string.setting_no_schedule_today),
            style = EbbingTheme.typography.bodySM,
            color = contentColor,
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
        )
    }
}
