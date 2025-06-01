package com.tgyuu.ebbingplanner.ui.widget.todaytodo

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
import com.tgyuu.designsystem.foundation.DarkBackground
import com.tgyuu.designsystem.foundation.LightBackground
import com.tgyuu.domain.model.TodoSchedule
import com.tgyuu.ebbingplanner.R
import com.tgyuu.ebbingplanner.ui.MainActivity
import com.tgyuu.ebbingplanner.ui.MainActivity.Companion.ADD_TODO
import com.tgyuu.ebbingplanner.ui.widget.todaytodo.TodayTodoWidgetReceiver.Companion.TODO_LISTS
import com.tgyuu.ebbingplanner.ui.widget.ui.EbbingWidgetCheck
import com.tgyuu.ebbingplanner.ui.widget.util.BaseWidgetPreview
import com.tgyuu.ebbingplanner.ui.widget.util.CheckTodoAction
import com.tgyuu.ebbingplanner.ui.widget.util.EbbingWidgetPreview
import com.tgyuu.ebbingplanner.ui.widget.util.GsonProvider
import com.tgyuu.ebbingplanner.ui.widget.util.destinationKey
import com.tgyuu.ebbingplanner.ui.widget.util.todoIdKey
import java.time.LocalDate

class TodayTodoWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val prefs = currentState<Preferences>()
            val rawJson: String = prefs[TODO_LISTS] ?: "[]"

            val type = object : TypeToken<List<TodoSchedule>>() {}.type
            val todoLists: List<TodoSchedule> = GsonProvider.gson.fromJson(rawJson, type)

            GlanceTheme { TodayTodoWidgetContent(todoLists) }
        }
    }
}

@Composable
private fun TodayTodoWidgetContent(todoLists: List<TodoSchedule>) {
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = GlanceModifier
            .fillMaxSize()
            .background(imageProvider = ImageProvider(R.drawable.shape_widget_background))
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = GlanceModifier.fillMaxWidth()
                .background(imageProvider = ImageProvider(R.drawable.shape_widget_header))
                .padding(horizontal = 12.dp, vertical = 4.dp),
        ) {
            Text(
                text = "오늘 할 일 ${todoLists.size}",
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Normal,
                    textAlign = TextAlign.Start,
                    color = ColorProvider(DarkBackground, LightBackground),

                    ),
                modifier = GlanceModifier.defaultWeight(),
            )

            Image(
                provider = ImageProvider(com.tgyuu.designsystem.R.drawable.ic_plus),
                contentDescription = null,
                colorFilter = ColorFilter.tint(ColorProvider(DarkBackground, LightBackground)),
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
                    color = ColorProvider(DarkBackground, LightBackground),
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
private fun TodoItemRow(
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
                color = ColorProvider(DarkBackground, LightBackground),
                textDecoration = if (todo.isDone) TextDecoration.LineThrough else null,
            ),
            maxLines = 1,
            modifier = GlanceModifier.padding(start = 12.dp)
        )

        Spacer(modifier = GlanceModifier.defaultWeight())

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
            todoLists = emptyList()
        )
    }
}

@EbbingWidgetPreview
@Composable
private fun HomeWidgetPreview2() {
    BaseWidgetPreview {
        TodayTodoWidgetContent(
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

