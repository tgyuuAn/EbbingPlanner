package com.tgyuu.setting.graph.theme

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
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
import com.tgyuu.designsystem.component.EbbingCheck
import com.tgyuu.designsystem.component.EbbingSolidButton
import com.tgyuu.designsystem.component.EbbingSubTopBar
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
import com.tgyuu.domain.model.DefaultTodoTag
import com.tgyuu.domain.model.Theme
import com.tgyuu.setting.graph.theme.contract.ThemeIntent
import com.tgyuu.setting.graph.theme.contract.ThemeState
import com.tgyuu.setting.graph.ui.animateEbbingColors

@Composable
internal fun ThemeRoute(viewModel: ThemeViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.loadTheme()
    }

    ThemeScreen(
        state = state,
        onBackClick = { viewModel.onIntent(ThemeIntent.OnBackClick) },
        onSaveClick = { viewModel.onIntent(ThemeIntent.OnUpdateClick) },
        onThemeChange = { viewModel.onIntent(ThemeIntent.OnThemeChange(it)) },
    )
}

@Composable
private fun ThemeScreen(
    state: ThemeState,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit,
    onThemeChange: (Theme) -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass

    if (windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.COMPACT) {
        PhoneThemeLayout(
            state = state,
            scrollState = scrollState,
            focusManager = focusManager,
            onBackClick = onBackClick,
            onSaveClick = onSaveClick,
            onThemeChange = onThemeChange,
            modifier = modifier
        )
    } else {
        TabletThemeLayout(
            state = state,
            focusManager = focusManager,
            onBackClick = onBackClick,
            onSaveClick = onSaveClick,
            onThemeChange = onThemeChange,
            modifier = modifier
        )
    }
}

