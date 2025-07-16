package com.tgyuu.datastore.datasource.user

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.tgyuu.domain.model.SortType
import com.tgyuu.domain.model.Theme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Named

class LocalUserConfigDataSourceImpl @Inject constructor(
    @Named("config") private val dataStore: DataStore<Preferences>,
) : LocalUserConfigDataSource {
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

    override val widgetAlpha: Flow<Float>
        get() = dataStore.data
            .map { prefs -> prefs[WIDGET_ALPHA] ?: 1f }

    override suspend fun consumeIsFirstAppOpen(): Boolean {
        var firstRun = false
        dataStore.edit { prefs ->
            firstRun = prefs[IS_FIRST_APP_OPEN] ?: true
            prefs[IS_FIRST_APP_OPEN] = false
        }
        return firstRun
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

    override suspend fun setAppTheme(theme: Theme) {
        dataStore.edit { prefs -> prefs[APP_THEME] = theme.name }
    }

    override suspend fun setWidgetTheme(theme: Theme) {
        dataStore.edit { prefs -> prefs[WIDGET_THEME] = theme.name }
    }

    override suspend fun setWidgetAlpha(alpha: Float) {
        dataStore.edit { prefs -> prefs[WIDGET_ALPHA] = alpha }
    }

    companion object {
        private val SORT_TYPE = stringPreferencesKey("SORT_TYPE")
        private val NOTIFICATION_ENABLED = booleanPreferencesKey("NOTIFICATION_ENABLED")
        private val IS_FIRST_APP_OPEN = booleanPreferencesKey("IS_FIRST_APP_OPEN")
        private val ALARM_TIME = stringPreferencesKey("ALARM_TIME")
        private val APP_THEME = stringPreferencesKey("APP_THEME")
        private val WIDGET_THEME = stringPreferencesKey("WIDGET_THEME")
        private val WIDGET_ALPHA = floatPreferencesKey("WIDGET_ALPHA")
    }
}
