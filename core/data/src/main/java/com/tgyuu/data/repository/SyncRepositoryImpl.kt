package com.tgyuu.data.repository

import com.tgyuu.datastore.datasource.sync.LocalSyncDataSource
import com.tgyuu.domain.repository.SyncRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class SyncRepositoryImpl @Inject constructor(
    private val localSyncDataSource: LocalSyncDataSource,
) : SyncRepository {
    override suspend fun ensureUUIDExists() = localSyncDataSource.ensureUUIDExists()
    override suspend fun getUUID(): String = localSyncDataSource.uuid.first()
}
