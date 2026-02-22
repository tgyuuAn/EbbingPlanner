package com.tgyuu.home.fake

import com.tgyuu.domain.model.SortType
import com.tgyuu.domain.model.Theme
import com.tgyuu.domain.model.UpdateInfo
import com.tgyuu.domain.repository.ConfigRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeConfigRepository : ConfigRepository {
    private var sortType: SortType = SortType.CREATED
    private var alarmHour: Int = 9
    private var alarmMinute: Int = 0
    private var alarmMessage: String = ConfigRepository.DEFAULT_ALARM_MESSAGE
    private var appTheme: Theme = Theme.NORMAL
    private var widgetTheme: Theme = Theme.NORMAL
    private var widgetBackgroundAlpha: Float = 1f
    private var widgetTextAlpha: Float = 1f
    private var notificationEnabled: Boolean = true
    private var isFirstAppOpen: Boolean = false
    private var shouldShowNotificationNudge: Boolean = false

    override fun getAppTheme(): Flow<Theme> = flowOf(appTheme)

    override fun getWidgetTheme(): Flow<Theme> = flowOf(widgetTheme)

    override fun getWidgetBackgroundAlpha(): Flow<Float> = flowOf(widgetBackgroundAlpha)

    override fun getWidgetTextAlpha(): Flow<Float> = flowOf(widgetTextAlpha)

    override suspend fun isFirstAppOpen(): Boolean = isFirstAppOpen

    override suspend fun shouldShowNotificationNudge(): Boolean = shouldShowNotificationNudge

    override suspend fun setSortType(sortType: SortType) {
        this.sortType = sortType
    }

    override suspend fun getSortType(): SortType = sortType

    override suspend fun getNotificationEnabled(): Flow<Boolean> = flowOf(notificationEnabled)

    override suspend fun setNotificationEnabled(enabled: Boolean) {
        this.notificationEnabled = enabled
    }

    override suspend fun setAppTheme(theme: Theme) {
        this.appTheme = theme
    }

    override suspend fun setWidgetTheme(theme: Theme) {
        this.widgetTheme = theme
    }

    override suspend fun setWidgetBackgroundAlpha(alpha: Float) {
        this.widgetBackgroundAlpha = alpha
    }

    override suspend fun setWidgetTextAlpha(alpha: Float) {
        this.widgetTextAlpha = alpha
    }

    override suspend fun updateAlarmTime(hour: String, minute: String) {
        this.alarmHour = hour.toInt()
        this.alarmMinute = minute.toInt()
    }

    override suspend fun getAlarmTime(): Pair<Int, Int> = Pair(alarmHour, alarmMinute)

    override suspend fun updateAlarmMessage(message: String) {
        this.alarmMessage = message
    }

    override suspend fun getAlarmMessage(): String = alarmMessage

    override suspend fun getSoftUpdateInfo(): UpdateInfo {
        return UpdateInfo(minVersion = "1.0.0", noticeMsg = "")
    }

    override suspend fun getHardUpdateInfo(): UpdateInfo {
        return UpdateInfo(minVersion = "1.0.0", noticeMsg = "")
    }

    override suspend fun getClearSyncFlag(): Boolean = false

    override suspend fun markFirstTodoAdded(): Boolean = false
}
