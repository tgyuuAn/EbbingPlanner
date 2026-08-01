package com.tgyuu.domain.repository

import com.tgyuu.domain.model.CalendarDefaultView
import com.tgyuu.domain.model.SortType
import com.tgyuu.domain.model.Theme
import com.tgyuu.domain.model.UpdateInfo
import kotlinx.coroutines.flow.Flow

interface ConfigRepository {
    fun getAppTheme(): Flow<Theme>
    fun getWidgetTheme(): Flow<Theme>
    fun getWidgetBackgroundAlpha(): Flow<Float>
    fun getWidgetTextAlpha(): Flow<Float>
    suspend fun isFirstAppOpen(): Boolean
    suspend fun shouldShowNotificationNudge(): Boolean
    suspend fun setSortType(sortType: SortType)
    suspend fun getSortType(): SortType
    suspend fun getNotificationEnabled(): Flow<Boolean>
    suspend fun setNotificationEnabled(enabled: Boolean)
    suspend fun setAppTheme(theme: Theme)
    suspend fun setWidgetTheme(theme: Theme)
    suspend fun setWidgetBackgroundAlpha(alpha: Float)
    suspend fun setWidgetTextAlpha(alpha: Float)
    suspend fun updateAlarmTime(hour: String, minute: String)
    suspend fun getAlarmTime(): Pair<Int, Int>
    suspend fun updateAlarmMessage(message: String)
    suspend fun getAlarmMessage(): String
    suspend fun getSoftUpdateInfo(): UpdateInfo
    suspend fun getHardUpdateInfo(): UpdateInfo

    suspend fun getClearSyncFlag(): Boolean
    suspend fun consumeInAppReview(): Boolean
    suspend fun markFirstTodoAdded(): Boolean
    suspend fun incrementTodoRegisteredCount()
    fun getTodoRegisteredCount(): Flow<Int>
    fun getMondayStart(): Flow<Boolean>
    suspend fun setMondayStart(enabled: Boolean)
    fun getCalendarDefaultView(): Flow<CalendarDefaultView>
    suspend fun setCalendarDefaultView(view: CalendarDefaultView)
    fun getAutoBackupEnabled(): Flow<Boolean>
    suspend fun setAutoBackupEnabled(enabled: Boolean)

    suspend fun getTagUsageOrder(): List<Int>
    suspend fun recordTagUsage(tagId: Int)
    suspend fun getRepeatCycleUsageOrder(): List<Int>
    suspend fun recordRepeatCycleUsage(cycleId: Int)

    companion object {
        const val DEFAULT_ALARM_MESSAGE: String = "{할일} 을 확인하고, 잊지 말고 복습하세요!"

        // 로케일별 표시 토큰(ko {할일} / en {task} / ja {タスク})은 designsystem alarm_placeholder_token 리소스로 표시·검증한다.
        // 아래 목록은 Context가 없는 레이어(알림 치환·analytics)에서 모든 로케일 토큰을 인식하기 위함 — 리소스와 동기화 유지.
        val PLACEHOLDER_TOKENS: List<String> = listOf("{할일}", "{task}", "{タスク}")
    }
}
