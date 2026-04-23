package com.tgyuu.ebbingplanner.widget.todaytodo

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import androidx.compose.ui.graphics.toArgb
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
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
import com.tgyuu.domain.model.SortType
import com.tgyuu.domain.model.Theme
import com.tgyuu.domain.model.TodoSchedule
import com.tgyuu.domain.repository.ConfigRepository
import com.tgyuu.domain.repository.TodoRepository
import com.tgyuu.ebbingplanner.widget.designsystem.foundation.BACKGROUND_ALPHA
import com.tgyuu.ebbingplanner.widget.designsystem.foundation.TEXT_ALPHA
import com.tgyuu.ebbingplanner.widget.designsystem.foundation.THEME
import com.tgyuu.ebbingplanner.widget.util.CheckTodoAction
import com.tgyuu.ebbingplanner.widget.util.CheckTodoAction.Companion.TODO_ID
import com.tgyuu.analytics.AnalyticsEvent
import com.tgyuu.analytics.AnalyticsHelper
import com.tgyuu.ebbingplanner.widget.util.ADD_TODO_ACTION
import com.tgyuu.ebbingplanner.widget.util.GsonProvider
import com.tgyuu.ebbingplanner.widget.util.KEY_WIDGET_SOURCE
import com.tgyuu.ebbingplanner.widget.util.PretendardBitmapRenderer
import com.tgyuu.ebbingplanner.widget.util.RefreshAction
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import javax.inject.Inject

@AndroidEntryPoint
class TodayTodoWidgetReceiver : GlanceAppWidgetReceiver() {
    @Inject
    lateinit var todoRepository: TodoRepository

    @Inject
    lateinit var configRepository: ConfigRepository

    @Inject
    lateinit var analyticsHelper: AnalyticsHelper

    override val glanceAppWidget: GlanceAppWidget = TodayTodoWidget()

