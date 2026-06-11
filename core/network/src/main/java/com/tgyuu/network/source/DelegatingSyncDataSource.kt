package com.tgyuu.network.source

import com.tgyuu.domain.model.sync.ConnectInfo
import com.tgyuu.domain.model.sync.RepeatCycleForSync
import com.tgyuu.domain.model.sync.TodoInfoForSync
import com.tgyuu.domain.model.sync.TodoScheduleForSync
import com.tgyuu.domain.model.sync.TodoTagForSync
import com.tgyuu.network.source.firestore.FirestoreSyncDataSource
import java.time.ZonedDateTime
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DelegatingSyncDataSource @Inject constructor(
    private val featureFlagDataSource: FeatureFlagDataSource,
    private val firestoreDataSource: FirestoreSyncDataSource,
    private val supabaseDataSource: SupabaseSyncDataSource,
) : SyncRemoteDataSource {

    private val delegate: SyncRemoteDataSource
        get() = if (featureFlagDataSource.getBooleanSync(FeatureFlagDataSource.USE_SUPABASE_SYNC)) {
            supabaseDataSource
        } else {
            firestoreDataSource
        }

    override suspend fun getSyncInfo(uuid: String): ZonedDateTime? =
        delegate.getSyncInfo(uuid)

    override suspend fun uploadData(
        uuid: String,
        schedules: List<TodoScheduleForSync>,
        infos: List<TodoInfoForSync>,
        repeatCycles: List<RepeatCycleForSync>,
        tags: List<TodoTagForSync>,
    ): ZonedDateTime = delegate.uploadData(uuid, schedules, infos, repeatCycles, tags)

    override suspend fun downloadData(uuid: String, lastSyncTime: Date): Result<SyncDownloadResult> =
        delegate.downloadData(uuid, lastSyncTime)

    override suspend fun generateConnectCode(uuid: String, connectCode: String): ZonedDateTime =
        delegate.generateConnectCode(uuid, connectCode)

    override suspend fun connectAnother(connectCode: String): Result<ConnectInfo?> =
        delegate.connectAnother(connectCode)
}
