package com.tgyuu.ebbingplanner.widget.calendar

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import com.tgyuu.designsystem.component.calendar.getEbbingDayOfWeek
import com.tgyuu.designsystem.component.calendar.toKorean
import com.tgyuu.domain.model.SortType
import com.tgyuu.domain.model.Theme
import com.tgyuu.domain.model.TodoSchedule
import com.tgyuu.domain.repository.ConfigRepository
import com.tgyuu.domain.repository.TodoRepository
import com.tgyuu.ebbingplanner.widget.designsystem.foundation.BACKGROUND_ALPHA
import com.tgyuu.ebbingplanner.widget.designsystem.foundation.TEXT_ALPHA
import com.tgyuu.ebbingplanner.widget.designsystem.foundation.THEME
import com.tgyuu.ebbingplanner.widget.designsystem.foundation.WIDGET_MONDAY_START
import com.tgyuu.analytics.AnalyticsEvent
import com.tgyuu.analytics.AnalyticsHelper
import com.tgyuu.ebbingplanner.widget.util.GsonProvider
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
                val pendingResult = goAsync()
                analyticsHelper.logEvent(
                    AnalyticsEvent.Click(screenName = "CalendarWidget", buttonName = "Refresh")
                )
                scope.launch {
                    try {
                        updateDataInternal(context)
                    } finally {
                        pendingResult.finish()
                    }
                }
            }
        }
    }

    private fun updateData(context: Context) {
        scope.launch { updateDataInternal(context) }
    }

    private suspend fun updateDataInternal(context: Context) {
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

        withContext(Dispatchers.IO) {
            generatePretendardBitmaps(context, mondayStart, now)
        }

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
        mondayStart: Boolean,
        now: LocalDate,
    ) {
        val white = android.graphics.Color.WHITE
        // 년/월 헤더
        PretendardBitmapRenderer.renderAndSave(
            context, "${now.year}년 ${now.monthValue}월",
            PretendardBitmapRenderer.Weight.BOLD, 16f, white,
            filename = "calendar_header.png",
        )

        // 요일 라벨 (0~6)
        getEbbingDayOfWeek(mondayStart).forEachIndexed { index, dow ->
            PretendardBitmapRenderer.renderAndSave(
                context, dow.toKorean(),
                PretendardBitmapRenderer.Weight.MEDIUM, 14f, white,
                filename = "calendar_dow_$index.png",
            )
        }

        // 섹션 헤더: "오늘 할 일" + "X월 D일 할 일" (현재 월 전체)
        PretendardBitmapRenderer.renderAndSave(
            context, "오늘 할 일",
            PretendardBitmapRenderer.Weight.BOLD, 16f, white,
            filename = "calendar_section_today.png",
        )
        (1..now.lengthOfMonth()).forEach { day ->
            PretendardBitmapRenderer.renderAndSave(
                context, "${now.monthValue}월 ${day}일 할 일",
                PretendardBitmapRenderer.Weight.BOLD, 16f, white,
                filename = "calendar_section_day_$day.png",
            )
        }

        // 날짜 숫자 bitmap (흰색 1벌 → Glance ColorFilter.tint로 색상 적용)
        (1..31).forEach { day ->
            PretendardBitmapRenderer.renderAndSave(
                context, "$day",
                PretendardBitmapRenderer.Weight.MEDIUM, 12f, white,
                filename = "calendar_num_$day.png",
            )
        }
        PretendardBitmapRenderer.renderAndSave(
            context, "${now.dayOfMonth}",
            PretendardBitmapRenderer.Weight.BOLD, 12f, white,
            filename = "calendar_num_today.png",
        )
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
