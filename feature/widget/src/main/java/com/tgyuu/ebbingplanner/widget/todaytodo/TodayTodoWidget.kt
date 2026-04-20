package com.tgyuu.ebbingplanner.widget.todaytodo

import android.content.ComponentName
import android.content.Context
import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.actionParametersOf
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.color.ColorProvider
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontFamily
import androidx.glance.text.FontStyle
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextDecoration
import androidx.glance.text.TextStyle
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
import com.tgyuu.ebbingplanner.widget.util.ACTION_OPEN_ADD_TODO
import com.tgyuu.ebbingplanner.widget.util.AddTodoFromWidgetAction
import com.tgyuu.ebbingplanner.widget.util.BaseWidgetPreview
import com.tgyuu.ebbingplanner.widget.util.CheckTodoAction
import com.tgyuu.ebbingplanner.widget.util.EbbingWidgetPreview
import com.tgyuu.ebbingplanner.widget.util.GsonProvider
import com.tgyuu.ebbingplanner.widget.util.PretendardBitmapRenderer
import com.tgyuu.ebbingplanner.widget.util.todoIdKey
import com.tgyuu.ebbingplanner.widget.util.widgetSourceKey
import java.time.LocalDate

class TodayTodoWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val headerBitmap = PretendardBitmapRenderer.loadBitmap(context, "todo_header.png")
        val doneBitmap = PretendardBitmapRenderer.loadBitmap(context, "todo_done_count.png")
        val totalBitmap = PretendardBitmapRenderer.loadBitmap(context, "todo_total.png")
        val emptyBitmap = PretendardBitmapRenderer.loadBitmap(context, "todo_empty.png")
        val titleBitmaps = (0 until MAX_VISIBLE_TODOS).map { i ->
            PretendardBitmapRenderer.loadBitmap(context, "todo_title_$i.png")
        }

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
                    headerBitmap = headerBitmap,
                    doneBitmap = doneBitmap,
                    totalBitmap = totalBitmap,
                    emptyBitmap = emptyBitmap,
                    titleBitmaps = titleBitmaps,
                )
            }
        }
    }
}

@Composable
private fun TodayTodoWidgetContent(
    alpha: Float,
    todoLists: List<TodoSchedule>,
    headerBitmap: Bitmap?,
    doneBitmap: Bitmap?,
    totalBitmap: Bitmap?,
    emptyBitmap: Bitmap?,
    titleBitmaps: List<Bitmap?>,
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

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = GlanceModifier.defaultWeight(),
            ) {
                if (headerBitmap != null) {
                    Image(provider = ImageProvider(headerBitmap), contentDescription = "오늘 할 일")
                } else {
                    Text(
                        text = "오늘 할 일   ",
                        style = EbbingWidgetTypography.heading18B.copy(
                            color = LocalEbbingWidgetColors.current.textOnBackground,
                        ),
                    )
                }
                if (doneBitmap != null) {
                    Image(
                        provider = ImageProvider(doneBitmap),
                        contentDescription = "완료 ${todoListsDoneSize}개",
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
                if (totalBitmap != null) {
                    Image(
                        provider = ImageProvider(totalBitmap),
                        contentDescription = "전체 ${todoLists.size}개",
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
        }

        if (todoLists.isEmpty()) {
            if (emptyBitmap != null) {
                Image(
                    provider = ImageProvider(emptyBitmap),
                    contentDescription = "오늘은 일정이 없어요",
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
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                items(items = todoLists.mapIndexed { i, it -> i to it }) { (index, item) ->
                    TodoItemRow(
                        todo = item,
                        titleBitmap = titleBitmaps.getOrNull(index),
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
                .height(14.dp)
                .cornerRadius(2.dp)
                .background(ColorProvider(Color(todo.color), Color(todo.color)))
        )

        if (titleBitmap != null) {
            Image(
                provider = ImageProvider(titleBitmap),
                contentDescription = todo.title,
                modifier = GlanceModifier.padding(horizontal = 12.dp).defaultWeight(),
            )
        } else {
            Text(
                text = todo.title,
                style = (if (todo.isDone) EbbingWidgetTypography.body14M else EbbingWidgetTypography.heading14SB).copy(
                    color = if (todo.isDone) LocalEbbingWidgetColors.current.textDisabled else LocalEbbingWidgetColors.current.textOnBackground,
                    textDecoration = if (todo.isDone) TextDecoration.LineThrough else null,
                ),
                maxLines = 2,
                modifier = GlanceModifier.padding(horizontal = 12.dp)
                    .defaultWeight(),
            )
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
            headerBitmap = null,
            doneBitmap = null,
            totalBitmap = null,
            emptyBitmap = null,
            titleBitmaps = emptyList(),
        )
    }
}

@EbbingWidgetPreview
@Composable
private fun HomeWidgetPreview2() {
    BaseWidgetPreview {
        TodayTodoWidgetContent(
            alpha = 1f,
            headerBitmap = null,
            doneBitmap = null,
            totalBitmap = null,
            emptyBitmap = null,
            titleBitmaps = emptyList(),
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

