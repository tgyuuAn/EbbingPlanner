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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TodoAlarmReceiver : BroadcastReceiver() {
    @Inject
    lateinit var notificationHelper: NotificationHelper

    @Inject
    lateinit var todoRepository: TodoRepository

    @Inject
    lateinit var configRepository: ConfigRepository

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onReceive(context: Context, intent: Intent) {
        val job = SupervisorJob()
        val scope = CoroutineScope(Dispatchers.IO + job)

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
                job.cancel()
            }
        }
    }
}
