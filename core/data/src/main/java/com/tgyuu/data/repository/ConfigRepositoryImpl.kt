package com.tgyuu.data.repository

import com.tgyuu.datastore.datasource.LocalUserConfigDataSource
import com.tgyuu.domain.model.SortType
import com.tgyuu.domain.model.UpdateInfo
import com.tgyuu.domain.repository.ConfigRepository
import com.tgyuu.network.model.GetUpdateInfoResponse
import com.tgyuu.network.source.ConfigDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class ConfigRepositoryImpl @Inject constructor(
    private val localUserConfigDataSource: LocalUserConfigDataSource,
    private val configDataSource: ConfigDataSource,
) : ConfigRepository {
    override suspend fun isFirstAppOpen(): Boolean =
        localUserConfigDataSource.consumeIsFirstAppOpen()

    override suspend fun setSortType(sortType: SortType) =
        localUserConfigDataSource.setSortType(sortType)

    override suspend fun getSortType(): SortType = localUserConfigDataSource.sortType.first()
    override suspend fun getNotificationEnabled(): Flow<Boolean> =
        localUserConfigDataSource.notificationEnabled

    override suspend fun setNotificationEnabled(enabled: Boolean) =
        localUserConfigDataSource.setNotificationEnabled(enabled)

    override suspend fun updateAlarmTime(hour: String, minute: String) =
        localUserConfigDataSource.setAlarmTime(hour, minute)

    override suspend fun getAlarmTime(): Pair<Int, Int> =
        localUserConfigDataSource.alarmTime.first()

    override suspend fun getUpdateInfo(): Result<UpdateInfo> = runCatching {
        configDataSource.getReferenceType(
            key = ConfigDataSource.Key.getKey(ConfigDataSource.UPDATE),
            defaultValue = GetUpdateInfoResponse(),
        ).toDomain()
    }
}
