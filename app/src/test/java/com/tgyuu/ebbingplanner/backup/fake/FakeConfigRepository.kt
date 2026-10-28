package com.tgyuu.ebbingplanner.backup.fake

import com.tgyuu.domain.model.CalendarDefaultView
import com.tgyuu.domain.model.SortType
import com.tgyuu.domain.model.Theme
import com.tgyuu.domain.model.UpdateInfo
import com.tgyuu.domain.repository.ConfigRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf

class FakeConfigRepository : ConfigRepository {
    private val _autoBackupEnabled = MutableStateFlow(true)

    fun setAutoBackupEnabledValue(enabled: Boolean) {
        _autoBackupEnabled.value = enabled
    }

    override fun getAutoBackupEnabled(): Flow<Boolean> = _autoBackupEnabled
    override suspend fun setAutoBackupEnabled(enabled: Boolean) {
        _autoBackupEnabled.value = enabled
    }

    override fun getAppTheme(): Flow<Theme> = flowOf(Theme.NORMAL)
    override fun getWidgetTheme(): Flow<Theme> = flowOf(Theme.NORMAL)
    override fun getWidgetBackgroundAlpha(): Flow<Float> = flowOf(1f)
    override fun getWidgetTextAlpha(): Flow<Float> = flowOf(1f)
    override suspend fun isFirstAppOpen(): Boolean = false
    override suspend fun shouldShowNotificationNudge(): Boolean = false
    override suspend fun setSortType(sortType: SortType) {}
    override suspend fun getSortType(): SortType = SortType.CREATED
    override suspend fun getNotificationEnabled(): Flow<Boolean> = flowOf(true)
    override suspend fun setNotificationEnabled(enabled: Boolean) {}
    override suspend fun setAppTheme(theme: Theme) {}
    override suspend fun setWidgetTheme(theme: Theme) {}
    override suspend fun setWidgetBackgroundAlpha(alpha: Float) {}
    override suspend fun setWidgetTextAlpha(alpha: Float) {}
    override suspend fun updateAlarmTime(hour: String, minute: String) {}
    override suspend fun getAlarmTime(): Pair<Int, Int> = Pair(9, 0)
    override suspend fun updateAlarmMessage(message: String) {}
    override suspend fun getAlarmMessage(): String = ConfigRepository.DEFAULT_ALARM_MESSAGE
    override suspend fun getSoftUpdateInfo(): UpdateInfo = UpdateInfo("1.0.0", "")
    override suspend fun getHardUpdateInfo(): UpdateInfo = UpdateInfo("1.0.0", "")
    override suspend fun getClearSyncFlag(): Boolean = false
    override suspend fun consumeInAppReview(): Boolean = false
    override suspend fun markFirstTodoAdded(): Boolean = false
    override suspend fun incrementTodoRegisteredCount() {}
    override fun getTodoRegisteredCount(): Flow<Int> = flowOf(0)
    override fun getMondayStart(): Flow<Boolean> = flowOf(false)
    override suspend fun setMondayStart(enabled: Boolean) {}
    override fun getCalendarDefaultView(): Flow<CalendarDefaultView> =
        flowOf(CalendarDefaultView.MONTHLY)
    override suspend fun setCalendarDefaultView(view: CalendarDefaultView) {}
    override suspend fun getTagUsageOrder(): List<Int> = emptyList()
    override suspend fun recordTagUsage(tagId: Int) {}
    override suspend fun getRepeatCycleUsageOrder(): List<Int> = emptyList()
    override suspend fun recordRepeatCycleUsage(cycleId: Int) {}
}
