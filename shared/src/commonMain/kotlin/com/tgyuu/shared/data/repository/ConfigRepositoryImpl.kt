package com.tgyuu.shared.data.repository

import com.tgyuu.shared.domain.model.SortType
import com.tgyuu.shared.domain.model.Theme
import com.tgyuu.shared.domain.model.UpdateInfo
import com.tgyuu.shared.domain.repository.ConfigRepository
import com.tgyuu.shared.platform.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ConfigRepositoryImpl(
    private val settings: Settings,
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

    override suspend fun getAlarmTime(): Pair<Int, Int> {
        val time = settings.getString(KEY_ALARM_TIME, "18:30")
        val parts = time.split(":")
        return Pair(
            parts.getOrNull(0)?.toIntOrNull() ?: 18,
            parts.getOrNull(1)?.toIntOrNull() ?: 30,
        )
    }

    override suspend fun updateAlarmMessage(message: String) {
        settings.putString(KEY_ALARM_MESSAGE, message)
    }

    override suspend fun getAlarmMessage(): String =
        settings.getString(KEY_ALARM_MESSAGE, ConfigRepository.DEFAULT_ALARM_MESSAGE)

    override suspend fun getSoftUpdateInfo(): UpdateInfo {
        // TODO: Implement with remote config
        return UpdateInfo(minVersion = "", noticeMsg = "")
    }

    override suspend fun getHardUpdateInfo(): UpdateInfo {
        // TODO: Implement with remote config
        return UpdateInfo(minVersion = "", noticeMsg = "")
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

    companion object {
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
    }
}
