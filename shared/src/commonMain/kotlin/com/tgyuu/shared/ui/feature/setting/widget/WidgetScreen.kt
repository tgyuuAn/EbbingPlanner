package com.tgyuu.shared.ui.feature.setting.widget

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.tgyuu.shared.common.util.noRippleClickable
import com.tgyuu.shared.designsystem.component.EbbingSolidButton
import com.tgyuu.shared.designsystem.component.EbbingSubTopBar
import com.tgyuu.shared.designsystem.component.HorizontalBackgroundSlider
import com.tgyuu.shared.designsystem.component.HorizontalTextSlider
import com.tgyuu.shared.designsystem.foundation.EbbingTheme
import com.tgyuu.shared.designsystem.foundation.LocalColors
import com.tgyuu.shared.designsystem.foundation.colorSchemeFor
import com.tgyuu.shared.designsystem.util.EbbingVisibleAnimation
import com.tgyuu.shared.domain.model.Theme
import com.tgyuu.shared.platform.AnalyticsHelper
import com.tgyuu.shared.platform.logClick
import com.tgyuu.shared.ui.feature.schedule.bottomsheet.heading14SB
import com.tgyuu.shared.ui.feature.setting.theme.animateEbbingColors
import ebbingplanner.shared.generated.resources.Res
import ebbingplanner.shared.generated.resources.ic_check
import ebbingplanner.shared.generated.resources.ic_plus
import ebbingplanner.shared.generated.resources.setting_alpha_percent
import ebbingplanner.shared.generated.resources.setting_apply
import ebbingplanner.shared.generated.resources.setting_background_alpha
import ebbingplanner.shared.generated.resources.setting_change_widget_theme
import ebbingplanner.shared.generated.resources.setting_content_alpha
import ebbingplanner.shared.generated.resources.setting_dark
import ebbingplanner.shared.generated.resources.setting_light
import ebbingplanner.shared.generated.resources.setting_no_schedule_today
import ebbingplanner.shared.generated.resources.setting_preview
import ebbingplanner.shared.generated.resources.setting_theme
import ebbingplanner.shared.generated.resources.setting_widget_theme_change
import ebbingplanner.shared.generated.resources.setting_widget_today_todo
import kotlin.math.roundToInt
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Composable
fun WidgetScreen(
    viewModel: WidgetViewModel,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsState()
    val analyticsHelper = koinInject<AnalyticsHelper>()
    val focusManager = LocalFocusManager.current
    val onBackClick: () -> Unit = { viewModel.onIntent(WidgetIntent.OnBackClick) }
    val onThemeChange: (Theme) -> Unit = { viewModel.onIntent(WidgetIntent.OnThemeChange(it)) }
    val onBackgroundAlphaChange: (Float) -> Unit = { viewModel.onIntent(WidgetIntent.OnBackgroundAlphaChange(it)) }
    val onTextAlphaChange: (Float) -> Unit = { viewModel.onIntent(WidgetIntent.OnTextAlphaChange(it)) }
    val onSaveClick: () -> Unit = {
        // Android WidgetViewModel.updateWidget과 동일: Click_Apply_Widget
        analyticsHelper.logClick("Widget", "Apply")
        viewModel.onIntent(WidgetIntent.OnSaveClick)
        focusManager.clearFocus()
    }

    // Android와 동일: 테마/투명도 값을 모두 불러오기 전에는 내용(미리보기/선택기/슬라이더)을 그리지 않아요
    val theme = state.selectedTheme
    val backgroundAlpha = state.selectedBackgroundAlpha
    val textAlpha = state.selectedTextAlpha
    val isLoaded = theme != null && backgroundAlpha != null && textAlpha != null

    Column(modifier = modifier.fillMaxSize().imePadding()) {
        EbbingSubTopBar(
            title = stringResource(Res.string.setting_widget_theme_change),
            onNavigationClick = onBackClick,
            rightComponent = {},
            modifier = Modifier.padding(horizontal = 20.dp),
        )

        if (!EbbingTheme.windowLayout.isTablet) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
            ) {
                Text(
                    text = stringResource(Res.string.setting_change_widget_theme),
                    style = EbbingTheme.typography.headingLSB,
                    color = EbbingTheme.colors.textOnBackground,
                )

                if (isLoaded) {
                    PreviewBody(theme = theme!!, backgroundAlpha = backgroundAlpha!!, textAlpha = textAlpha!!)
                    ThemeBody(selectedTheme = theme, onThemeChange = onThemeChange)
                    AlphaBody(
                        selectedBackgroundAlpha = backgroundAlpha,
                        selectedTextAlpha = textAlpha,
                        onBackgroundAlphaChange = onBackgroundAlphaChange,
                        onTextAlphaChange = onTextAlphaChange,
                    )
                }

                Spacer(modifier = Modifier.height(60.dp))
            }
        } else {
            // Android TabletWidgetScreen과 동일: 스크롤 Row(padding 20, 간격 20) 안에 왼쪽 선택기/슬라이더, 오른쪽 미리보기
            Row(
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    modifier = Modifier.weight(1f),
                ) {
                    Text(
                        text = stringResource(Res.string.setting_change_widget_theme),
                        style = EbbingTheme.typography.headingLSB,
                        color = EbbingTheme.colors.textOnBackground,
                    )

                    if (isLoaded) {
                        ThemeBody(selectedTheme = theme!!, onThemeChange = onThemeChange)
                        AlphaBody(
                            selectedBackgroundAlpha = backgroundAlpha!!,
                            selectedTextAlpha = textAlpha!!,
                            onBackgroundAlphaChange = onBackgroundAlphaChange,
                            onTextAlphaChange = onTextAlphaChange,
                        )
                    }
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    modifier = Modifier.weight(1f),
                ) {
                    if (isLoaded) {
                        PreviewBody(theme = theme!!, backgroundAlpha = backgroundAlpha!!, textAlpha = textAlpha!!)
                    }
                }
            }
        }

        EbbingSolidButton(
            label = stringResource(Res.string.setting_apply),
            onClick = onSaveClick,
            enabled = state.isSaveEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .background(EbbingTheme.colors.background)
                .padding(horizontal = 20.dp, vertical = 16.dp),
        )
    }
}

