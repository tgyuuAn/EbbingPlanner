package com.tgyuu.ebbingplanner.widget.todaytodo

import android.content.ComponentName
import android.content.Context
import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastMapIndexed
import androidx.datastore.preferences.core.Preferences
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.actionParametersOf
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.color.ColorProvider
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextDecoration
import com.google.gson.reflect.TypeToken
import com.tgyuu.domain.model.Theme
import com.tgyuu.domain.model.TodoSchedule
import com.tgyuu.widget.R
import com.tgyuu.ebbingplanner.widget.designsystem.component.EbbingWidgetCheck
import com.tgyuu.ebbingplanner.widget.designsystem.foundation.BACKGROUND_ALPHA
import com.tgyuu.ebbingplanner.widget.designsystem.foundation.EbbingWidgetTheme
import com.tgyuu.ebbingplanner.widget.designsystem.foundation.EbbingWidgetTypography
import com.tgyuu.ebbingplanner.widget.designsystem.foundation.LocalEbbingWidgetColors
import com.tgyuu.ebbingplanner.widget.designsystem.foundation.TEXT_ALPHA
import com.tgyuu.ebbingplanner.widget.designsystem.foundation.THEME
import com.tgyuu.ebbingplanner.widget.todaytodo.TodayTodoWidgetReceiver.Companion.MAX_VISIBLE_TODOS
import com.tgyuu.ebbingplanner.widget.todaytodo.TodayTodoWidgetReceiver.Companion.TODO_LISTS
import com.tgyuu.ebbingplanner.widget.util.AddTodoFromWidgetAction
import com.tgyuu.ebbingplanner.widget.util.BaseWidgetPreview
import com.tgyuu.ebbingplanner.widget.util.CheckTodoAction
import com.tgyuu.ebbingplanner.widget.util.EbbingWidgetPreview
import com.tgyuu.ebbingplanner.widget.util.RefreshAction
import com.tgyuu.ebbingplanner.widget.util.GsonProvider
import com.tgyuu.ebbingplanner.widget.util.TodayTodoBitmaps
import com.tgyuu.ebbingplanner.widget.util.WidgetBitmapStore
import com.tgyuu.ebbingplanner.widget.util.todoIdKey
import com.tgyuu.ebbingplanner.widget.util.widgetSourceKey
import java.time.LocalDate

class TodayTodoWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val bitmaps = WidgetBitmapStore.loadTodayTodoBitmaps(context, MAX_VISIBLE_TODOS)

        provideContent {
            val prefs = currentState<Preferences>()

            val rawTheme: String = prefs[THEME] ?: Theme.NORMAL.name
            val theme = Theme.create(rawTheme)

            val backgroundAlpha: Float = prefs[BACKGROUND_ALPHA] ?: 1f
            val textAlpha: Float = prefs[TEXT_ALPHA] ?: 1f

            val rawJson: String = prefs[TODO_LISTS] ?: "[]"
            val type = object : TypeToken<List<TodoSchedule>>() {}.type
            val todoLists: List<TodoSchedule> = GsonProvider.gson.fromJson(rawJson, type)

            EbbingWidgetTheme(
                theme = theme,
                alpha = textAlpha,
            ) {
                TodayTodoWidgetContent(
                    alpha = backgroundAlpha,
                    todoLists = todoLists,
                    bitmaps = bitmaps,
                )
            }
        }
    }
}

