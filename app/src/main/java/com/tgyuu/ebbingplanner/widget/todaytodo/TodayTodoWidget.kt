package com.tgyuu.ebbingplanner.widget.todaytodo

import android.content.Context
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
import androidx.glance.layout.padding
import androidx.glance.layout.size
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
import com.tgyuu.ebbingplanner.MainActivity
import com.tgyuu.ebbingplanner.R
import com.tgyuu.ebbingplanner.widget.designsystem.component.EbbingWidgetCheck
import com.tgyuu.ebbingplanner.widget.designsystem.foundation.BACKGROUND_ALPHA
import com.tgyuu.ebbingplanner.widget.designsystem.foundation.EbbingWidgetTheme
import com.tgyuu.ebbingplanner.widget.designsystem.foundation.TEXT_ALPHA
import com.tgyuu.ebbingplanner.widget.designsystem.foundation.THEME
import com.tgyuu.ebbingplanner.widget.todaytodo.TodayTodoWidgetReceiver.Companion.TODO_LISTS
import com.tgyuu.ebbingplanner.widget.util.ADD_TODO
import com.tgyuu.ebbingplanner.widget.util.BaseWidgetPreview
import com.tgyuu.ebbingplanner.widget.util.CheckTodoAction
import com.tgyuu.ebbingplanner.widget.util.EbbingWidgetPreview
import com.tgyuu.ebbingplanner.widget.util.GsonProvider
import com.tgyuu.ebbingplanner.widget.util.destinationKey
import com.tgyuu.ebbingplanner.widget.util.todoIdKey
import kotlinx.datetime.LocalDate

class TodayTodoWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
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
                )
            }
        }
    }
}

@Composable
private fun TodayTodoWidgetContent(
    alpha: Float,
    todoLists: List<TodoSchedule>,
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
            .clickable(onClick = actionStartActivity<MainActivity>())
            .background(
                imageProvider = ImageProvider(backgroundImage),
                colorFilter = ColorFilter.tint(GlanceTheme.colors.onBackground),
            )
            .padding(12.dp)
    ) {
        val headerImage = when (alpha) {
            0.25f -> R.drawable.shape_widget_header_25
            0.5f -> R.drawable.shape_widget_header_50
            0.75f -> R.drawable.shape_widget_header_75
            else -> R.drawable.shape_widget_header_100
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = GlanceModifier.fillMaxWidth()
                .background(
                    imageProvider = ImageProvider(headerImage),
                    colorFilter = ColorFilter.tint(GlanceTheme.colors.onSurfaceVariant),
                )
                .padding(horizontal = 12.dp, vertical = 4.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = GlanceModifier.defaultWeight()
            ) {
                val todoListsDoneSize = todoLists.filter { it.isDone }.size

                Text(
                    text = "오늘 할 일   ",
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Normal,
                        textAlign = TextAlign.Start,
                        color = GlanceTheme.colors.surface,
                    ),
                )
                Text(
                    text = todoListsDoneSize.toString(),
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Normal,
                        textAlign = TextAlign.Start,
                        color = GlanceTheme.colors.primary,
                    ),
                )
                Text(
                    text = " /${todoLists.size}",
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Normal,
                        textAlign = TextAlign.Start,
                        color = GlanceTheme.colors.surface,
                    ),
                )
            }

            Image(
                provider = ImageProvider(com.tgyuu.designsystem.R.drawable.ic_plus),
                contentDescription = null,
                colorFilter = ColorFilter.tint(GlanceTheme.colors.surface),
                modifier = GlanceModifier
                    .size(20.dp)
                    .clickable(
                        actionStartActivity<MainActivity>(
                            actionParametersOf(destinationKey to ADD_TODO)
                        )
                    ),
            )
        }

        if (todoLists.isEmpty()) {
            Text(
                text = "금일 스케줄이 없어요.",
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    fontStyle = FontStyle.Normal,
                    textAlign = TextAlign.Center,
                    fontFamily = FontFamily.Cursive,
                    color = GlanceTheme.colors.surface,
                ),
                modifier = GlanceModifier.fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 30.dp),
            )
        } else {
            LazyColumn(
                modifier = GlanceModifier.fillMaxSize()
                    .padding(12.dp)
            ) {
                items(items = todoLists) { item ->
                    TodoItemRow(
                        todo = item,
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
    modifier: GlanceModifier = GlanceModifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
    ) {
        Spacer(
            modifier = GlanceModifier
                .size(16.dp)
                .cornerRadius(999.dp)
                .background(ColorProvider(Color(todo.color), Color(todo.color)))
        )

        Text(
            text = todo.title,
            style = TextStyle(
                fontSize = 14.sp,
                fontWeight = if (todo.isDone) FontWeight.Bold else FontWeight.Normal,
                color = GlanceTheme.colors.surface,
                textDecoration = if (todo.isDone) TextDecoration.LineThrough else null,
            ),
            maxLines = 2,
            modifier = GlanceModifier.padding(horizontal = 12.dp)
                .defaultWeight(),
        )

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
            todoLists = emptyList()
        )
    }
}

@EbbingWidgetPreview
@Composable
private fun HomeWidgetPreview2() {
    BaseWidgetPreview {
        TodayTodoWidgetContent(
            alpha = 1f,
            todoLists = listOf(
                TodoSchedule(
                    id = 1,
                    infoId = 101,
                    title = "코틀린 공부",
                    tagId = 1,
                    name = "공부",
                    color = 0xFF3282B8.toInt(),
                    date = LocalDate(2025, 5, 8),
                    memo = "Jetpack Compose 위젯",
                    priority = 1,
                    isDone = false,
                    createdAt = LocalDate(2025, 5, 1),
                    infoCreatedAt = LocalDate(2025, 5, 1)
                ),
                TodoSchedule(
                    id = 2,
                    infoId = 102,
                    title = "운동하기",
                    tagId = 2,
                    name = "운동",
                    color = 0xFFFF7490.toInt(),
                    date = LocalDate(2025, 5, 8),
                    memo = "헬스장 1시간",
                    priority = 2,
                    isDone = true,
                    createdAt = LocalDate(2025, 5, 2),
                    infoCreatedAt = LocalDate(2025, 5, 2)
                )
            )
        )
    }
}

