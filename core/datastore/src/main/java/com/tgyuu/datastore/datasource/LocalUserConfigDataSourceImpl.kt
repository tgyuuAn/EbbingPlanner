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
    private val isFirst: Flow<Boolean>
        get() = dataStore.data
            .map { prefs ->
                prefs[IS_FIRST] ?: true
            }

    override val sortType: Flow<SortType>
        get() = dataStore.data
            .map { prefs ->
                val name = prefs[SORT_TYPE] ?: SortType.CREATED.name
                SortType.create(name)
            }

    override val notificationEnabled: Flow<Boolean>
        get() = dataStore.data
            .map { prefs ->
                prefs[NOTIFICATION_ENABLED] ?: true
            }

    override suspend fun consumeIsFirst(): Boolean {
        var firstRun = false
        dataStore.edit { prefs ->
            firstRun = prefs[IS_FIRST] ?: true
            prefs[IS_FIRST] = false
        }
        return firstRun
    }

    override suspend fun setSortType(sortType: SortType) {
        dataStore.edit { prefs -> prefs[SORT_TYPE] = sortType.name }
    }

    override suspend fun setNotificationEnabled(enabled: Boolean) {
        dataStore.edit { prefs -> prefs[NOTIFICATION_ENABLED] = enabled }
    }

    companion object {
        private val SORT_TYPE = stringPreferencesKey("SORT_TYPE")
        private val NOTIFICATION_ENABLED = booleanPreferencesKey("NOTIFICATION_ENABLED")
        private val IS_FIRST = booleanPreferencesKey("IS_FIRST")
    }
}
