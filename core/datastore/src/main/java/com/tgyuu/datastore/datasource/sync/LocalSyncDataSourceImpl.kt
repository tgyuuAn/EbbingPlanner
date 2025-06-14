package com.tgyuu.datastore.datasource.sync

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Named

class LocalSyncDataSourceImpl @Inject constructor(
    @Named("sync") private val dataStore: DataStore<Preferences>,
) : LocalSyncDataSource {
    override val uuid: Flow<String>
        get() = dataStore.data
            .map { prefs -> prefs[UUID] ?: "INVALID" }

    override suspend fun ensureUUIDExists() {
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
        private val UUID = stringPreferencesKey("UUID")
    }
}
