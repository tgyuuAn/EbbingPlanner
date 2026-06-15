package com.tgyuu.alarm

import com.tgyuu.common.initializer.Initializer
import com.tgyuu.common.initializer.Initializer.Companion.PRIORITY_LOW
import javax.inject.Inject

/**
 * 앱 시작 시 저장된 미래 일정들의 알람을 재등록한다.
 * 절전/강제종료 등으로 알람이 취소된 경우를 대비한 복구 로직.
 */
class AlarmInitializer @Inject constructor(
    private val alarmRescheduler: AlarmRescheduler,
) : Initializer {
    override val priority: Int
        get() = PRIORITY_LOW

    override suspend fun initialize() {
        alarmRescheduler.rescheduleAll()
    }
}
