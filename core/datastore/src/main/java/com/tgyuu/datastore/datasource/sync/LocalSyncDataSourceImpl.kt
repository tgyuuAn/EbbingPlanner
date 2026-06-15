package com.tgyuu.datastore.datasource.sync

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
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

    override val connectedUuid: Flow<String?>
        get() = dataStore.data
            .map { prefs -> prefs[CONNECTED_UUID] }

    override val connectCode: Flow<String?>
        get() = dataStore.data
            .map { prefs -> prefs[CONNECT_CODE] }

    override val lastSyncTime: Flow<ZonedDateTime?>
        get() = dataStore.data
            .map { prefs -> prefs[LAST_SYNC_TIME]?.let { ZonedDateTime.parse(it) } }

    override val connectCodeExpirationTime: Flow<ZonedDateTime?>
        get() = dataStore.data
            .map { prefs -> prefs[CONNECT_CODE_EXPIRATION_TIME]?.let { ZonedDateTime.parse(it) } }

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
    override suspend fun setConnectedUuid(uuid: String?) {
        dataStore.edit { prefs ->
            if (uuid == null) {
                // 키를 아예 삭제
                prefs.remove(CONNECTED_UUID)
            } else {
                prefs[CONNECTED_UUID] = uuid
            }
        }
    }

    override suspend fun setConnectCode(linkCode: String?) {
        dataStore.edit { prefs ->
            if (linkCode == null) {
                prefs.remove(CONNECT_CODE)
            } else {
                prefs[CONNECT_CODE] = linkCode
            }
        }
    }

    override suspend fun setLastSyncTime(time: ZonedDateTime?) {
        dataStore.edit { prefs ->
            if (time == null) {
                prefs.remove(LAST_SYNC_TIME)
            } else {
                prefs[LAST_SYNC_TIME] = time.toString()
            }
        }
    }

    override suspend fun setConnectCodeExpirationTime(time: ZonedDateTime?) {
        dataStore.edit { prefs ->
            if (time == null) {
                prefs.remove(CONNECT_CODE_EXPIRATION_TIME)
            } else {
                prefs[CONNECT_CODE_EXPIRATION_TIME] = time.toString()
            }
        }
    }

    override val backupPending: Flow<Boolean>
        get() = dataStore.data
            .map { prefs -> prefs[BACKUP_PENDING] ?: false }

    override suspend fun setBackupPending(pending: Boolean) {
        dataStore.edit { prefs -> prefs[BACKUP_PENDING] = pending }
    }

    companion object {
        private val UUID = stringPreferencesKey("UUID")
        private val CONNECTED_UUID = stringPreferencesKey("CONNECTED_UUID")
        private val LAST_SYNC_TIME = stringPreferencesKey("LAST_SYNC_TIME")
        private val CONNECT_CODE = stringPreferencesKey("CONNECT_CODE")
        private val CONNECT_CODE_EXPIRATION_TIME = stringPreferencesKey("CODE_EXPIRATION_TIME")
        private val BACKUP_PENDING = booleanPreferencesKey("BACKUP_PENDING")
    }
}
