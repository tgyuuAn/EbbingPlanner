package com.tgyuu.shared.ui.feature.setting.theme

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.tgyuu.shared.common.now
import com.tgyuu.shared.common.util.noRippleClickable
import com.tgyuu.shared.designsystem.component.EbbingSolidButton
import com.tgyuu.shared.designsystem.component.EbbingSubTopBar
import com.tgyuu.shared.designsystem.component.TodoListCard
import com.tgyuu.shared.designsystem.foundation.EbbingColors
import com.tgyuu.shared.designsystem.foundation.EbbingTheme
import com.tgyuu.shared.designsystem.foundation.LocalColors
import com.tgyuu.shared.designsystem.foundation.colorSchemeFor
import com.tgyuu.shared.designsystem.util.EbbingVisibleAnimation
import com.tgyuu.shared.domain.model.DefaultTodoTag
import com.tgyuu.shared.domain.model.Theme
import com.tgyuu.shared.platform.AnalyticsHelper
import com.tgyuu.shared.platform.logClick
import com.tgyuu.shared.ui.feature.schedule.bottomsheet.heading14SB
import com.tgyuu.shared.ui.model.TodoScheduleUiModel
import ebbingplanner.shared.generated.resources.Res
import ebbingplanner.shared.generated.resources.ic_check
import ebbingplanner.shared.generated.resources.setting_apply
import ebbingplanner.shared.generated.resources.setting_change_app_theme
import ebbingplanner.shared.generated.resources.setting_dark
import ebbingplanner.shared.generated.resources.setting_ebbing_planner_preview
import ebbingplanner.shared.generated.resources.setting_light
import ebbingplanner.shared.generated.resources.setting_preview
import ebbingplanner.shared.generated.resources.setting_theme
import ebbingplanner.shared.generated.resources.setting_theme_change
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Composable
fun ThemeScreen(
    viewModel: ThemeViewModel,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsState()
    val analyticsHelper = koinInject<AnalyticsHelper>()
    val focusManager = LocalFocusManager.current
    val onBackClick: () -> Unit = { viewModel.onIntent(ThemeIntent.OnBackClick) }
    val onThemeChange: (Theme) -> Unit = { viewModel.onIntent(ThemeIntent.OnThemeChange(it)) }
    val onSaveClick: () -> Unit = {
        // Android ThemeViewModel.updateTheme와 동일: Click_Apply_Theme
        analyticsHelper.logClick("Theme", "Apply")
        viewModel.onIntent(ThemeIntent.OnUpdateClick)
        focusManager.clearFocus()
    }

    // Android와 동일: 폰/태블릿 분기는 앱 뼈대가 내려주는 windowLayout 기준
    if (!EbbingTheme.windowLayout.isTablet) {
        Column(modifier = modifier.fillMaxSize().imePadding()) {
            EbbingSubTopBar(
                title = stringResource(Res.string.setting_theme_change),
                onNavigationClick = onBackClick,
                rightComponent = {},
                modifier = Modifier.padding(horizontal = 20.dp),
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
            ) {
                Text(
                    text = stringResource(Res.string.setting_change_app_theme),
                    style = EbbingTheme.typography.headingLSB,
                    color = EbbingTheme.colors.black,
                )

                state.selectTheme?.let { selectTheme ->
                    PreviewBody(theme = selectTheme)
                    ThemeBody(selectedTheme = selectTheme, onThemeChange = onThemeChange)
                }

                Spacer(modifier = Modifier.height(60.dp))
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
    } else {
        // Android TabletThemeLayout과 동일: 바깥 가로 20 + 컬럼 20, 왼쪽 선택기 / 오른쪽 미리보기
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
        ) {
            EbbingSubTopBar(
                title = stringResource(Res.string.setting_theme_change),
                onNavigationClick = onBackClick,
                rightComponent = {},
            )

            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .imePadding(),
            ) {
                Column(modifier = Modifier.weight(1f).padding(20.dp)) {
                    Text(
                        text = stringResource(Res.string.setting_change_app_theme),
                        style = EbbingTheme.typography.headingLSB,
                        color = EbbingTheme.colors.black,
                    )

                    state.selectTheme?.let { selectTheme ->
                        ThemeBody(selectedTheme = selectTheme, onThemeChange = onThemeChange)
                    }
                }

                Column(modifier = Modifier.weight(1f).padding(20.dp)) {
                    state.selectTheme?.let { selectTheme ->
                        PreviewBody(theme = selectTheme)
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
}

/** Android ThemeBody 대응: '테마' 라벨(top 32) + 색상 원 목록(top 20) */
@Composable
private fun ThemeBody(
    selectedTheme: Theme,
    onThemeChange: (Theme) -> Unit,
) {
    Text(
        text = stringResource(Res.string.setting_theme),
        style = EbbingTheme.typography.bodyMM,
        color = EbbingTheme.colors.black,
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
                        .background(colorSchemeFor(theme, darkTheme).primaryDefault)
                        .noRippleClickable { onThemeChange(theme) },
                )

                EbbingVisibleAnimation(visible = selectedTheme == theme) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_check),
                        contentDescription = null,
                        // Android textOnPrimary 대응 (라이트=흰색, 다크=검정)
                        tint = EbbingTheme.colors.white,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }
        }
    }
}

/** Android PreviewBody 대응: '미리보기' 라벨(top 32) + 라이트/다크 카드(각 top 20), 색상 전환 애니메이션 */
@Composable
private fun PreviewBody(theme: Theme) {
    val animatedLight = animateEbbingColors(colorSchemeFor(theme, darkTheme = false))
    val animatedDark = animateEbbingColors(colorSchemeFor(theme, darkTheme = true))

    Text(
        text = stringResource(Res.string.setting_preview),
        style = EbbingTheme.typography.bodyMM,
        color = EbbingTheme.colors.black,
        modifier = Modifier.padding(top = 32.dp),
    )

    CompositionLocalProvider(LocalColors provides animatedLight) {
        ThemePreviewCard(
            isDarkMode = false,
            modifier = Modifier.padding(top = 20.dp).fillMaxWidth(),
        )
    }

    CompositionLocalProvider(LocalColors provides animatedDark) {
        ThemePreviewCard(
            isDarkMode = true,
            modifier = Modifier.padding(top = 20.dp).fillMaxWidth(),
        )
    }
}

@Composable
private fun ThemePreviewCard(
    isDarkMode: Boolean,
    modifier: Modifier = Modifier,
) {
    val previewText = stringResource(Res.string.setting_ebbing_planner_preview)
    val sampleTodos = remember(previewText) {
        val today = LocalDate.now()
        List(3) { index ->
            TodoScheduleUiModel(
                id = index + 1,
                infoId = 1,
                title = previewText,
                tagId = 1,
                name = previewText,
                color = DefaultTodoTag.color,
                date = today.plus(index, DateTimeUnit.DAY),
                memo = previewText,
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
                color = EbbingTheme.colors.black,
                width = 0.5.dp,
                shape = RoundedCornerShape(12.dp),
            )
            .padding(20.dp),
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
            text = stringResource(if (isDarkMode) Res.string.setting_dark else Res.string.setting_light),
            style = EbbingTheme.typography.heading14SB,
            color = EbbingTheme.colors.black,
            modifier = Modifier.align(Alignment.BottomEnd),
        )
    }
}

/** Android animateEbbingColors 대응: 테마를 바꿀 때 미리보기 색이 부드럽게 전환돼요 */
@Composable
internal fun animateEbbingColors(target: EbbingColors): EbbingColors = EbbingColors(
    background = animateColor(target.background),
    primaryDefault = animateColor(target.primaryDefault),
    primaryMiddle = animateColor(target.primaryMiddle),
    primaryLight = animateColor(target.primaryLight),
    black = animateColor(target.black),
    dark1 = animateColor(target.dark1),
    dark2 = animateColor(target.dark2),
    dark3 = animateColor(target.dark3),
    light1 = animateColor(target.light1),
    light2 = animateColor(target.light2),
    light3 = animateColor(target.light3),
    white = animateColor(target.white),
    error = animateColor(target.error),
    success = animateColor(target.success),
)

@Composable
private fun animateColor(target: Color): Color {
    val color by animateColorAsState(targetValue = target)
    return color
}
