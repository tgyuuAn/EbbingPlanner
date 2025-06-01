package com.tgyuu.ebbingplanner.ui.widget.todaytodo

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
import com.tgyuu.ebbingplanner.ui.widget.util.CheckTodoAction
import com.tgyuu.ebbingplanner.ui.widget.util.GsonProvider
import com.tgyuu.ebbingplanner.ui.widget.util.RefreshAction
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@AndroidEntryPoint
class TodayTodoWidgetReceiver : GlanceAppWidgetReceiver() {
    @Inject
    lateinit var todoRepository: TodoRepository

    override val glanceAppWidget: GlanceAppWidget = TodayTodoWidget()

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
            RefreshAction.TODAY_TODO_UPDATE_ACTION -> updateData(context)
            CheckTodoAction.CHECK_TODO -> {
                val todoId = intent.extras?.getInt(TODO_ID)
                todoId ?: return
                checkTodo(todoId, context)
            }
        }
    }

    private fun checkTodo(todoId: Int, context: Context) = scope.launch {
        val selectedTodo = todoRepository.loadSchedule(todoId)
        val updatedTodo = selectedTodo.copy(isDone = !selectedTodo.isDone)
        todoRepository.updateTodo(updatedTodo)
        updateData(context)
    }

    private fun updateData(context: Context) = scope.launch {
        val gson = GsonProvider.gson
        val todoLists = todoRepository
            .loadSchedulesByDate(LocalDate.now())
            .sortedWith(compareBy({ it.isDone }, { it.title }))

        val glanceId = GlanceAppWidgetManager(context)
            .getGlanceIds(TodayTodoWidget::class.java)
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

    companion object {
        val TODO_LISTS = stringPreferencesKey("todoLists")
        const val TODO_ID = "todoId"
    }
}
