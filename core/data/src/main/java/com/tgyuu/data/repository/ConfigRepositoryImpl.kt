package com.tgyuu.data.repository

import com.tgyuu.datastore.datasource.user.LocalUserConfigDataSource
import com.tgyuu.domain.model.SortType
import com.tgyuu.domain.model.Theme
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

    override suspend fun shouldShowNotificationNudge(): Boolean =
        localUserConfigDataSource.consumeHasSeenNotificationNudgeScreen()

    override suspend fun setSortType(sortType: SortType) =
        localUserConfigDataSource.setSortType(sortType)

    override suspend fun getSortType(): SortType = localUserConfigDataSource.sortType.first()
    override suspend fun getNotificationEnabled(): Flow<Boolean> =
        localUserConfigDataSource.notificationEnabled

    override suspend fun setNotificationEnabled(enabled: Boolean) =
        localUserConfigDataSource.setNotificationEnabled(enabled)

    override suspend fun setAppTheme(theme: Theme) = localUserConfigDataSource.setAppTheme(theme)
    override suspend fun setWidgetTheme(theme: Theme) =
        localUserConfigDataSource.setWidgetTheme(theme)

    override suspend fun setWidgetBackgroundAlpha(alpha: Float) =
        localUserConfigDataSource.setWidgetBackgroundAlpha(alpha)

    override suspend fun setWidgetTextAlpha(alpha: Float) =
        localUserConfigDataSource.setWidgetTextAlpha(alpha)

    override suspend fun updateAlarmTime(hour: String, minute: String) =
        localUserConfigDataSource.setAlarmTime(hour, minute)

    override suspend fun getAlarmTime(): Pair<Int, Int> =
        localUserConfigDataSource.alarmTime.first()

    override suspend fun updateAlarmMessage(message: String) =
        localUserConfigDataSource.setAlarmMessage(message)

    override suspend fun getAlarmMessage(): String =
        localUserConfigDataSource.alarmMessage.first()

    override suspend fun getSoftUpdateInfo(): UpdateInfo = configDataSource.getReferenceType(
        key = ConfigDataSource.Key.getKey(ConfigDataSource.UPDATE),
        defaultValue = GetUpdateInfoResponse(),
    ).toDomain()

    override suspend fun getHardUpdateInfo(): UpdateInfo = configDataSource.getReferenceType(
        key = ConfigDataSource.Key.getKey(ConfigDataSource.HARD_UPDATE),
        defaultValue = GetUpdateInfoResponse(),
    ).toDomain()

    override fun getAppTheme(): Flow<Theme> = localUserConfigDataSource.appTheme
    override fun getWidgetTheme(): Flow<Theme> = localUserConfigDataSource.widgetTheme
    override fun getWidgetBackgroundAlpha(): Flow<Float> =
        localUserConfigDataSource.widgetBackgroundAlpha

    override fun getWidgetTextAlpha(): Flow<Float> = localUserConfigDataSource.widgetTextAlpha

    override suspend fun getClearSyncFlag(): Boolean =
        localUserConfigDataSource.clearSyncFlag.first()

    override suspend fun markFirstTodoAdded(): Boolean =
        localUserConfigDataSource.markFirstTodoAdded()

    override suspend fun incrementTodoRegisteredCount() =
        localUserConfigDataSource.incrementTodoRegisteredCount()

    override fun getTodoRegisteredCount(): Flow<Int> =
        localUserConfigDataSource.todoRegisteredCount

    override fun getMondayStart(): Flow<Boolean> = localUserConfigDataSource.mondayStart

    override suspend fun setMondayStart(enabled: Boolean) =
        localUserConfigDataSource.setMondayStart(enabled)
}
