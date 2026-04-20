package com.tgyuu.ebbingplanner.widget.calendar

import android.appwidget.AppWidgetManager
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
import com.tgyuu.designsystem.component.calendar.getEbbingDayOfWeek
import com.tgyuu.designsystem.component.calendar.toKorean
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
import com.tgyuu.ebbingplanner.widget.designsystem.foundation.WIDGET_MONDAY_START
import com.tgyuu.ebbingplanner.widget.util.CheckTodoAction
import com.tgyuu.ebbingplanner.widget.util.CheckTodoAction.Companion.TODO_ID
import com.tgyuu.analytics.AnalyticsEvent
import com.tgyuu.analytics.AnalyticsHelper
import com.tgyuu.ebbingplanner.widget.util.GsonProvider
import com.tgyuu.ebbingplanner.widget.util.PretendardBitmapRenderer
import com.tgyuu.ebbingplanner.widget.util.RefreshAction
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@AndroidEntryPoint
class CalendarWidgetReceiver : GlanceAppWidgetReceiver() {
    @Inject
    lateinit var todoRepository: TodoRepository

    @Inject
    lateinit var configRepository: ConfigRepository

    @Inject
    lateinit var analyticsHelper: AnalyticsHelper

    override val glanceAppWidget: GlanceAppWidget = CalendarWidget()

    private val scope = MainScope()

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        analyticsHelper.logEvent(
            AnalyticsEvent.Click(screenName = "CalendarWidget", buttonName = "Set")
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
                    AnalyticsEvent.Click(screenName = "CalendarWidget", buttonName = "Refresh")
                )
                updateData(context)
            }
            CheckTodoAction.CHECK_TODO_ACTION -> {
                val todoId = intent.extras?.getInt(TODO_ID)
                todoId ?: return
                analyticsHelper.logEvent(
                    AnalyticsEvent.Click(
                        screenName = "CalendarWidget",
                        buttonName = "CheckTodo",
                        properties = mapOf("todoId" to todoId),
                    )
                )
                checkTodo(todoId, context)
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
        val theme = configRepository.getWidgetTheme().firstOrNull() ?: Theme.NORMAL
        val backgroundAlpha = configRepository.getWidgetBackgroundAlpha().firstOrNull() ?: 1f
        val textAlpha = configRepository.getWidgetTextAlpha().firstOrNull() ?: 1f
        val mondayStart = configRepository.getMondayStart().firstOrNull() ?: false
        val sortType = configRepository.getSortType()

        val now = LocalDate.now()
        val allSchedules = todoRepository.loadTodoSchedulesByDateRange(
            now.withDayOfMonth(1),
            now.withDayOfMonth(now.lengthOfMonth())
        )
        val byDate = buildByDateMap(allSchedules, sortType)

        generatePretendardBitmaps(context, theme, textAlpha, mondayStart, now)

        val glanceId = GlanceAppWidgetManager(context)
            .getGlanceIds(CalendarWidget::class.java)
            .firstOrNull()

        val gson = GsonProvider.gson
        val json = gson.toJson(byDate)
        glanceId?.let {
            updateAppWidgetState(context, PreferencesGlanceStateDefinition, it) { pref ->
                pref.toMutablePreferences().apply {
                    this[SCHEDULES_BY_DATE_MAP] = json
                    this[THEME] = theme.name
                    this[BACKGROUND_ALPHA] = backgroundAlpha
                    this[TEXT_ALPHA] = textAlpha
                    this[WIDGET_MONDAY_START] = mondayStart
                }
            }

            glanceAppWidget.update(context, it)
        }
    }

    private fun generatePretendardBitmaps(
        context: Context,
        theme: Theme,
        textAlpha: Float,
        mondayStart: Boolean,
        now: LocalDate,
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
        val textSub = colorScheme.textSub.copy(alpha = textAlpha).toArgb()

        // 년/월 헤더
        PretendardBitmapRenderer.renderAndSave(
            context, "${now.year}년 ${now.monthValue}월",
            PretendardBitmapRenderer.Weight.BOLD, 16f, textOnBackground,
            filename = "calendar_header.png",
        )

        // 요일 라벨 (0~6)
        getEbbingDayOfWeek(mondayStart).forEachIndexed { index, dow ->
            PretendardBitmapRenderer.renderAndSave(
                context, dow.toKorean(),
                PretendardBitmapRenderer.Weight.MEDIUM, 14f, textSub,
                filename = "calendar_dow_$index.png",
            )
        }
    }

    private fun buildByDateMap(
        schedules: List<TodoSchedule>,
        sortType: SortType,
    ): Map<LocalDate, List<TodoSchedule>> {
        val grouped = schedules.groupBy { it.date }

        return grouped.mapValues { (_, list) ->
            when (sortType) {
                SortType.CREATED -> list.sortedWith(compareBy({ it.isDone }, { it.createdAt }))
                SortType.NAME -> list.sortedWith(compareBy({ it.isDone }, { it.title }))
                SortType.PRIORITY -> list.sortedWith(compareBy({ it.isDone }, { it.priority }))
            }
        }
    }

    companion object {
        val SCHEDULES_BY_DATE_MAP = stringPreferencesKey("schedulesByDateMap")
    }
}
