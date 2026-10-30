package com.tgyuu.shared.domain.repository

import com.tgyuu.shared.domain.model.CalendarDefaultView
import com.tgyuu.shared.domain.model.SortType
import com.tgyuu.shared.domain.model.Theme
import com.tgyuu.shared.domain.model.UpdateInfo
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

    fun getMondayStart(): Flow<Boolean>
    suspend fun setMondayStart(enabled: Boolean)

    fun getCalendarDefaultView(): Flow<CalendarDefaultView>
    suspend fun setCalendarDefaultView(view: CalendarDefaultView)

    fun getAutoBackupEnabled(): Flow<Boolean>
    suspend fun setAutoBackupEnabled(enabled: Boolean)

    suspend fun consumeInAppReview(): Boolean

    suspend fun getClearSyncFlag(): Boolean
    suspend fun markFirstTodoAdded(): Boolean
    suspend fun incrementTodoRegisteredCount()
    fun getTodoRegisteredCount(): Flow<Int>

    suspend fun getTagUsageOrder(): List<Int>
    suspend fun recordTagUsage(tagId: Int)
    suspend fun getRepeatCycleUsageOrder(): List<Int>
    suspend fun recordRepeatCycleUsage(cycleId: Int)

    companion object {
        /** 저장 sentinel(Android와 동일). 화면 표시 시에는 로케일별 default_alarm_message로 치환한다. */
        const val DEFAULT_ALARM_MESSAGE: String = "{할일} 을 확인하고, 잊지 말고 복습하세요!"

        /** 알림 문구 치환 토큰(로케일 무관하게 모두 치환, Android ConfigRepository.PLACEHOLDER_TOKENS 동일) */
        val PLACEHOLDER_TOKENS: List<String> = listOf("{할일}", "{task}", "{タスク}")

        const val DEFAULT_ALARM_HOUR: Int = 18
        const val DEFAULT_ALARM_MINUTE: Int = 30

        /** Android LocalUserConfigDataSourceImpl.alarmTime과 동일: 형식/범위가 잘못되면 18:30으로 폴백 */
        fun parseAlarmTime(raw: String?): Pair<Int, Int> {
            val default = DEFAULT_ALARM_HOUR to DEFAULT_ALARM_MINUTE
            val parts = raw?.split(":")?.takeIf { it.size == 2 } ?: return default
            val h = parts[0].toIntOrNull()
            val m = parts[1].toIntOrNull()
            return if (h != null && m != null && h in 0..23 && m in 0..59) h to m else default
        }

        /** 알림 본문 생성: 모든 플레이스홀더 토큰을 제목으로 치환 (Android NotificationHelperImpl 동일) */
        fun resolveAlarmMessage(template: String, title: String): String =
            PLACEHOLDER_TOKENS.fold(template) { acc, token -> acc.replace(token, title) }
    }
}
