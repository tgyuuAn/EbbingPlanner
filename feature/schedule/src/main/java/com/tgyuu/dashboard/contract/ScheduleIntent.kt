package com.tgyuu.dashboard.contract

import com.tgyuu.common.base.UiIntent
import com.tgyuu.domain.model.TodoInfo
import com.tgyuu.domain.model.TodoSchedule
import com.tgyuu.domain.model.TodoTag

sealed interface ScheduleIntent : UiIntent {
    data class OnTagClick(val tag: TodoTag) : ScheduleIntent
    data class OnInfoClick(val todoInfo: TodoInfo) : ScheduleIntent
    data class OnScheduleClick(val schedule: TodoSchedule) : ScheduleIntent
}