@Composable
private fun PhoneThemeLayout(
    state: ThemeState,
    scrollState: androidx.compose.foundation.ScrollState,
    focusManager: androidx.compose.ui.focus.FocusManager,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit,
    onThemeChange: (Theme) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize().imePadding()) {
        EbbingSubTopBar(
            title = "테마 변경",
            onNavigationClick = onBackClick,
            rightComponent = {
                if (!state.isTreatment) {
                    Text(
                        text = "적용",
                        style = if (state.isSaveEnabled) EbbingTheme.typography.body16M else EbbingTheme.typography.body16M,
                        color = if (state.isSaveEnabled) EbbingTheme.colors.primaryNormal else EbbingTheme.colors.textDisabled,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .throttledClickable(
                                throttleTime = 1500L,
                                enabled = state.isSaveEnabled
                            ) {
                                onSaveClick()
                                focusManager.clearFocus()
                            },
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
                text = "앱 테마를 변경해요.",
                style = EbbingTheme.typography.heading24B,
                color = EbbingTheme.colors.textOnBackground,
            )

            state.selectTheme?.let {
                PreviewBody(theme = state.selectTheme)

                ThemeBody(
                    selectedTheme = state.selectTheme,
                    onThemeChange = onThemeChange,
                )
            }

            Spacer(modifier = Modifier.height(60.dp))
        }

        if (state.isTreatment) {
            EbbingSolidButton(
                label = "적용",
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
private fun TabletThemeLayout(
    state: ThemeState,
    focusManager: androidx.compose.ui.focus.FocusManager,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit,
    onThemeChange: (Theme) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
    ) {
        EbbingSubTopBar(
            title = "테마 변경",
            onNavigationClick = onBackClick,
            rightComponent = {
                Text(
                    text = "적용",
                    style = if (state.isSaveEnabled) EbbingTheme.typography.body16M else EbbingTheme.typography.body16M,
                    color = if (state.isSaveEnabled) EbbingTheme.colors.primaryNormal else EbbingTheme.colors.textDisabled,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .throttledClickable(
                            throttleTime = 1500L,
                            enabled = state.isSaveEnabled
                        ) {
                            onSaveClick()
                            focusManager.clearFocus()
                        },
                )
            },
        )

        Row(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(20.dp),
            ) {
                Text(
                    text = "앱 테마를 변경해요.",
                    style = EbbingTheme.typography.heading24B,
                    color = EbbingTheme.colors.textOnBackground,
                )

                state.selectTheme?.let {
                    ThemeBody(
                        selectedTheme = state.selectTheme,
                        onThemeChange = onThemeChange,
                    )
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(20.dp),
            ) {
                state.selectTheme?.let {
                    PreviewBody(theme = state.selectTheme)
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
        text = "테마",
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
internal fun PreviewBody(
    theme: Theme,
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
        style = EbbingTheme.typography.body16M,
        color = EbbingTheme.colors.textOnBackground,
        modifier = Modifier.padding(top = 32.dp),
    )

    CompositionLocalProvider(LocalColors provides animatedLight) {
        TodoListCard(
            isDarkMode = false,
            modifier = modifier
                .padding(top = 20.dp)
                .fillMaxWidth(),
        )
    }

    CompositionLocalProvider(LocalColors provides animatedDark) {
        TodoListCard(
            isDarkMode = true,
            modifier = modifier
                .padding(top = 20.dp)
                .fillMaxWidth(),
        )
    }
}

@Composable
private fun TodoListCard(
    isDarkMode: Boolean,
    modifier: Modifier = Modifier
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = modifier
                .background(EbbingTheme.colors.background, shape = RoundedCornerShape(12.dp))
                .border(
                    color = EbbingTheme.colors.textOnBackground,
                    width = 0.5.dp,
                    shape = RoundedCornerShape(12.dp)
                )
                .wrapContentHeight()
                .animateContentSize(
                    animationSpec = tween(
                        durationMillis = 300,
                        easing = FastOutSlowInEasing,
                    )
                )
                .padding(20.dp)
        ) {
            Row(modifier = Modifier.height(IntrinsicSize.Min)) {
                VerticalDivider(
                    thickness = 8.dp,
                    color = Color(DefaultTodoTag.color),
                    modifier = Modifier
                        .fillMaxHeight()
                        .animateContentSize(
                            animationSpec = tween(
                                durationMillis = 300,
                                easing = FastOutSlowInEasing
                            )
                        )
                        .padding(end = 8.dp),
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(EbbingTheme.colors.fillNormal)
                            .padding(vertical = 12.dp, horizontal = 16.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "에빙 플래너 미리보기",
                                style = EbbingTheme.typography.body16M,
                                color = EbbingTheme.colors.textOnBackground,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(bottom = 4.dp),
                            )

                            Text(
                                text = "에빙 플래너 미리보기",
                                style = EbbingTheme.typography.body16M,
                                color = EbbingTheme.colors.textSub,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(bottom = 4.dp),
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp),
                            ) {
                                EbbingCheck(
                                    checked = true,
                                    colorValue = DefaultTodoTag.color,
                                    onCheckedChange = {},
                                    modifier = Modifier.size(20.dp),
                                )

                                Text(
                                    text = "우선도 : 0",
                                    style = EbbingTheme.typography.heading14SB,
                                    color = EbbingTheme.colors.textSub,
                                    maxLines = 1,
                                    textAlign = TextAlign.End,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f),
                                )
                            }
                        }

                        Image(
                            painter = painterResource(com.tgyuu.designsystem.R.drawable.ic_3dots),
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(EbbingTheme.colors.textSub),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    EbbingCheck(
                        checked = true,
                        colorValue = DefaultTodoTag.color,
                        onCheckedChange = { },
                        modifier = Modifier.size(20.dp),
                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(end = 32.dp, top = 4.dp, bottom = 4.dp),
            ) {
                Image(
                    painter = painterResource(com.tgyuu.designsystem.R.drawable.ic_memo),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(EbbingTheme.colors.textSub),
                    modifier = Modifier.size(16.dp),
                )

                Text(
                    text = "에빙 플래너 미리보기",
                    style = EbbingTheme.typography.heading14SB,
                    color = EbbingTheme.colors.textSub,
                    modifier = Modifier.weight(1f),
                )
            }
        }

        Text(
            text = if (isDarkMode) "다크" else "라이트",
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
private fun PreviewTheme() {
    BasePreview {
        ThemeScreen(
            state = ThemeState(),
            onBackClick = {},
            onSaveClick = {},
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