@Composable
private fun TodayTodoWidgetContent(
    alpha: Float,
    todoLists: List<TodoSchedule>,
    bitmaps: TodayTodoBitmaps,
) {
    val backgroundImage = when (alpha) {
        0.25f -> R.drawable.shape_widget_background_25
        0.5f -> R.drawable.shape_widget_background_25
        0.75f -> R.drawable.shape_widget_background_75
        else -> R.drawable.shape_widget_background_100
    }

    Column(
        horizontalAlignment = Alignment.Start,
        modifier = GlanceModifier
            .fillMaxSize()
            .clickable(
                onClick = actionStartActivity(
                    ComponentName(
                        androidx.glance.LocalContext.current.packageName,
                        "${androidx.glance.LocalContext.current.packageName}.MainActivity"
                    )
                )
            )
            .background(
                imageProvider = ImageProvider(backgroundImage),
                colorFilter = ColorFilter.tint(LocalEbbingWidgetColors.current.background),
            )
            .padding(20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = GlanceModifier.fillMaxWidth(),
        ) {
            val todoListsDoneSize = todoLists.filter { it.isDone }.size
            val isAllDone = todoLists.isNotEmpty() && todoListsDoneSize == todoLists.size
            val headerColor = if (isAllDone) LocalEbbingWidgetColors.current.textDisabled
                else LocalEbbingWidgetColors.current.textOnBackground

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = GlanceModifier.defaultWeight(),
            ) {
                if (bitmaps.header != null) {
                    Image(
                        provider = ImageProvider(bitmaps.header),
                        contentDescription = "오늘 할 일",
                        colorFilter = ColorFilter.tint(headerColor),
                    )
                } else {
                    Text(
                        text = "오늘 할 일   ",
                        style = EbbingWidgetTypography.heading18B.copy(
                            color = headerColor,
                            textDecoration = if (isAllDone) TextDecoration.LineThrough else null,
                        ),
                    )
                }
                if (bitmaps.doneCount != null) {
                    Image(
                        provider = ImageProvider(bitmaps.doneCount),
                        contentDescription = "완료 ${todoListsDoneSize}개",
                        colorFilter = ColorFilter.tint(
                            if (todoListsDoneSize > 0) LocalEbbingWidgetColors.current.textPrimary
                            else LocalEbbingWidgetColors.current.textDisabled,
                        ),
                    )
                } else {
                    Text(
                        text = todoListsDoneSize.toString(),
                        style = EbbingWidgetTypography.heading18B.copy(
                            color = if (todoListsDoneSize > 0) LocalEbbingWidgetColors.current.textPrimary
                            else LocalEbbingWidgetColors.current.textDisabled,
                        ),
                    )
                }
                if (bitmaps.total != null) {
                    Image(
                        provider = ImageProvider(bitmaps.total),
                        contentDescription = "전체 ${todoLists.size}개",
                        colorFilter = ColorFilter.tint(LocalEbbingWidgetColors.current.textDisabled),
                    )
                } else {
                    Text(
                        text = "/${todoLists.size}",
                        style = EbbingWidgetTypography.heading18B.copy(
                            color = LocalEbbingWidgetColors.current.textDisabled,
                        ),
                    )
                }
            }

            Image(
                provider = ImageProvider(R.drawable.ic_widget_plus),
                contentDescription = null,
                modifier = GlanceModifier
                    .size(20.dp)
                    .clickable(
                        actionRunCallback<AddTodoFromWidgetAction>(
                            actionParametersOf(widgetSourceKey to "TodoWidget")
                        )
                    ),
            )

            Spacer(modifier = GlanceModifier.size(12.dp))

            Image(
                provider = ImageProvider(R.drawable.ic_widget_refresh),
                contentDescription = null,
                modifier = GlanceModifier
                    .size(20.dp)
                    .clickable(actionRunCallback<RefreshAction>()),
                colorFilter = ColorFilter.tint(LocalEbbingWidgetColors.current.textSub),
            )
        }

        if (todoLists.isEmpty()) {
            if (bitmaps.empty != null) {
                Image(
                    provider = ImageProvider(bitmaps.empty),
                    contentDescription = "오늘은 일정이 없어요",
                    colorFilter = ColorFilter.tint(LocalEbbingWidgetColors.current.textSub),
                    modifier = GlanceModifier.padding(vertical = 12.dp),
                )
            } else {
                Text(
                    text = "오늘은 일정이 없어요",
                    style = EbbingWidgetTypography.heading16SB.copy(
                        textAlign = TextAlign.Start,
                        color = LocalEbbingWidgetColors.current.textSub,
                    ),
                    modifier = GlanceModifier.fillMaxWidth()
                        .padding(vertical = 12.dp),
                )
            }
        } else {
            LazyColumn(
                modifier = GlanceModifier.fillMaxSize()
                    .padding(top = 6.dp),
            ) {
                items(items = todoLists.fastMapIndexed { i, it -> i to it }) { (index, item) ->
                    TodoItemRow(
                        todo = item,
                        titleBitmap = bitmaps.titles.getOrNull(index),
                        modifier = GlanceModifier.fillMaxWidth()
                            .padding(vertical = 6.dp),
                    )
                }
            }
        }
    }
}

