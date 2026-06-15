package com.tgyuu.alarm

import android.util.Log
import com.tgyuu.domain.repository.ConfigRepository
import com.tgyuu.domain.repository.TodoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 저장된 미래 일정들의 알람을 일괄 재등록한다.
 * 재부팅(BootCompletedReceiver) / 앱 시작(AlarmInitializer) 시점에 호출되어,
 * 시스템이 알람을 비운 경우(재부팅, 절전, 강제종료 등)에도 알림이 누락되지 않도록 한다.
 */
@Singleton
class AlarmRescheduler @Inject constructor(
    private val alarmScheduler: AlarmScheduler,
    private val todoRepository: TodoRepository,
    private val configRepository: ConfigRepository,
) {
    suspend fun rescheduleAll() = withContext(Dispatchers.IO) {
        val notificationEnabled =
            configRepository.getNotificationEnabled().firstOrNull() ?: true
        if (!notificationEnabled) return@withContext

        // 정확 알람 권한이 없으면 재등록을 시도하지 않는다.
        // (권한 없을 때 scheduleDailyExact 가 설정 화면을 띄우므로 반복 호출을 방지)
        if (!alarmScheduler.canScheduleExact()) return@withContext

        val (hour, minute) = configRepository.getAlarmTime()
        val now = System.currentTimeMillis()

        todoRepository.loadUpcomingSchedules(LocalDate.now())
            .map { it.date }
            .distinct()
            .forEach { date ->
                runCatching {
                    val triggerAtMillis = date.atTime(hour, minute)
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli()

                    if (triggerAtMillis <= now) return@forEach

                    alarmScheduler.scheduleDailyExact(
                        date = date,
                        triggerAtMillis = triggerAtMillis,
                    )
                }.onFailure {
                    Log.e("AlarmRescheduler", "알람 재등록 실패: $date", it)
                }
            }
    }
}
