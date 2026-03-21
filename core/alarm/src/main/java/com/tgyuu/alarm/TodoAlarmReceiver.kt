package com.tgyuu.alarm

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.annotation.RequiresPermission
import com.tgyuu.common.toLocalDateOrThrow
import com.tgyuu.domain.repository.ConfigRepository
import com.tgyuu.domain.repository.TodoRepository
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class TodoAlarmReceiver : BroadcastReceiver(), KoinComponent {
    private val notificationHelper: NotificationHelper by inject()
    private val todoRepository: TodoRepository by inject()
    private val configRepository: ConfigRepository by inject()

    private val scope = MainScope()

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onReceive(context: Context, intent: Intent) {

        val pendingResult = goAsync()
        scope.launch {
            try {
                val notificationEnabled =
                    configRepository.getNotificationEnabled().firstOrNull() ?: return@launch
                if (!notificationEnabled) return@launch

                val date = intent.getStringExtra("date")?.toLocalDateOrThrow() ?: return@launch
                val schedules = todoRepository.loadSchedulesByDate(date)
                    .filter { !it.isDone }
                    .sortedBy { it.priority }

                if (schedules.isNotEmpty()) {
                    notificationHelper.showTodoNotification(context, schedules, date)
                }
            } catch (e: Exception) {
                Log.e("TodoAlarmReceiver", "알람 처리 중 오류", e)
            } finally {
                pendingResult.finish()
                scope.cancel()
            }
        }
    }
}
