package com.tgyuu.datastore.datasource.user

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.tgyuu.domain.model.CalendarDefaultView
import com.tgyuu.domain.model.SortType
import com.tgyuu.domain.model.Theme
import com.tgyuu.domain.repository.ConfigRepository.Companion.DEFAULT_ALARM_MESSAGE
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LocalUserConfigDataSourceImpl(
    private val dataStore: DataStore<Preferences>,
) : LocalUserConfigDataSource {
    override val clearSyncFlag: Flow<Boolean>
        get() = dataStore.data
            .map { prefs ->
                val flag = prefs[CLEAR_SYNC_FLAG] ?: true
                if (flag) { dataStore.edit { p -> p[CLEAR_SYNC_FLAG] = false } }
                flag
            }

    override val sortType: Flow<SortType>
        get() = dataStore.data
            .map { prefs ->
                val name = prefs[SORT_TYPE] ?: SortType.CREATED.name
                SortType.create(name)
            }

    override val notificationEnabled: Flow<Boolean>
        get() = dataStore.data
            .map { prefs -> prefs[NOTIFICATION_ENABLED] ?: true }

    override val alarmTime: Flow<Pair<Int, Int>>
        get() = dataStore.data.map { prefs ->
            val raw = prefs[ALARM_TIME]

            val default = 18 to 30

            raw?.split(":")
                ?.takeIf { it.size == 2 }
                ?.let { (h, m) -> h.toIntOrNull() to m.toIntOrNull() }
                ?.let { (h, m) ->
                    if (h in 0..23 && m in 0..59) h!! to m!! else default
                } ?: default
        }

    override val alarmMessage: Flow<String>
        get() = dataStore.data
            .map { prefs -> prefs[ALARM_MESSAGE] ?: DEFAULT_ALARM_MESSAGE }

    override val appTheme: Flow<Theme>
        get() = dataStore.data
            .map { prefs ->
                val name = prefs[APP_THEME] ?: Theme.NORMAL.name
                Theme.create(name)
            }

    override val widgetTheme: Flow<Theme>
        get() = dataStore.data
            .map { prefs ->
                val name = prefs[WIDGET_THEME] ?: Theme.NORMAL.name
                Theme.create(name)
            }

    override val widgetBackgroundAlpha: Flow<Float>
        get() = dataStore.data
            .map { prefs -> prefs[WIDGET_BACKGROUND_ALPHA] ?: 1f }

    override val widgetTextAlpha: Flow<Float>
        get() = dataStore.data
            .map { prefs -> prefs[WIDGET_TEXT_ALPHA] ?: 1f }

    override suspend fun consumeIsFirstAppOpen(): Boolean {
        var firstRun = false
        dataStore.edit { prefs ->
            firstRun = prefs[IS_FIRST_APP_OPEN] ?: true
            prefs[IS_FIRST_APP_OPEN] = false
        }
        return firstRun
    }

    override suspend fun consumeHasSeenNotificationNudgeScreen(): Boolean {
        var hasNotSeen = false
        dataStore.edit { prefs ->
            hasNotSeen = !(prefs[HAS_SEEN_NOTIFICATION_NUDGE_SCREEN] ?: false)
            prefs[HAS_SEEN_NOTIFICATION_NUDGE_SCREEN] = true
        }
        return hasNotSeen
    }

    override suspend fun setSortType(sortType: SortType) {
        dataStore.edit { prefs -> prefs[SORT_TYPE] = sortType.name }
    }

    override suspend fun setNotificationEnabled(enabled: Boolean) {
        dataStore.edit { prefs -> prefs[NOTIFICATION_ENABLED] = enabled }
    }

    override suspend fun setAlarmTime(hour: String, minute: String) {
        dataStore.edit { prefs -> prefs[ALARM_TIME] = "$hour:$minute" }
    }

    override suspend fun setAlarmMessage(message: String) {
        dataStore.edit { prefs -> prefs[ALARM_MESSAGE] = message }
    }

    override suspend fun setAppTheme(theme: Theme) {
        dataStore.edit { prefs -> prefs[APP_THEME] = theme.name }
    }

    override suspend fun setWidgetTheme(theme: Theme) {
        dataStore.edit { prefs -> prefs[WIDGET_THEME] = theme.name }
    }

    override suspend fun setWidgetBackgroundAlpha(alpha: Float) {
        dataStore.edit { prefs -> prefs[WIDGET_BACKGROUND_ALPHA] = alpha }
    }

    override suspend fun setWidgetTextAlpha(alpha: Float) {
        dataStore.edit { prefs -> prefs[WIDGET_TEXT_ALPHA] = alpha }
    }

    override val mondayStart: Flow<Boolean>
        get() = dataStore.data
            .map { prefs -> prefs[MONDAY_START] ?: false }

    override suspend fun setMondayStart(enabled: Boolean) {
        dataStore.edit { prefs -> prefs[MONDAY_START] = enabled }
    }

    override val calendarDefaultView: Flow<CalendarDefaultView>
        get() = dataStore.data
            .map { prefs -> CalendarDefaultView.create(prefs[CALENDAR_DEFAULT_VIEW] ?: "MONTHLY") }

    override suspend fun setCalendarDefaultView(view: CalendarDefaultView) {
        dataStore.edit { prefs -> prefs[CALENDAR_DEFAULT_VIEW] = view.name }
    }

    override suspend fun consumeInAppReview(): Boolean {
        var shouldShow = false
        dataStore.edit { prefs ->
            if (!(prefs[HAS_SHOWN_IN_APP_REVIEW] ?: false)) {
                shouldShow = true
                prefs[HAS_SHOWN_IN_APP_REVIEW] = true
            }
        }
        return shouldShow
    }

    override suspend fun markFirstTodoAdded(): Boolean {
        var isFirstTime = false
        dataStore.edit { prefs ->
            if (prefs[HAS_EVER_ADDED_TODO] != true) {
                isFirstTime = true
                prefs[HAS_EVER_ADDED_TODO] = true
            }
        }
        return isFirstTime
    }

    override val todoRegisteredCount: Flow<Int>
        get() = dataStore.data.map { prefs -> prefs[TODO_REGISTERED_COUNT] ?: 0 }

    override suspend fun incrementTodoRegisteredCount() {
        dataStore.edit { prefs ->
            prefs[TODO_REGISTERED_COUNT] = (prefs[TODO_REGISTERED_COUNT] ?: 0) + 1
        }
    }

    companion object {
        private val CLEAR_SYNC_FLAG = booleanPreferencesKey("CLEAR_SYNC_FLAG")
        private val SORT_TYPE = stringPreferencesKey("SORT_TYPE")
        private val NOTIFICATION_ENABLED = booleanPreferencesKey("NOTIFICATION_ENABLED")
        private val IS_FIRST_APP_OPEN = booleanPreferencesKey("IS_FIRST_APP_OPEN")
        private val HAS_SEEN_NOTIFICATION_NUDGE_SCREEN = booleanPreferencesKey("HAS_SEEN_NOTIFICATION_NUDGE_SCREEN")
        private val ALARM_TIME = stringPreferencesKey("ALARM_TIME")
        private val ALARM_MESSAGE = stringPreferencesKey("ALARM_MESSAGE")
        private val APP_THEME = stringPreferencesKey("APP_THEME")
        private val WIDGET_THEME = stringPreferencesKey("WIDGET_THEME")
        private val WIDGET_BACKGROUND_ALPHA = floatPreferencesKey("WIDGET_BACKGROUND_ALPHA")
        private val WIDGET_TEXT_ALPHA = floatPreferencesKey("WIDGET_TEXT_ALPHA")
        private val HAS_EVER_ADDED_TODO = booleanPreferencesKey("HAS_EVER_ADDED_TODO")
        private val HAS_SHOWN_IN_APP_REVIEW = booleanPreferencesKey("HAS_SHOWN_IN_APP_REVIEW")
        private val TODO_REGISTERED_COUNT = intPreferencesKey("TODO_REGISTERED_COUNT")
        private val MONDAY_START = booleanPreferencesKey("MONDAY_START")
        private val CALENDAR_DEFAULT_VIEW = stringPreferencesKey("CALENDAR_DEFAULT_VIEW")
    }
}
