package com.tgyuu.setting.graph.widget

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
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowWidthSizeClass
import com.tgyuu.common.util.EbbingVisibleAnimation
import com.tgyuu.common.util.clickable
import com.tgyuu.common.util.throttledClickable
import com.tgyuu.designsystem.BasePreview
import com.tgyuu.designsystem.EbbingPreview
import com.tgyuu.designsystem.R
import com.tgyuu.designsystem.component.EbbingSolidButton
import com.tgyuu.designsystem.component.EbbingSubTopBar
import com.tgyuu.designsystem.component.HorizontalBackgroundSlider
import com.tgyuu.designsystem.component.HorizontalTextSlider
import com.tgyuu.designsystem.foundation.EbbingTheme
import com.tgyuu.designsystem.foundation.LocalColors
import com.tgyuu.designsystem.foundation.forestDarkColorScheme
import com.tgyuu.designsystem.foundation.forestLightColorScheme
import com.tgyuu.designsystem.foundation.lilacDarkColorScheme
import com.tgyuu.designsystem.foundation.lilacLightColorScheme
import com.tgyuu.designsystem.foundation.marineDarkColorScheme
import com.tgyuu.designsystem.foundation.marineLightColorScheme
import com.tgyuu.designsystem.foundation.normalDarkColorScheme
import com.tgyuu.designsystem.foundation.normalLightColorScheme
import com.tgyuu.designsystem.foundation.sunsetDarkColorScheme
import com.tgyuu.designsystem.foundation.sunsetLightColorScheme
import com.tgyuu.domain.model.Theme
import com.tgyuu.setting.graph.ui.animateEbbingColors
import com.tgyuu.setting.graph.widget.contract.WidgetIntent
import com.tgyuu.setting.graph.widget.contract.WidgetState
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
internal fun WidgetRoute(
    viewModel: WidgetViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        coroutineScope {
            launch { viewModel.loadWidgetBackgroundAlpha() }
            launch { viewModel.loadWidgetTextAlpha() }
            launch { viewModel.loadWidgetTheme() }
        }
    }

    WidgetScreen(
        state = state,
        onBackClick = { viewModel.onIntent(WidgetIntent.OnBackClick) },
        onSaveClick = { viewModel.onIntent(WidgetIntent.OnSaveClick) },
        onThemeChange = { viewModel.onIntent(WidgetIntent.OnThemeChange(it)) },
        onBackgroundAlphaChange = { viewModel.onIntent(WidgetIntent.OnBackgroundAlphaChange(it)) },
        onTextAlphaChange = { viewModel.onIntent(WidgetIntent.OnTextAlphaChange(it)) },
    )
}

@Composable
private fun WidgetScreen(
    state: WidgetState,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit,
    onThemeChange: (Theme) -> Unit,
    onBackgroundAlphaChange: (Float) -> Unit,
    onTextAlphaChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    val windowSizeClass = currentWindowAdaptiveInfo()
        .windowSizeClass
        .windowWidthSizeClass

    if (windowSizeClass == WindowWidthSizeClass.COMPACT) {
        PhoneWidgetScreen(
            state = state,
            onBackClick = onBackClick,
            onSaveClick = onSaveClick,
            onThemeChange = onThemeChange,
            onBackgroundAlphaChange = onBackgroundAlphaChange,
            onTextAlphaChange = onTextAlphaChange,
            modifier = modifier,
        )
    } else {
        TabletWidgetScreen(
            state = state,
            onBackClick = onBackClick,
            onSaveClick = onSaveClick,
            onThemeChange = onThemeChange,
            onBackgroundAlphaChange = onBackgroundAlphaChange,
            onTextAlphaChange = onTextAlphaChange,
            modifier = modifier,
        )
    }
}

