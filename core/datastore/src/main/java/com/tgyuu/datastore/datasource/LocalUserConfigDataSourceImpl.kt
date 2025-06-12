package com.tgyuu.datastore.datasource

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.tgyuu.domain.model.SortType
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

    override val uuid: Flow<String>
        get() = dataStore.data
            .map { prefs -> prefs[UUID] ?: "INVALID" }

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

    override suspend fun ensureUuidExists() {
        dataStore.edit { prefs ->
            val savedUuid = prefs[UUID]
            if (savedUuid == null) {
                prefs[UUID] = java.util.UUID.randomUUID().toString()
            }
        }
    }

    override suspend fun setUuid(uuid: String) {
        dataStore.edit { prefs -> prefs[UUID] = uuid }
    }

    companion object {
        private val SORT_TYPE = stringPreferencesKey("SORT_TYPE")
        private val NOTIFICATION_ENABLED = booleanPreferencesKey("NOTIFICATION_ENABLED")
        private val IS_FIRST_APP_OPEN = booleanPreferencesKey("IS_FIRST_APP_OPEN")
        private val ALARM_TIME = stringPreferencesKey("ALARM_TIME")
        private val UUID = stringPreferencesKey("UUID")
    }
}
