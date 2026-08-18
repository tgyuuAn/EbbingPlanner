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
import ebbingplanner.shared.generated.resources.ic_check
import com.tgyuu.shared.designsystem.foundation.colorSchemeFor
import androidx.compose.material3.Icon
import androidx.compose.foundation.isSystemInDarkTheme
import com.tgyuu.shared.domain.model.Theme
import ebbingplanner.shared.generated.resources.Res
import ebbingplanner.shared.generated.resources.setting_apply
import ebbingplanner.shared.generated.resources.setting_dark
import ebbingplanner.shared.generated.resources.setting_ebbing_planner_preview
import com.tgyuu.shared.ui.model.TodoScheduleUiModel
import com.tgyuu.shared.designsystem.component.TodoListCard
import com.tgyuu.shared.domain.model.DefaultTodoTag
import com.tgyuu.shared.common.now
import kotlinx.datetime.LocalDate
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.plus
import ebbingplanner.shared.generated.resources.setting_light
import ebbingplanner.shared.generated.resources.setting_preview
import ebbingplanner.shared.generated.resources.setting_theme_change
import ebbingplanner.shared.generated.resources.theme_select_headline
import ebbingplanner.shared.generated.resources.setting_change_app_theme
import org.jetbrains.compose.resources.painterResource
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
            title = stringResource(Res.string.setting_theme_change),
            onNavigationClick = { viewModel.onIntent(ThemeIntent.OnBackClick) },
            rightComponent = {
                if (false) { // Android 정렬: 상단 저장 링크 미사용
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
                    Text(text = stringResource(Res.string.setting_change_app_theme), style = EbbingTheme.typography.headingLSB, color = EbbingTheme.colors.black)
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
                Text(text = stringResource(Res.string.setting_change_app_theme), style = EbbingTheme.typography.headingLSB, color = EbbingTheme.colors.black)
                Spacer(modifier = Modifier.height(32.dp))
                ThemeSelector(selectedTheme = state.selectTheme, onThemeSelected = { viewModel.onIntent(ThemeIntent.OnThemeChange(it)) })
                Spacer(modifier = Modifier.height(32.dp))
                ThemePreview(theme = state.selectTheme ?: Theme.NORMAL)
            }
        }

        if (true) { // Android 정렬: 항상 하단 저장 버튼
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
        val darkTheme = isSystemInDarkTheme()
        Theme.entries.forEach { theme ->
            val isSelected = theme == selectedTheme
            // Android\uc640 \ub3d9\uc77c: \uc6d0 \uc0c9\uc0c1\uc740 \ud574\ub2f9 \ud14c\ub9c8\uc758 primary, \uc120\ud0dd \ud45c\uc2dc\ub294 ic_check \uc544\uc774\ucf58
            val circleColor = colorSchemeFor(theme, darkTheme).primaryDefault

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.clickable { onThemeSelected(theme) },
            ) {
                Spacer(
                    modifier = Modifier
                        .size(45.dp)
                        .clip(CircleShape)
                        .background(circleColor),
                )

                androidx.compose.animation.AnimatedVisibility(
                    visible = isSelected,
                    enter = fadeIn(),
                    exit = fadeOut(),
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_check),
                        contentDescription = null,
                        tint = EbbingTheme.colors.white,
                        modifier = Modifier.size(20.dp),
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

        // Android PreviewBody와 동일: 라이트/다크 각각 실제 TodoListCard 샘플을 세로로 표시
        ThemePreviewCard(
            theme = theme,
            darkTheme = false,
            modifier = Modifier.padding(top = 20.dp).fillMaxWidth(),
        )
        ThemePreviewCard(
            theme = theme,
            darkTheme = true,
            modifier = Modifier.padding(top = 20.dp).fillMaxWidth(),
        )
    }
}

@Composable
private fun ThemePreviewCard(
    theme: Theme,
    darkTheme: Boolean,
    modifier: Modifier = Modifier,
) {
    val previewText = stringResource(Res.string.setting_ebbing_planner_preview)
    val labelText = stringResource(if (darkTheme) Res.string.setting_dark else Res.string.setting_light)
    // 해당 테마/다크모드 색을 적용해 실제 카드 모양으로 미리보기 (Android ThemePreviewCard 대응)
    EbbingTheme(darkTheme = darkTheme, theme = theme) {
        val today = LocalDate.now()
        val sampleTodos = List(3) { index ->
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
        Box(
            modifier = modifier
                .clip(androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
                .background(EbbingTheme.colors.background)
                .border(
                    0.5.dp,
                    EbbingTheme.colors.black,
                    androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                )
                .padding(20.dp),
        ) {
            TodoListCard(
                todo = sampleTodos.first(),
                todosWithSameInfo = sampleTodos,
                onCheckedChange = {},
                onEditScheduleClick = {},
                modifier = Modifier.fillMaxWidth().padding(bottom = 28.dp),
            )
            Text(
                text = labelText,
                style = EbbingTheme.typography.captionR12,
                color = EbbingTheme.colors.black,
                modifier = Modifier.align(Alignment.BottomEnd),
            )
        }
    }
}

