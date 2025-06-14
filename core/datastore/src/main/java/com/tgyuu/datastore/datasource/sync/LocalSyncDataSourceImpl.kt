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

    override val syncedAt: Flow<ZonedDateTime?>
        get() = dataStore.data
            .map { prefs -> prefs[SYNCEDED_AT]?.let { ZonedDateTime.parse(it) } }

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

    override suspend fun setSyncedAt(time: ZonedDateTime?) {
        time?.let {
            dataStore.edit { prefs -> prefs[SYNCEDED_AT] = time.toString() }
        }
    }

    companion object {
        private val UUID = stringPreferencesKey("UUID")
        private val SYNCEDED_AT = stringPreferencesKey("UPLOADED_AT")
    }
}