@Composable
private fun PhoneWidgetScreen(
    state: WidgetState,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit,
    onThemeChange: (Theme) -> Unit,
    onBackgroundAlphaChange: (Float) -> Unit,
    onTextAlphaChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()
    val focusManager = androidx.compose.ui.platform.LocalFocusManager.current

    Column(modifier = modifier.fillMaxSize().imePadding()) {
        EbbingSubTopBar(
            title = stringResource(R.string.setting_widget_theme_change),
            onNavigationClick = onBackClick,
            rightComponent = {
                if (!state.isTreatment) {
                    Text(
                        text = stringResource(R.string.setting_apply),
                        style = if (state.isSaveEnabled) EbbingTheme.typography.body16M else EbbingTheme.typography.body16M,
                        color = if (state.isSaveEnabled) EbbingTheme.colors.primaryNormal else EbbingTheme.colors.textDisabled,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .throttledClickable(
                                throttleTime = 1500L,
                                enabled = state.isSaveEnabled,
                                onClick = onSaveClick,
                            ),
                    )
                }
            },
            modifier = Modifier.padding(horizontal = 20.dp),
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
                .padding(20.dp),
        ) {
            Text(
                text = stringResource(R.string.setting_change_widget_theme),
                style = EbbingTheme.typography.heading24B,
                color = EbbingTheme.colors.textOnBackground,
            )

            state.selectedTheme?.let {
                state.selectedBackgroundAlpha?.let {
                    state.selectedTextAlpha?.let {
                        PreviewBody(
                            theme = state.selectedTheme,
                            backgroundAlpha = state.selectedBackgroundAlpha,
                            textAlpha = state.selectedTextAlpha,
                        )

                        ThemeBody(
                            selectedTheme = state.selectedTheme,
                            onThemeChange = onThemeChange,
                        )

                        AlphaBody(
                            selectedBackgroundAlpha = state.selectedBackgroundAlpha,
                            selectedTextAlpha = state.selectedTextAlpha,
                            onBackgroundAlphaChange = onBackgroundAlphaChange,
                            onTextAlphaChange = onTextAlphaChange,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(60.dp))
        }

        if (state.isTreatment) {
            EbbingSolidButton(
                label = stringResource(R.string.setting_apply),
                onClick = {
                    onSaveClick()
                    focusManager.clearFocus()
                },
                enabled = state.isSaveEnabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(EbbingTheme.colors.background)
                    .padding(horizontal = 20.dp, vertical = 16.dp),
            )
        }
    }
}

@Composable
private fun TabletWidgetScreen(
    state: WidgetState,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit,
    onThemeChange: (Theme) -> Unit,
    onBackgroundAlphaChange: (Float) -> Unit,
    onTextAlphaChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        EbbingSubTopBar(
            title = stringResource(R.string.setting_widget_theme_change),
            onNavigationClick = onBackClick,
            rightComponent = {
                Text(
                    text = stringResource(R.string.setting_apply),
                    style = if (state.isSaveEnabled) EbbingTheme.typography.body16M else EbbingTheme.typography.body16M,
                    color = if (state.isSaveEnabled) EbbingTheme.colors.primaryNormal else EbbingTheme.colors.textDisabled,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .throttledClickable(
                            throttleTime = 1500L,
                            enabled = state.isSaveEnabled,
                            onClick = onSaveClick,
                        ),
                )
            },
            modifier = Modifier.padding(horizontal = 20.dp),
        )

        Row(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
                .imePadding(),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Text(
                    text = stringResource(R.string.setting_change_widget_theme),
                    style = EbbingTheme.typography.heading24B,
                    color = EbbingTheme.colors.textOnBackground,
                )

                state.selectedTheme?.let {
                    state.selectedBackgroundAlpha?.let {
                        state.selectedTextAlpha?.let {
                            ThemeBody(
                                selectedTheme = state.selectedTheme,
                                onThemeChange = onThemeChange,
                            )
                            AlphaBody(
                                selectedBackgroundAlpha = state.selectedBackgroundAlpha,
                                selectedTextAlpha = state.selectedTextAlpha,
                                onBackgroundAlphaChange = onBackgroundAlphaChange,
                                onTextAlphaChange = onTextAlphaChange,
                            )
                        }
                    }
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                state.selectedTheme?.let {
                    state.selectedBackgroundAlpha?.let {
                        state.selectedTextAlpha?.let {
                            PreviewBody(
                                theme = state.selectedTheme,
                                backgroundAlpha = state.selectedBackgroundAlpha,
                                textAlpha = state.selectedTextAlpha,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun ThemeBody(
    selectedTheme: Theme,
    onThemeChange: (Theme) -> Unit,
) {
    Text(
        text = stringResource(R.string.setting_theme),
        style = EbbingTheme.typography.body16M,
        color = EbbingTheme.colors.textOnBackground,
        modifier = Modifier.padding(top = 32.dp),
    )

    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp),
    ) {
        Theme.entries.forEach { theme ->
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier,
            ) {
                val color = theme.primaryNormalColor(isSystemInDarkTheme())

                Spacer(
                    modifier = Modifier
                        .size(45.dp)
                        .clip(CircleShape)
                        .background(color)
                        .clickable { onThemeChange(theme) }
                )

                EbbingVisibleAnimation(selectedTheme == theme) {
                    Image(
                        painter = painterResource(R.drawable.ic_check),
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
internal fun AlphaBody(
    selectedBackgroundAlpha: Float,
    selectedTextAlpha: Float,
    onBackgroundAlphaChange: (Float) -> Unit,
    onTextAlphaChange: (Float) -> Unit,
) {
    Text(
        text = stringResource(R.string.setting_background_alpha),
        style = EbbingTheme.typography.body16M,
        color = EbbingTheme.colors.textOnBackground,
        modifier = Modifier.padding(top = 32.dp),
    )

    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp)
    ) {
        HorizontalBackgroundSlider(
            value = selectedBackgroundAlpha,
            onValueChange = onBackgroundAlphaChange,
            modifier = Modifier.weight(1f),
        )

        Text(
            text = stringResource(
                R.string.setting_alpha_percent,
                (selectedBackgroundAlpha * 100).roundToInt(),
            ),
            style = EbbingTheme.typography.body16M,
            color = EbbingTheme.colors.textOnBackground,
        )
    }

    Text(
        text = stringResource(R.string.setting_content_alpha),
        style = EbbingTheme.typography.body16M,
        color = EbbingTheme.colors.textOnBackground,
        modifier = Modifier.padding(top = 32.dp),
    )

    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp)
    ) {
        HorizontalTextSlider(
            value = selectedTextAlpha,
            onValueChange = onTextAlphaChange,
            modifier = Modifier.weight(1f),
        )

        Text(
            text = stringResource(
                R.string.setting_alpha_percent,
                (selectedTextAlpha * 100).roundToInt(),
            ),
            style = EbbingTheme.typography.body16M,
            color = EbbingTheme.colors.textOnBackground,
        )
    }
}

@Composable
internal fun PreviewBody(
    theme: Theme,
    backgroundAlpha: Float,
    textAlpha: Float,
    modifier: Modifier = Modifier,
) {
    val (darkColors, lightColors) = when (theme) {
        Theme.NORMAL -> normalDarkColorScheme to normalLightColorScheme
        Theme.FOREST -> forestDarkColorScheme to forestLightColorScheme
        Theme.SUNSET -> sunsetDarkColorScheme to sunsetLightColorScheme
        Theme.MARINE -> marineDarkColorScheme to marineLightColorScheme
        Theme.LILAC -> lilacDarkColorScheme to lilacLightColorScheme
    }

    val animatedDark = animateEbbingColors(darkColors)
    val animatedLight = animateEbbingColors(lightColors)

    Text(
        text = stringResource(R.string.setting_preview),
        style = EbbingTheme.typography.body16M,
        color = EbbingTheme.colors.textOnBackground,
        modifier = Modifier.padding(top = 32.dp),
    )

    CompositionLocalProvider(LocalColors provides animatedLight) {
        WidgetCard(
            isDarkMode = false,
            backgroundAlpha = backgroundAlpha,
            textAlpha = textAlpha,
            modifier = modifier
                .padding(top = 20.dp)
                .fillMaxWidth(),
        )
    }

    CompositionLocalProvider(LocalColors provides animatedDark) {
        WidgetCard(
            isDarkMode = true,
            backgroundAlpha = backgroundAlpha,
            textAlpha = textAlpha,
            modifier = modifier
                .padding(top = 20.dp)
                .fillMaxWidth(),
        )
    }
}

@Composable
private fun WidgetCard(
    isDarkMode: Boolean,
    backgroundAlpha: Float,
    textAlpha: Float,
    modifier: Modifier = Modifier
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .fillMaxWidth()
                .background(
                    color = EbbingTheme.colors.background.copy(alpha = backgroundAlpha),
                    shape = RoundedCornerShape(16.dp),
                )
                .border(
                    color = EbbingTheme.colors.textOnBackground,
                    width = 0.5.dp,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        EbbingTheme.colors.fillDisabled.copy(alpha = backgroundAlpha),
                        shape = RoundedCornerShape(12.dp),
                    )
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                val todayTodoLabel = stringResource(R.string.setting_widget_today_todo)
                Text(
                    text = buildAnnotatedString {
                        append(todayTodoLabel)

                        withStyle(SpanStyle(color = EbbingTheme.colors.primaryDeep.copy(textAlpha))) {
                            append("0")
                        }
                        append(" /0")
                    },
                    style = EbbingTheme.typography.body16M,
                    color = EbbingTheme.colors.textOnBackground.copy(alpha = textAlpha),
                    modifier = Modifier.weight(1f),
                )

                Image(
                    painter = painterResource(R.drawable.ic_plus),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(EbbingTheme.colors.textOnBackground.copy(alpha = textAlpha)),
                    modifier = Modifier.size(20.dp),
                )
            }

            Text(
                text = stringResource(R.string.setting_no_schedule_today),
                style = EbbingTheme.typography.body16M,
                color = EbbingTheme.colors.textOnBackground.copy(alpha = textAlpha),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 30.dp),
            )
        }

        Text(
            text = if (isDarkMode) stringResource(R.string.setting_dark) else stringResource(R.string.setting_light),
            style = EbbingTheme.typography.heading14SB,
            color = EbbingTheme.colors.textOnBackground,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 8.dp, end = 20.dp)
        )
    }
}

@EbbingPreview
@Composable
private fun PreviewWidget() {
    BasePreview {
        WidgetScreen(
            state = WidgetState(),
            onSaveClick = {},
            onBackClick = {},
            onBackgroundAlphaChange = {},
            onTextAlphaChange = {},
            onThemeChange = {},
        )
    }
}

private fun Theme.primaryNormalColor(darkTheme: Boolean): Color {
    val scheme = when (this) {
        Theme.NORMAL -> if (darkTheme) normalDarkColorScheme else normalLightColorScheme
        Theme.FOREST -> if (darkTheme) forestDarkColorScheme else forestLightColorScheme
        Theme.SUNSET -> if (darkTheme) sunsetDarkColorScheme else sunsetLightColorScheme
        Theme.MARINE -> if (darkTheme) marineDarkColorScheme else marineLightColorScheme
        Theme.LILAC -> if (darkTheme) lilacDarkColorScheme else lilacLightColorScheme
    }
    return scheme.primaryNormal
}