@Composable
private fun ThemeBody(
    selectedTheme: Theme,
    onThemeChange: (Theme) -> Unit,
) {
    Text(
        text = stringResource(Res.string.setting_theme),
        style = EbbingTheme.typography.bodyMM,
        color = EbbingTheme.colors.textOnBackground,
        modifier = Modifier.padding(top = 32.dp),
    )

    val darkTheme = isSystemInDarkTheme()
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp),
    ) {
        Theme.entries.forEach { theme ->
            Box(contentAlignment = Alignment.Center) {
                // Android와 동일: 원 색상은 시스템 다크 모드 기준 해당 테마의 primary
                Spacer(
                    modifier = Modifier
                        .size(45.dp)
                        .clip(CircleShape)
                        .background(colorSchemeFor(theme, darkTheme).primaryNormal)
                        .noRippleClickable { onThemeChange(theme) },
                )

                EbbingVisibleAnimation(visible = selectedTheme == theme) {
                    Image(
                        painter = painterResource(Res.drawable.ic_check),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(EbbingTheme.colors.textOnPrimary),
                        modifier = Modifier.size(20.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun AlphaBody(
    selectedBackgroundAlpha: Float,
    selectedTextAlpha: Float,
    onBackgroundAlphaChange: (Float) -> Unit,
    onTextAlphaChange: (Float) -> Unit,
) {
    Text(
        text = stringResource(Res.string.setting_background_alpha),
        style = EbbingTheme.typography.bodyMM,
        color = EbbingTheme.colors.textOnBackground,
        modifier = Modifier.padding(top = 32.dp),
    )

    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp),
    ) {
        // Android HorizontalBackgroundSlider: 0.25~1, 0.25/0.5/0.75/1 스냅
        HorizontalBackgroundSlider(
            value = selectedBackgroundAlpha,
            onValueChange = onBackgroundAlphaChange,
            modifier = Modifier.weight(1f),
        )

        Text(
            text = stringResource(Res.string.setting_alpha_percent, (selectedBackgroundAlpha * 100).roundToInt()),
            style = EbbingTheme.typography.bodyMM,
            color = EbbingTheme.colors.textOnBackground,
        )
    }

    Text(
        text = stringResource(Res.string.setting_content_alpha),
        style = EbbingTheme.typography.bodyMM,
        color = EbbingTheme.colors.textOnBackground,
        modifier = Modifier.padding(top = 32.dp),
    )

    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp),
    ) {
        // Android HorizontalTextSlider: 0.1~1
        HorizontalTextSlider(
            value = selectedTextAlpha,
            onValueChange = onTextAlphaChange,
            modifier = Modifier.weight(1f),
        )

        Text(
            text = stringResource(Res.string.setting_alpha_percent, (selectedTextAlpha * 100).roundToInt()),
            style = EbbingTheme.typography.bodyMM,
            color = EbbingTheme.colors.textOnBackground,
        )
    }
}

@Composable
private fun PreviewBody(
    theme: Theme,
    backgroundAlpha: Float,
    textAlpha: Float,
) {
    val animatedLight = animateEbbingColors(colorSchemeFor(theme, darkTheme = false))
    val animatedDark = animateEbbingColors(colorSchemeFor(theme, darkTheme = true))

    Text(
        text = stringResource(Res.string.setting_preview),
        style = EbbingTheme.typography.bodyMM,
        color = EbbingTheme.colors.textOnBackground,
        modifier = Modifier.padding(top = 32.dp),
    )

    CompositionLocalProvider(LocalColors provides animatedLight) {
        WidgetCard(
            isDarkMode = false,
            backgroundAlpha = backgroundAlpha,
            textAlpha = textAlpha,
            modifier = Modifier.padding(top = 20.dp).fillMaxWidth(),
        )
    }

    CompositionLocalProvider(LocalColors provides animatedDark) {
        WidgetCard(
            isDarkMode = true,
            backgroundAlpha = backgroundAlpha,
            textAlpha = textAlpha,
            modifier = Modifier.padding(top = 20.dp).fillMaxWidth(),
        )
    }
}

@Composable
private fun WidgetCard(
    isDarkMode: Boolean,
    backgroundAlpha: Float,
    textAlpha: Float,
    modifier: Modifier = Modifier,
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .fillMaxWidth()
                .background(EbbingTheme.colors.background.copy(alpha = backgroundAlpha), RoundedCornerShape(16.dp))
                .border(0.5.dp, EbbingTheme.colors.textOnBackground, RoundedCornerShape(16.dp))
                .padding(12.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(EbbingTheme.colors.fillDisabled.copy(alpha = backgroundAlpha), RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 4.dp),
            ) {
                val todayTodoLabel = stringResource(Res.string.setting_widget_today_todo)
                Text(
                    text = buildAnnotatedString {
                        append(todayTodoLabel)
                        withStyle(SpanStyle(color = EbbingTheme.colors.primaryDeep.copy(alpha = textAlpha))) {
                            append("0")
                        }
                        append(" /0")
                    },
                    style = EbbingTheme.typography.bodyMM,
                    color = EbbingTheme.colors.textOnBackground.copy(alpha = textAlpha),
                    modifier = Modifier.weight(1f),
                )

                Image(
                    painter = painterResource(Res.drawable.ic_plus),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(EbbingTheme.colors.textOnBackground.copy(alpha = textAlpha)),
                    modifier = Modifier.size(20.dp),
                )
            }

            Text(
                text = stringResource(Res.string.setting_no_schedule_today),
                style = EbbingTheme.typography.bodyMM,
                color = EbbingTheme.colors.textOnBackground.copy(alpha = textAlpha),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 30.dp),
            )
        }

        Text(
            text = if (isDarkMode) stringResource(Res.string.setting_dark) else stringResource(Res.string.setting_light),
            style = EbbingTheme.typography.heading14SB,
            color = EbbingTheme.colors.textOnBackground,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 8.dp, end = 20.dp),
        )
    }
}
