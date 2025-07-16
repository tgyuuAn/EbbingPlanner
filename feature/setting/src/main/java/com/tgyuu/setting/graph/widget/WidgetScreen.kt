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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tgyuu.common.util.EbbingVisibleAnimation
import com.tgyuu.common.util.clickable
import com.tgyuu.common.util.throttledClickable
import com.tgyuu.designsystem.BasePreview
import com.tgyuu.designsystem.EbbingPreview
import com.tgyuu.designsystem.R
import com.tgyuu.designsystem.component.EbbingSubTopBar
import com.tgyuu.designsystem.component.HorizontalSlider
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
            launch { viewModel.loadWidgetAlpha() }
            launch { viewModel.loadWidgetTheme() }
        }
    }

    WidgetScreen(
        state = state,
        onBackClick = { viewModel.onIntent(WidgetIntent.OnBackClick) },
        onSaveClick = { viewModel.onIntent(WidgetIntent.OnSaveClick) },
        onThemeChange = { viewModel.onIntent(WidgetIntent.OnThemeChange(it)) },
        onAlphaChange = { viewModel.onIntent(WidgetIntent.OnAlphaChange(it)) },
    )
}

@Composable
private fun WidgetScreen(
    state: WidgetState,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit,
    onThemeChange: (Theme) -> Unit,
    onAlphaChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass

    val scrollState = rememberScrollState()
    Column(modifier = modifier.fillMaxSize()) {
        EbbingSubTopBar(
            title = "위젯 테마 변경",
            onNavigationClick = onBackClick,
            rightComponent = {
                Text(
                    text = "적용",
                    style = if (state.isSaveEnabled) EbbingTheme.typography.bodyMSB else EbbingTheme.typography.bodyMM,
                    color = if (state.isSaveEnabled) EbbingTheme.colors.primaryDefault else EbbingTheme.colors.dark3,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .throttledClickable(
                            throttleTime = 1500L,
                            enabled = state.isSaveEnabled
                        ) {
                            onSaveClick()
                        },
                )
            },
            modifier = Modifier.padding(horizontal = 20.dp),
        )

        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .padding(20.dp)
                .imePadding(),
        ) {
            Text(
                text = "위젯 테마를 변경해요.",
                style = EbbingTheme.typography.headingLSB,
                color = EbbingTheme.colors.black,
            )

            state.selectedTheme?.let {
                state.selectedAlpha?.let {
                    PreviewBody(
                        theme = state.selectedTheme,
                        alpha = state.selectedAlpha,
                    )

                    ThemeBody(
                        selectedTheme = state.selectedTheme,
                        onThemeChange = onThemeChange,
                    )

                    AlphaBody(
                        selectedAlpha = state.selectedAlpha,
                        onAlphaChange = onAlphaChange,
                    )
                }
            }

            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}


@Composable
internal fun ThemeBody(
    selectedTheme: Theme,
    onThemeChange: (Theme) -> Unit,
) {
    Text(
        text = "테마",
        style = EbbingTheme.typography.bodyMSB,
        color = EbbingTheme.colors.black,
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
                val color = Color(if (isSystemInDarkTheme()) theme.lightBg else theme.darkBg)

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
                        colorFilter = ColorFilter.tint(EbbingTheme.colors.white),
                        modifier = Modifier.size(20.dp),
                    )
                }
            }
        }
    }
}

@Composable
internal fun AlphaBody(
    selectedAlpha: Float,
    onAlphaChange: (Float) -> Unit,
) {
    Text(
        text = "투명도",
        style = EbbingTheme.typography.bodyMSB,
        color = EbbingTheme.colors.black,
        modifier = Modifier.padding(top = 32.dp),
    )

    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp)
    ) {
        HorizontalSlider(
            value = selectedAlpha,
            onValueChange = onAlphaChange,
            modifier = Modifier.weight(1f),
        )

        Text(
            text = "${(selectedAlpha * 100).roundToInt()} %",
            style = EbbingTheme.typography.bodyMSB,
            color = EbbingTheme.colors.black,
        )
    }
}

@Composable
internal fun PreviewBody(
    theme: Theme,
    alpha: Float,
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
        text = "미리보기",
        style = EbbingTheme.typography.bodyMSB,
        color = EbbingTheme.colors.black,
        modifier = Modifier.padding(top = 32.dp),
    )

    CompositionLocalProvider(LocalColors provides animatedLight) {
        WidgetCard(
            isDarkMode = false,
            alpha = alpha,
            modifier = modifier
                .padding(top = 20.dp)
                .fillMaxWidth(),
        )
    }

    CompositionLocalProvider(LocalColors provides animatedDark) {
        WidgetCard(
            isDarkMode = true,
            alpha = alpha,
            modifier = modifier
                .padding(top = 20.dp)
                .fillMaxWidth(),
        )
    }
}

@Composable
private fun WidgetCard(
    isDarkMode: Boolean,
    alpha: Float,
    modifier: Modifier = Modifier
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .fillMaxWidth()
                .background(
                    color = EbbingTheme.colors.background.copy(alpha = alpha),
                    shape = RoundedCornerShape(16.dp),
                )
                .border(
                    color = EbbingTheme.colors.black,
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
                        EbbingTheme.colors.light1.copy(alpha = alpha),
                        shape = RoundedCornerShape(12.dp),
                    )
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(
                    text = buildAnnotatedString {
                        append("오늘 할 일  ")

                        withStyle(SpanStyle(color = EbbingTheme.colors.primaryMiddle)) {
                            append("0")
                        }
                        append(" /0")
                    },
                    style = EbbingTheme.typography.bodyMSB,
                    color = EbbingTheme.colors.black.copy(alpha = alpha),
                    modifier = Modifier.weight(1f),
                )

                Image(
                    painter = painterResource(R.drawable.ic_plus),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(EbbingTheme.colors.black.copy(alpha = alpha)),
                    modifier = Modifier.size(20.dp),
                )
            }

            Text(
                text = "금일 스케줄이 없어요.",
                style = EbbingTheme.typography.bodyMM,
                color = EbbingTheme.colors.black.copy(alpha = alpha),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 30.dp),
            )
        }

        Text(
            text = if (isDarkMode) "다크" else "라이트",
            style = EbbingTheme.typography.bodySSB,
            color = EbbingTheme.colors.black,
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
            onAlphaChange = {},
            onThemeChange = {},
        )
    }
}
