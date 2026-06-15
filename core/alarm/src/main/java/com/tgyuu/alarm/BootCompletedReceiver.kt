package com.tgyuu.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 재부팅 시 AlarmManager 에 등록된 알람이 모두 사라지므로,
 * BOOT_COMPLETED 수신 시 저장된 미래 일정들의 알람을 재등록한다.
 * 앱 업데이트(MY_PACKAGE_REPLACED) 시에도 동일하게 재등록한다.
 */
@AndroidEntryPoint
class BootCompletedReceiver : BroadcastReceiver() {
    @Inject
    lateinit var alarmRescheduler: AlarmRescheduler

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED,
            ACTION_QUICKBOOT_POWERON,
            Intent.ACTION_MY_PACKAGE_REPLACED -> Unit

            else -> return
        }

        val pendingResult = goAsync()
        scope.launch {
            try {
                alarmRescheduler.rescheduleAll()
            } catch (e: Exception) {
                Log.e("BootCompletedReceiver", "부팅 후 알람 재등록 실패", e)
            } finally {
                pendingResult.finish()
                scope.cancel()
            }
        }
    }

    private companion object {
        // 일부 제조사(삼성 등)에서 사용하는 빠른 부팅 액션
        const val ACTION_QUICKBOOT_POWERON = "android.intent.action.QUICKBOOT_POWERON"
    }
}