    private val scope = MainScope()

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        analyticsHelper.logEvent(
            AnalyticsEvent.Click(screenName = "TodoWidget", buttonName = "Set")
        )
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        updateData(context)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        when (intent.action) {
            RefreshAction.UPDATE_ACTION -> {
                analyticsHelper.logEvent(
                    AnalyticsEvent.Click(screenName = "TodoWidget", buttonName = "Refresh")
                )
                updateData(context)
            }
            CheckTodoAction.CHECK_TODO_ACTION -> {
                val todoId = intent.extras?.getInt(TODO_ID)
                todoId ?: return
                analyticsHelper.logEvent(
                    AnalyticsEvent.Click(
                        screenName = "TodoWidget",
                        buttonName = "Check",
                        properties = mapOf("todoId" to todoId),
                    )
                )
                checkTodo(todoId, context)
            }
            ADD_TODO_ACTION -> {
                val source = intent.extras?.getString(KEY_WIDGET_SOURCE) ?: "TodoWidget"
                analyticsHelper.logEvent(
                    AnalyticsEvent.Click(screenName = source, buttonName = "AddTodo")
                )
            }
        }
    }

    private fun checkTodo(todoId: Int, context: Context) = scope.launch {
        val selectedTodo = todoRepository.loadSchedule(todoId) ?: run {
            updateData(context)
            return@launch
        }
        val updatedTodo = selectedTodo.copy(isDone = !selectedTodo.isDone)
        todoRepository.updateTodo(updatedTodo)
        updateData(context)
    }

    private fun updateData(context: Context) = scope.launch {
        val gson = GsonProvider.gson

        val theme = configRepository.getWidgetTheme().firstOrNull() ?: Theme.NORMAL
        val backgroundAlpha = configRepository.getWidgetBackgroundAlpha().firstOrNull() ?: 1f
        val textAlpha = configRepository.getWidgetTextAlpha().firstOrNull() ?: 1f
        val sortType = configRepository.getSortType()
        val todoLists = todoRepository
            .loadSchedulesByDate(LocalDate.now())
            .sortedWith(
                when (sortType) {
                    SortType.CREATED -> compareBy({ it.isDone }, { it.createdAt })
                    SortType.NAME -> compareBy({ it.isDone }, { it.title })
                    SortType.PRIORITY -> compareBy({ it.isDone }, { it.priority })
                }
            )

        withContext(Dispatchers.IO) {
            generatePretendardBitmaps(context, theme, textAlpha, todoLists)
        }

        val glanceId = GlanceAppWidgetManager(context)
            .getGlanceIds(TodayTodoWidget::class.java)
            .firstOrNull()

        val json = gson.toJson(todoLists)

        glanceId?.let {
            updateAppWidgetState(context, PreferencesGlanceStateDefinition, it) { pref ->
                pref.toMutablePreferences().apply {
                    this[TODO_LISTS] = json
                    this[THEME] = theme.name
                    this[BACKGROUND_ALPHA] = backgroundAlpha
                    this[TEXT_ALPHA] = textAlpha
                }
            }

            glanceAppWidget.update(context, it)
        }
    }

    private fun generatePretendardBitmaps(
        context: Context,
        theme: Theme,
        textAlpha: Float,
        todoLists: List<TodoSchedule>,
    ) {
        val isDark = (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) ==
            Configuration.UI_MODE_NIGHT_YES

        val colorScheme = when (theme) {
            Theme.NORMAL -> if (isDark) normalDarkColorScheme else normalLightColorScheme
            Theme.FOREST -> if (isDark) forestDarkColorScheme else forestLightColorScheme
            Theme.SUNSET -> if (isDark) sunsetDarkColorScheme else sunsetLightColorScheme
            Theme.MARINE -> if (isDark) marineDarkColorScheme else marineLightColorScheme
            Theme.LILAC -> if (isDark) lilacDarkColorScheme else lilacLightColorScheme
        }

        val textOnBackground = colorScheme.textOnBackground.copy(alpha = textAlpha).toArgb()
        val textPrimary = colorScheme.textPrimary.copy(alpha = textAlpha).toArgb()
        val textDisabled = colorScheme.textDisabled.copy(alpha = textAlpha).toArgb()
        val textSub = colorScheme.textSub.copy(alpha = textAlpha).toArgb()

        val doneSize = todoLists.count { it.isDone }
        val density = context.resources.displayMetrics.density
        // 실제 위젯 인스턴스 중 가장 작은 너비 기준으로 비트맵 폭 계산 (없으면 XML minWidth 180dp)
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val widgetIds = appWidgetManager.getAppWidgetIds(
            ComponentName(context, TodayTodoWidgetReceiver::class.java)
        )
        val minWidgetWidthDp = widgetIds.toList().mapNotNull { id ->
            appWidgetManager.getAppWidgetOptions(id)
                .getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, -1)
                .takeIf { it > 0 }
        }.minOrNull() ?: 180
        // 외부 패딩(40dp) + 색상 바·패딩(15dp) + 체크 아이콘(20dp) + 이미지 내부 패딩(24dp) 제외
        val titleMaxWidthPx = ((minWidgetWidthDp - 99) * density).toInt().coerceAtLeast(50)

        PretendardBitmapRenderer.renderAndSave(
            context, "오늘 할 일   ",
            PretendardBitmapRenderer.Weight.BOLD, 18f, textOnBackground,
            filename = "todo_header.png",
        )

        val doneColor = if (doneSize > 0) textPrimary else textDisabled
        PretendardBitmapRenderer.renderAndSave(
            context, doneSize.toString(),
            PretendardBitmapRenderer.Weight.BOLD, 18f, doneColor,
            filename = "todo_done_count.png",
        )

        PretendardBitmapRenderer.renderAndSave(
            context, "/${todoLists.size}",
            PretendardBitmapRenderer.Weight.BOLD, 18f, textDisabled,
            filename = "todo_total.png",
        )

        PretendardBitmapRenderer.renderAndSave(
            context, "오늘은 일정이 없어요",
            PretendardBitmapRenderer.Weight.SEMI_BOLD, 16f, textSub,
            filename = "todo_empty.png",
        )

        todoLists.take(MAX_VISIBLE_TODOS).forEachIndexed { index, todo ->
            val titleColor = if (todo.isDone) textDisabled else textOnBackground
            val weight = if (todo.isDone) PretendardBitmapRenderer.Weight.MEDIUM
                         else PretendardBitmapRenderer.Weight.SEMI_BOLD
            PretendardBitmapRenderer.renderAndSave(
                context, todo.title,
                weight, 14f, titleColor,
                filename = "todo_title_$index.png",
                maxWidthPx = titleMaxWidthPx,
                maxLines = 2,
                strikethrough = todo.isDone,
            )
        }
    }

    companion object {
        val TODO_LISTS = stringPreferencesKey("todoLists")
        const val MAX_VISIBLE_TODOS = 20
    }
}
