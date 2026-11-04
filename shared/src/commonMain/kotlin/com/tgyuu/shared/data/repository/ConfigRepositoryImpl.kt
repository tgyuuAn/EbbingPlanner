package com.tgyuu.shared.data.repository

import com.tgyuu.shared.domain.model.CalendarDefaultView
import com.tgyuu.shared.domain.model.SortType
import com.tgyuu.shared.domain.model.Theme
import com.tgyuu.shared.domain.model.UpdateInfo
import com.tgyuu.shared.data.source.DefaultRemoteConfigDataSource
import com.tgyuu.shared.data.source.NoOpNotificationLogDataSource
import com.tgyuu.shared.data.source.NotificationLogDataSource
import com.tgyuu.shared.data.source.RemoteConfigDataSource
import com.tgyuu.shared.data.source.UsageOrderStore
import com.tgyuu.shared.domain.repository.ConfigRepository
import com.tgyuu.shared.platform.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class ConfigRepositoryImpl(
    private val settings: Settings,
    private val usageOrderStore: UsageOrderStore,
    private val remoteConfigDataSource: RemoteConfigDataSource = DefaultRemoteConfigDataSource(),
    private val notificationLogDataSource: NotificationLogDataSource = NoOpNotificationLogDataSource(),
    /** 알림 설정 로그의 uuid (Android localSyncDataSource.uuid 대응) */
    private val uuidProvider: suspend () -> String = { "" },
    private val logScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default),
) : ConfigRepository {

    override fun getAppTheme(): Flow<Theme> =
        settings.observeString(KEY_APP_THEME, Theme.NORMAL.name)
            .map { Theme.create(it) }

    override fun getWidgetTheme(): Flow<Theme> =
        settings.observeString(KEY_WIDGET_THEME, Theme.NORMAL.name)
            .map { Theme.create(it) }

    override fun getWidgetBackgroundAlpha(): Flow<Float> =
        settings.observeFloat(KEY_WIDGET_BG_ALPHA, 1.0f)

    override fun getWidgetTextAlpha(): Flow<Float> =
        settings.observeFloat(KEY_WIDGET_TEXT_ALPHA, 1.0f)

    override suspend fun isFirstAppOpen(): Boolean {
        val isFirst = settings.getBoolean(KEY_IS_FIRST_APP_OPEN, true)
        if (isFirst) {
            settings.putBoolean(KEY_IS_FIRST_APP_OPEN, false)
        }
        return isFirst
    }

    override suspend fun shouldShowNotificationNudge(): Boolean {
        val hasSeen = settings.getBoolean(KEY_HAS_SEEN_NOTIFICATION_NUDGE, false)
        if (!hasSeen) {
            settings.putBoolean(KEY_HAS_SEEN_NOTIFICATION_NUDGE, true)
        }
        return !hasSeen
    }

    override suspend fun setSortType(sortType: SortType) {
        settings.putString(KEY_SORT_TYPE, sortType.name)
    }

    override suspend fun getSortType(): SortType =
        SortType.create(settings.getString(KEY_SORT_TYPE, SortType.CREATED.name))

    override suspend fun getNotificationEnabled(): Flow<Boolean> =
        settings.observeBoolean(KEY_NOTIFICATION_ENABLED, true)

    override suspend fun setNotificationEnabled(enabled: Boolean) {
        settings.putBoolean(KEY_NOTIFICATION_ENABLED, enabled)
        logNotificationConfigSilently()
    }

    override suspend fun setAppTheme(theme: Theme) {
        settings.putString(KEY_APP_THEME, theme.name)
    }

    override suspend fun setWidgetTheme(theme: Theme) {
        settings.putString(KEY_WIDGET_THEME, theme.name)
    }

    override suspend fun setWidgetBackgroundAlpha(alpha: Float) {
        settings.putFloat(KEY_WIDGET_BG_ALPHA, alpha)
    }

    override suspend fun setWidgetTextAlpha(alpha: Float) {
        settings.putFloat(KEY_WIDGET_TEXT_ALPHA, alpha)
    }

    override suspend fun updateAlarmTime(hour: String, minute: String) {
        settings.putString(KEY_ALARM_TIME, "$hour:$minute")
    }

    override suspend fun getAlarmTime(): Pair<Int, Int> =
        ConfigRepository.parseAlarmTime(settings.getString(KEY_ALARM_TIME, "18:30"))

    override suspend fun updateAlarmMessage(message: String) {
        settings.putString(KEY_ALARM_MESSAGE, message)
        logNotificationConfigSilently()
    }

    override suspend fun getAlarmMessage(): String =
        settings.getString(KEY_ALARM_MESSAGE, ConfigRepository.DEFAULT_ALARM_MESSAGE)

    // Android getSoftUpdateInfo/getHardUpdateInfo 대응 (키만 iOS 전용: RemoteConfigDataSource.Key 참고)
    override suspend fun getSoftUpdateInfo(): UpdateInfo =
        getUpdateInfo(RemoteConfigDataSource.Key.IOS_UPDATE)

    override suspend fun getHardUpdateInfo(): UpdateInfo =
        getUpdateInfo(RemoteConfigDataSource.Key.IOS_HARD_UPDATE)

    override suspend fun getAppStoreUrl(): String {
        remoteConfigDataSource.fetchAndActivate()
        return remoteConfigDataSource.getString(RemoteConfigDataSource.Key.IOS_APP_STORE_URL)?.trim().orEmpty()
    }

    private suspend fun getUpdateInfo(key: String): UpdateInfo {
        remoteConfigDataSource.fetchAndActivate()
        val raw = remoteConfigDataSource.getString(key)
        // Android ConfigDataSource.getReferenceType(defaultValue = GetUpdateInfoResponse()) 와 같은 기본값
        val response = raw?.let { runCatching { json.decodeFromString<UpdateInfoResponse>(it) }.getOrNull() }
            ?: UpdateInfoResponse()
        return UpdateInfo(minVersion = response.minVersion, noticeMsg = response.noticeMsg)
    }

    // Android ConfigRepositoryImpl.logNotificationConfigSilently 와 같은 값. 실패는 조용히 무시한다.
    // Android 는 저장 코루틴 안에서 바로 업로드하지만, 오프라인 타임아웃 동안 뒤 작업(알림 재예약 등)이
    // 밀리지 않도록 별도 스코프에서 보낸다.
    private fun logNotificationConfigSilently() {
        logScope.launch { sendNotificationConfigLog() }
    }

    private suspend fun sendNotificationConfigLog() {
        try {
            val uuid = uuidProvider()
            val enabled = getNotificationEnabled().first()
            val (hour, minute) = getAlarmTime()
            val message = getAlarmMessage()

            notificationLogDataSource.logNotificationConfig(
                uuid = uuid,
                enabled = enabled,
                alarmTime = "${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}",
                message = message,
                usesPlaceholder = ConfigRepository.PLACEHOLDER_TOKENS.any { message.contains(it) },
                isDefault = message == ConfigRepository.DEFAULT_ALARM_MESSAGE,
            )
        } catch (e: kotlinx.coroutines.CancellationException) {
            throw e
        } catch (_: Throwable) {
            // silent
        }
    }

    override fun getMondayStart(): Flow<Boolean> =
        settings.observeBoolean(KEY_MONDAY_START, false)

    override suspend fun setMondayStart(enabled: Boolean) {
        settings.putBoolean(KEY_MONDAY_START, enabled)
    }

    override fun getCalendarDefaultView(): Flow<CalendarDefaultView> =
        settings.observeString(KEY_CALENDAR_DEFAULT_VIEW, CalendarDefaultView.MONTHLY.name)
            .map { CalendarDefaultView.create(it) }

    override suspend fun setCalendarDefaultView(view: CalendarDefaultView) {
        settings.putString(KEY_CALENDAR_DEFAULT_VIEW, view.name)
    }

    override fun getAutoBackupEnabled(): Flow<Boolean> =
        settings.observeBoolean(KEY_AUTO_BACKUP_ENABLED, true)

    override suspend fun setAutoBackupEnabled(enabled: Boolean) {
        settings.putBoolean(KEY_AUTO_BACKUP_ENABLED, enabled)
    }

    override suspend fun consumeInAppReview(): Boolean {
        val hasShown = settings.getBoolean(KEY_HAS_SHOWN_IN_APP_REVIEW, false)
        if (!hasShown) {
            settings.putBoolean(KEY_HAS_SHOWN_IN_APP_REVIEW, true)
            return true
        }
        return false
    }

    override suspend fun getClearSyncFlag(): Boolean {
        val flag = settings.getBoolean(KEY_CLEAR_SYNC_FLAG, true)
        if (flag) {
            settings.putBoolean(KEY_CLEAR_SYNC_FLAG, false)
        }
        return flag
    }

    override suspend fun markFirstTodoAdded(): Boolean {
        val hasAdded = settings.getBoolean(KEY_HAS_EVER_ADDED_TODO, false)
        if (!hasAdded) {
            settings.putBoolean(KEY_HAS_EVER_ADDED_TODO, true)
        }
        return !hasAdded
    }

    // Android TODO_REGISTERED_COUNT 대응. Settings에 Int API가 없어 문자열로 저장한다.
    override suspend fun incrementTodoRegisteredCount() {
        val current = settings.getString(KEY_TODO_REGISTERED_COUNT, "0").toIntOrNull() ?: 0
        settings.putString(KEY_TODO_REGISTERED_COUNT, (current + 1).toString())
    }

    override fun getTodoRegisteredCount(): Flow<Int> =
        settings.observeString(KEY_TODO_REGISTERED_COUNT, "0").map { it.toIntOrNull() ?: 0 }

    override suspend fun getTagUsageOrder(): List<Int> = usageOrderStore.tagUsageOrder()

    override suspend fun recordTagUsage(tagId: Int) = usageOrderStore.recordTagUsage(tagId)

    override suspend fun getRepeatCycleUsageOrder(): List<Int> =
        usageOrderStore.repeatCycleUsageOrder()

    override suspend fun recordRepeatCycleUsage(cycleId: Int) =
        usageOrderStore.recordRepeatCycleUsage(cycleId)

    /** Android GetUpdateInfoResponse 와 같은 형식·기본값 */
    @Serializable
    private data class UpdateInfoResponse(
        val minVersion: String = "1.0.0",
        val noticeMsg: String = "여러분의 의견을 반영하여 사용성을 개선했습니다.\n지금 바로 업데이트해 보세요!",
    )

    companion object {
        private val json = Json { ignoreUnknownKeys = true }

        private const val KEY_CLEAR_SYNC_FLAG = "CLEAR_SYNC_FLAG"
        private const val KEY_SORT_TYPE = "SORT_TYPE"
        private const val KEY_NOTIFICATION_ENABLED = "NOTIFICATION_ENABLED"
        private const val KEY_IS_FIRST_APP_OPEN = "IS_FIRST_APP_OPEN"
        private const val KEY_HAS_SEEN_NOTIFICATION_NUDGE = "HAS_SEEN_NOTIFICATION_NUDGE_SCREEN"
        private const val KEY_ALARM_TIME = "ALARM_TIME"
        private const val KEY_ALARM_MESSAGE = "ALARM_MESSAGE"
        private const val KEY_APP_THEME = "APP_THEME"
        private const val KEY_WIDGET_THEME = "WIDGET_THEME"
        private const val KEY_WIDGET_BG_ALPHA = "WIDGET_BACKGROUND_ALPHA"
        private const val KEY_WIDGET_TEXT_ALPHA = "WIDGET_TEXT_ALPHA"
        private const val KEY_HAS_EVER_ADDED_TODO = "HAS_EVER_ADDED_TODO"
        private const val KEY_MONDAY_START = "MONDAY_START"
        private const val KEY_CALENDAR_DEFAULT_VIEW = "CALENDAR_DEFAULT_VIEW"
        private const val KEY_AUTO_BACKUP_ENABLED = "AUTO_BACKUP_ENABLED"
        private const val KEY_HAS_SHOWN_IN_APP_REVIEW = "HAS_SHOWN_IN_APP_REVIEW"
        private const val KEY_TODO_REGISTERED_COUNT = "TODO_REGISTERED_COUNT"
    }
}
