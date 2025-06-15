package com.tgyuu.datastore.datasource.sync

import kotlinx.coroutines.flow.Flow
import java.time.ZonedDateTime

interface LocalSyncDataSource {
    val uuid: Flow<String>
    val lastSyncTime: Flow<ZonedDateTime?>
    suspend fun ensureUUIDExists()
    suspend fun setUuid(uuid: String)
    suspend fun setSyncedAt(time: ZonedDateTime?)
}
