package com.tgyuu.ebbingplanner.ui.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import com.tgyuu.domain.repository.TodoRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@AndroidEntryPoint
class HomeAppWidgetReceiver : GlanceAppWidgetReceiver() {
    @Inject
    lateinit var todoRepository: TodoRepository

    override val glanceAppWidget: GlanceAppWidget = HomeAppWidget()

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
        if (intent.action == RefreshAction.UPDATE_ACTION) {
            updateData(context)
        }
    }

    private fun updateData(context: Context) {
        val gson = GsonProvider.gson
        scope.launch {
            val todoLists = todoRepository
                .loadSchedulesByDate(LocalDate.now())
                .sortedWith(compareBy({ it.isDone }, { it.title }))

            val glanceId = GlanceAppWidgetManager(context)
                .getGlanceIds(HomeAppWidget::class.java)
                .firstOrNull()

            val json = gson.toJson(todoLists)

            glanceId?.let {
                updateAppWidgetState(context, PreferencesGlanceStateDefinition, it) { pref ->
                    pref.toMutablePreferences().apply {
                        this[TODO_LISTS] = json
                    }
                }

                glanceAppWidget.update(context, it)
            }
        }
    }

    companion object {
        val TODO_LISTS = stringPreferencesKey("todoLists")
    }
}
