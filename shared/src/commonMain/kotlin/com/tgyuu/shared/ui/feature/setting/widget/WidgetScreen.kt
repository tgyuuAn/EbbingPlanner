package com.tgyuu.shared.ui.feature.setting.widget

import com.tgyuu.shared.designsystem.foundation.LayoutConstants
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt
import com.tgyuu.shared.designsystem.component.EbbingSubTopBar
import com.tgyuu.shared.designsystem.foundation.EbbingTheme
import com.tgyuu.shared.designsystem.foundation.LocalColors
import com.tgyuu.shared.designsystem.foundation.colorSchemeFor
import com.tgyuu.shared.domain.model.Theme
import com.tgyuu.shared.ui.feature.setting.widget.WidgetState
import ebbingplanner.shared.generated.resources.Res
import ebbingplanner.shared.generated.resources.setting_apply
import ebbingplanner.shared.generated.resources.setting_background_alpha
import ebbingplanner.shared.generated.resources.setting_content_alpha
import ebbingplanner.shared.generated.resources.setting_preview
import ebbingplanner.shared.generated.resources.setting_widget_theme_change
import ebbingplanner.shared.generated.resources.setting_change_widget_theme
import ebbingplanner.shared.generated.resources.setting_theme
import ebbingplanner.shared.generated.resources.setting_light
import ebbingplanner.shared.generated.resources.setting_dark
import ebbingplanner.shared.generated.resources.setting_no_schedule_today
import ebbingplanner.shared.generated.resources.setting_widget_today_todo
import ebbingplanner.shared.generated.resources.ic_plus
import ebbingplanner.shared.generated.resources.ic_check
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun WidgetScreen(
    viewModel: WidgetViewModel,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            EbbingSubTopBar(
                title = stringResource(Res.string.setting_widget_theme_change),
                onNavigationClick = { viewModel.onIntent(WidgetIntent.OnBackClick) },
                rightComponent = {},
                modifier = Modifier.padding(horizontal = 20.dp),
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .padding(20.dp),
            ) {
                // Android와 동일: 큰 제목 → 미리보기(라이트/다크) → 테마 선택 → 투명도
                Text(
                    text = stringResource(Res.string.setting_change_widget_theme),
                    style = EbbingTheme.typography.headingLSB,
                    color = EbbingTheme.colors.black,
                )

                WidgetPreviewSection(state = state)
                WidgetThemeSection(
                    selectedTheme = state.selectedTheme ?: Theme.NORMAL,
                    onThemeChange = { viewModel.onIntent(WidgetIntent.OnThemeChange(it)) },
                )
                WidgetAlphaSection(
                    backgroundAlpha = state.selectedBackgroundAlpha ?: 1f,
                    textAlpha = state.selectedTextAlpha ?: 1f,
                    onBgAlphaChange = { viewModel.onIntent(WidgetIntent.OnBackgroundAlphaChange(it)) },
                    onTextAlphaChange = { viewModel.onIntent(WidgetIntent.OnTextAlphaChange(it)) },
                )

                Spacer(modifier = Modifier.height(60.dp))
            }

            // Android와 동일: 항상 하단 풀폭 '적용' 버튼
            com.tgyuu.shared.designsystem.component.EbbingSolidButton(
                label = stringResource(Res.string.setting_apply),
                onClick = { viewModel.onIntent(WidgetIntent.OnSaveClick) },
                enabled = state.isSaveEnabled,
                modifier = Modifier.fillMaxWidth().background(EbbingTheme.colors.background).padding(horizontal = 20.dp, vertical = 16.dp),
            )
        }
    }
}

@Composable
private fun WidgetPreviewSection(state: WidgetState) {
    val theme = state.selectedTheme ?: Theme.NORMAL
    val bgAlpha = state.selectedBackgroundAlpha ?: 1f
    val textAlpha = state.selectedTextAlpha ?: 1f

    Text(
        text = stringResource(Res.string.setting_preview),
        style = EbbingTheme.typography.bodyMM,
        color = EbbingTheme.colors.black,
        modifier = Modifier.padding(top = 32.dp),
    )
    // Android PreviewBody와 동일: 라이트/다크 카드 2개
    WidgetCard(theme = theme, isDark = false, backgroundAlpha = bgAlpha, textAlpha = textAlpha)
    WidgetCard(theme = theme, isDark = true, backgroundAlpha = bgAlpha, textAlpha = textAlpha)
}

