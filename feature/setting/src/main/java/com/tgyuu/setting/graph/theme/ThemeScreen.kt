package com.tgyuu.setting.graph.theme

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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowWidthSizeClass
import com.tgyuu.common.util.EbbingVisibleAnimation
import com.tgyuu.common.util.clickable
import com.tgyuu.designsystem.BasePreview
import com.tgyuu.designsystem.EbbingPreview
import com.tgyuu.designsystem.R
import com.tgyuu.designsystem.component.EbbingSolidButton
import com.tgyuu.designsystem.component.EbbingSubTopBar
import com.tgyuu.designsystem.component.TodoListCard
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
import com.tgyuu.designsystem.model.ClickableText
import com.tgyuu.designsystem.model.TodoScheduleUiModel
import com.tgyuu.domain.model.DefaultTodoTag
import com.tgyuu.domain.model.Theme
import com.tgyuu.setting.graph.theme.contract.ThemeIntent
import com.tgyuu.setting.graph.theme.contract.ThemeState
import com.tgyuu.setting.graph.ui.animateEbbingColors
import com.tgyuu.common.now
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus

@Composable
internal fun ThemeRoute(viewModel: ThemeViewModel = koinViewModel()) {
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
            title = stringResource(R.string.setting_theme_change),
            onNavigationClick = onBackClick,
            rightComponent = {},
            modifier = Modifier.padding(horizontal = 20.dp),
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
                .padding(20.dp),
        ) {
            Text(
                text = stringResource(R.string.setting_change_app_theme),
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
            .fillMaxSize()
            .padding(horizontal = 20.dp),
    ) {
        EbbingSubTopBar(
            title = stringResource(R.string.setting_theme_change),
            onNavigationClick = onBackClick,
            rightComponent = {},
        )

        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .imePadding()
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(20.dp),
            ) {
                Text(
                    text = stringResource(R.string.setting_change_app_theme),
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
        text = stringResource(R.string.setting_preview),
        style = EbbingTheme.typography.body16M,
        color = EbbingTheme.colors.textOnBackground,
        modifier = Modifier.padding(top = 32.dp),
    )

    CompositionLocalProvider(LocalColors provides animatedLight) {
        ThemePreviewCard(
            isDarkMode = false,
            modifier = modifier
                .padding(top = 20.dp)
                .fillMaxWidth(),
        )
    }

    CompositionLocalProvider(LocalColors provides animatedDark) {
        ThemePreviewCard(
            isDarkMode = true,
            modifier = modifier
                .padding(top = 20.dp)
                .fillMaxWidth(),
        )
    }
}

@Composable
private fun ThemePreviewCard(
    isDarkMode: Boolean,
    modifier: Modifier = Modifier
) {
    val previewText = stringResource(R.string.setting_ebbing_planner_preview)
    val sampleTodos = remember(previewText) {
        val today = LocalDate.now()
        List(3) { index ->
            TodoScheduleUiModel(
                id = index + 1,
                infoId = 1,
                title = ClickableText.from(previewText),
                tagId = 1,
                name = previewText,
                color = DefaultTodoTag.color,
                date = today.plus(index, DateTimeUnit.DAY),
                memo = ClickableText.from(previewText),
                isPinned = true,
                isDone = index == 0,
                createdAt = today,
                infoCreatedAt = today,
            )
        }
    }

    Box(
        modifier = modifier
            .background(EbbingTheme.colors.background, shape = RoundedCornerShape(12.dp))
            .border(
                color = EbbingTheme.colors.textOnBackground,
                width = 0.5.dp,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(20.dp)
    ) {
        TodoListCard(
            todo = sampleTodos.first(),
            todosWithSameInfo = sampleTodos,
            onCheckedChange = {},
            onEditScheduleClick = {},
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 28.dp),
        )

        Text(
            text = if (isDarkMode) stringResource(R.string.setting_dark) else stringResource(R.string.setting_light),
            style = EbbingTheme.typography.heading14SB,
            color = EbbingTheme.colors.textOnBackground,
            modifier = Modifier.align(Alignment.BottomEnd)
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
