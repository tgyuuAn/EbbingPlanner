package com.tgyuu.datastore.datasource.sync

import kotlinx.coroutines.flow.Flow

interface LocalSyncDataSource {
    val uuid: Flow<String>
    suspend fun ensureUUIDExists()
    suspend fun setUuid(uuid: String)
}