@Composable
internal fun TodoItemRow(
    todo: TodoSchedule,
    titleBitmap: Bitmap? = null,
    modifier: GlanceModifier = GlanceModifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
    ) {
        Spacer(
            modifier = GlanceModifier
                .width(3.dp)
                .height(24.dp)
                .background(ColorProvider(Color(todo.color), Color(todo.color)))
        )

        Box(
            contentAlignment = Alignment.CenterStart,
            modifier = GlanceModifier.defaultWeight().padding(horizontal = 8.dp),
        ) {
            if (titleBitmap != null) {
                Image(
                    provider = ImageProvider(titleBitmap),
                    contentDescription = todo.title,
                    colorFilter = ColorFilter.tint(
                        if (todo.isDone) LocalEbbingWidgetColors.current.textDisabled
                        else LocalEbbingWidgetColors.current.textOnBackground,
                    ),
                )
            } else {
                Text(
                    text = todo.title,
                    style = (if (todo.isDone) EbbingWidgetTypography.heading16SB else EbbingWidgetTypography.body16M).copy(
                        color = if (todo.isDone) LocalEbbingWidgetColors.current.textDisabled else LocalEbbingWidgetColors.current.textOnBackground,
                        textDecoration = if (todo.isDone) TextDecoration.LineThrough else null,
                    ),
                    maxLines = 1,
                )
            }

            if (todo.isDone) {
                Spacer(
                    modifier = GlanceModifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(LocalEbbingWidgetColors.current.textDisabled),
                )
            }
        }

        EbbingWidgetCheck(
            checked = todo.isDone,
            colorValue = todo.color,
            onCheckedChange = actionRunCallback<CheckTodoAction>(
                actionParametersOf(todoIdKey to todo.id)
            )
        )
    }
}

@EbbingWidgetPreview
@Composable
private fun HomeWidgetPreview() {
    BaseWidgetPreview {
        TodayTodoWidgetContent(
            alpha = 1f,
            todoLists = emptyList(),
            bitmaps = TodayTodoBitmaps(
                header = null, doneCount = null, total = null,
                empty = null, titles = emptyList(),
            ),
        )
    }
}

@EbbingWidgetPreview
@Composable
private fun HomeWidgetPreview2() {
    BaseWidgetPreview {
        TodayTodoWidgetContent(
            alpha = 1f,
            bitmaps = TodayTodoBitmaps(
                header = null, doneCount = null, total = null,
                empty = null, titles = emptyList(),
            ),
            todoLists = listOf(
                TodoSchedule(
                    id = 1,
                    infoId = 101,
                    title = "코틀린 공부",
                    tagId = 1,
                    name = "공부",
                    color = 0xFF3282B8.toInt(),
                    date = LocalDate.of(2025, 5, 8),
                    memo = "Jetpack Compose 위젯",
                    priority = 1,
                    isDone = false,
                    createdAt = LocalDate.of(2025, 5, 1),
                    infoCreatedAt = LocalDate.of(2025, 5, 1)
                ),
                TodoSchedule(
                    id = 2,
                    infoId = 102,
                    title = "운동하기",
                    tagId = 2,
                    name = "운동",
                    color = 0xFFFF7490.toInt(),
                    date = LocalDate.of(2025, 5, 8),
                    memo = "헬스장 1시간",
                    priority = 2,
                    isDone = true,
                    createdAt = LocalDate.of(2025, 5, 2),
                    infoCreatedAt = LocalDate.of(2025, 5, 2)
                )
            )
        )
    }
}