@Composable
private fun WidgetCard(
    theme: Theme,
    isDark: Boolean,
    backgroundAlpha: Float,
    textAlpha: Float,
) {
    // 해당 테마/모드의 색상 스킴을 LocalColors로 주입해 카드 내부 색을 렌더 (Android CompositionLocalProvider 대응)
    CompositionLocalProvider(LocalColors provides colorSchemeFor(theme, isDark)) {
        Box(modifier = Modifier.fillMaxWidth().padding(top = 20.dp)) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(EbbingTheme.colors.background.copy(alpha = backgroundAlpha), RoundedCornerShape(16.dp))
                    .border(0.5.dp, EbbingTheme.colors.black, RoundedCornerShape(16.dp))
                    .padding(12.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(EbbingTheme.colors.light2.copy(alpha = backgroundAlpha), RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                ) {
                    val todayTodoLabel = stringResource(Res.string.setting_widget_today_todo)
                    Text(
                        text = buildAnnotatedString {
                            append(todayTodoLabel)
                            withStyle(SpanStyle(color = EbbingTheme.colors.primaryDefault.copy(alpha = textAlpha))) {
                                append("0")
                            }
                            append(" /0")
                        },
                        style = EbbingTheme.typography.bodyMM,
                        color = EbbingTheme.colors.black.copy(alpha = textAlpha),
                        modifier = Modifier.weight(1f),
                    )
                    Image(
                        painter = painterResource(Res.drawable.ic_plus),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(EbbingTheme.colors.black.copy(alpha = textAlpha)),
                        modifier = Modifier.size(20.dp),
                    )
                }

                Text(
                    text = stringResource(Res.string.setting_no_schedule_today),
                    style = EbbingTheme.typography.bodyMM,
                    color = EbbingTheme.colors.black.copy(alpha = textAlpha),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 30.dp),
                )
            }

            Text(
                text = if (isDark) stringResource(Res.string.setting_dark) else stringResource(Res.string.setting_light),
                style = EbbingTheme.typography.headingSSB,
                color = EbbingTheme.colors.black,
                modifier = Modifier.align(Alignment.BottomEnd).padding(bottom = 8.dp, end = 20.dp),
            )
        }
    }
}

@Composable
private fun WidgetThemeSection(
    selectedTheme: Theme,
    onThemeChange: (Theme) -> Unit,
) {
    Text(
        text = stringResource(Res.string.setting_theme),
        style = EbbingTheme.typography.bodyMM,
        color = EbbingTheme.colors.black,
        modifier = Modifier.padding(top = 32.dp),
    )
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
    ) {
        Theme.entries.forEach { theme ->
            ThemeSwatch(
                theme = theme,
                selected = selectedTheme == theme,
                onClick = { onThemeChange(theme) },
            )
        }
    }
}

@Composable
private fun ThemeSwatch(
    theme: Theme,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Box(contentAlignment = Alignment.Center) {
        Spacer(
            modifier = Modifier
                .size(45.dp)
                .clip(CircleShape)
                .background(colorSchemeFor(theme, false).primaryDefault)
                .clickable { onClick() },
        )
        AnimatedVisibility(visible = selected, enter = fadeIn(), exit = fadeOut()) {
            Image(
                painter = painterResource(Res.drawable.ic_check),
                contentDescription = null,
                colorFilter = ColorFilter.tint(EbbingTheme.colors.white),
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

@Composable
private fun WidgetAlphaSection(
    backgroundAlpha: Float,
    textAlpha: Float,
    onBgAlphaChange: (Float) -> Unit,
    onTextAlphaChange: (Float) -> Unit,
) {
    AlphaRow(
        label = stringResource(Res.string.setting_background_alpha),
        value = backgroundAlpha,
        onValueChange = onBgAlphaChange,
    )
    AlphaRow(
        label = stringResource(Res.string.setting_content_alpha),
        value = textAlpha,
        onValueChange = onTextAlphaChange,
    )
}

@Composable
private fun AlphaRow(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
) {
    Text(
        text = label,
        style = EbbingTheme.typography.bodyMM,
        color = EbbingTheme.colors.black,
        modifier = Modifier.padding(top = 32.dp),
    )
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
    ) {
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = 0f..1f,
            colors = SliderDefaults.colors(
                thumbColor = EbbingTheme.colors.primaryDefault,
                activeTrackColor = EbbingTheme.colors.primaryDefault,
            ),
            modifier = Modifier.weight(1f),
        )
        Text(
            text = "${(value * 100).roundToInt()}%",
            style = EbbingTheme.typography.bodyMM,
            color = EbbingTheme.colors.black,
        )
    }
}
