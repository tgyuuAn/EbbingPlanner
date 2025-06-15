package com.tgyuu.datastore.datasource.sync

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.ZonedDateTime
import javax.inject.Inject
import javax.inject.Named

class LocalSyncDataSourceImpl @Inject constructor(
    @Named("sync") private val dataStore: DataStore<Preferences>,
) : LocalSyncDataSource {
    override val uuid: Flow<String>
        get() = dataStore.data
            .map { prefs -> prefs[UUID] ?: "INVALID" }

    override val linkedUuid: Flow<String?>
        get() = dataStore.data
            .map { prefs -> prefs[LINKED_UUID] }


    override val lastSyncTime: Flow<ZonedDateTime?>
        get() = dataStore.data
            .map { prefs -> prefs[SYNCED_AT]?.let { ZonedDateTime.parse(it) } }

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

    override suspend fun setLinkedUuid(uuid: String) {
        dataStore.edit { prefs -> prefs[LINKED_UUID] = uuid }
    }

    override suspend fun setSyncedAt(time: ZonedDateTime?) {
        time?.let {
            dataStore.edit { prefs -> prefs[SYNCED_AT] = time.toString() }
        }
    }

    companion object {
        private val UUID = stringPreferencesKey("UUID")
        private val LINKED_UUID = stringPreferencesKey("LINKED_UUID")
        private val SYNCED_AT = stringPreferencesKey("SYNCED_AT")
    }
}
