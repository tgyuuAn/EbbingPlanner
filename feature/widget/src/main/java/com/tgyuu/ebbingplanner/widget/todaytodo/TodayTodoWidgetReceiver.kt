package com.tgyuu.ebbingplanner.widget.todaytodo

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import com.tgyuu.domain.model.SortType
import com.tgyuu.domain.model.Theme
import com.tgyuu.domain.model.TodoSchedule
import com.tgyuu.domain.repository.ConfigRepository
import com.tgyuu.domain.repository.TodoRepository
import com.tgyuu.ebbingplanner.widget.designsystem.foundation.BACKGROUND_ALPHA
import com.tgyuu.ebbingplanner.widget.designsystem.foundation.TEXT_ALPHA
import com.tgyuu.ebbingplanner.widget.designsystem.foundation.THEME
import com.tgyuu.analytics.AnalyticsEvent
import com.tgyuu.analytics.AnalyticsHelper
import com.tgyuu.ebbingplanner.widget.util.ADD_TODO_ACTION
import com.tgyuu.ebbingplanner.widget.util.GsonProvider
import com.tgyuu.ebbingplanner.widget.util.KEY_WIDGET_SOURCE
import com.tgyuu.ebbingplanner.widget.util.PretendardBitmapRenderer
import com.tgyuu.ebbingplanner.widget.util.RefreshAction
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
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
    private var updateJob: Job? = null

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
                val pendingResult = goAsync()
                analyticsHelper.logEvent(
                    AnalyticsEvent.Click(screenName = "TodoWidget", buttonName = "Refresh")
                )
                updateJob?.cancel()
                updateJob = scope.launch {
                    try {
                        updateDataInternal(context)
                    } finally {
                        pendingResult.finish()
                    }
                }
            }
            ADD_TODO_ACTION -> {
                val source = intent.extras?.getString(KEY_WIDGET_SOURCE) ?: "TodoWidget"
                analyticsHelper.logEvent(
                    AnalyticsEvent.Click(screenName = source, buttonName = "AddTodo")
                )
            }
        }
    }

    private fun updateData(context: Context) {
        updateJob?.cancel()
        updateJob = scope.launch { updateDataInternal(context) }
    }

    private suspend fun updateDataInternal(context: Context) {
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
            generatePretendardBitmaps(context, todoLists)
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
        todoLists: List<TodoSchedule>,
    ) {

        val white = android.graphics.Color.WHITE

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

        val doneSize = todoLists.count { it.isDone }
        val isAllDone = todoLists.isNotEmpty() && doneSize == todoLists.size

        PretendardBitmapRenderer.renderAndSave(
            context, "오늘 할 일   ",
            PretendardBitmapRenderer.Weight.BOLD, 18f, white,
            filename = "todo_header.png",
            strikethrough = isAllDone,
        )
        PretendardBitmapRenderer.renderAndSave(
            context, doneSize.toString(),
            PretendardBitmapRenderer.Weight.BOLD, 18f, white,
            filename = "todo_done_count.png",
        )

        PretendardBitmapRenderer.renderAndSave(
            context, "/${todoLists.size}",
            PretendardBitmapRenderer.Weight.BOLD, 18f, white,
            filename = "todo_total.png",
        )

        PretendardBitmapRenderer.renderAndSave(
            context, "오늘은 일정이 없어요",
            PretendardBitmapRenderer.Weight.SEMI_BOLD, 16f, white,
            filename = "todo_empty.png",
        )

        todoLists.take(MAX_VISIBLE_TODOS).forEachIndexed { index, todo ->
            PretendardBitmapRenderer.renderAndSave(
                context, todo.title,
                PretendardBitmapRenderer.Weight.SEMI_BOLD, 14f, white,
                filename = "todo_title_$index.png",
                maxWidthPx = titleMaxWidthPx,
                maxLines = 1,
            )
        }
    }

    companion object {
        val TODO_LISTS = stringPreferencesKey("todoLists")
        const val MAX_VISIBLE_TODOS = 20
    }
}
