package com.tgyuu.ebbingplanner.ui.widget.calendar

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import com.tgyuu.domain.model.SortType
import com.tgyuu.domain.model.TodoSchedule
import com.tgyuu.domain.repository.ConfigRepository
import com.tgyuu.domain.repository.TodoRepository
import com.tgyuu.ebbingplanner.ui.widget.util.GsonProvider
import com.tgyuu.ebbingplanner.ui.widget.util.RefreshAction
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@AndroidEntryPoint
class CalendarWidgetReceiver : GlanceAppWidgetReceiver() {
    @Inject
    lateinit var todoRepository: TodoRepository

    @Inject
    lateinit var configRepository: ConfigRepository

    override val glanceAppWidget: GlanceAppWidget = CalendarWidget()

    private val scope = MainScope()

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
            RefreshAction.CALENDAR_UPDATE_ACTION -> updateData(context)
        }
    }

    private fun updateData(context: Context) = scope.launch {
        val gson = GsonProvider.gson
        val sortType = configRepository.getSortType()
        val allSchedules = todoRepository.loadSchedules()
        val byDate = buildByDateMap(allSchedules, sortType)

        val glanceId = GlanceAppWidgetManager(context)
            .getGlanceIds(CalendarWidget::class.java)
            .firstOrNull()

        val json = gson.toJson(byDate)
        glanceId?.let {
            updateAppWidgetState(context, PreferencesGlanceStateDefinition, it) { pref ->
                pref.toMutablePreferences().apply {
                    this[SCHEDULES_BY_DATE_MAP] = json
                }
            }

            glanceAppWidget.update(context, it)
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
