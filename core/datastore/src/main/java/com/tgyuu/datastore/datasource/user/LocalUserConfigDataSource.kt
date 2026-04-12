package com.tgyuu.datastore.datasource.user

import com.tgyuu.domain.model.SortType
import com.tgyuu.domain.model.Theme
import kotlinx.coroutines.flow.Flow

interface LocalUserConfigDataSource {
    val clearSyncFlag: Flow<Boolean>
    val sortType: Flow<SortType>
    val notificationEnabled: Flow<Boolean>
    val alarmTime: Flow<Pair<Int, Int>>
    val alarmMessage: Flow<String>
    val appTheme: Flow<Theme>
    val widgetTheme: Flow<Theme>
    val widgetBackgroundAlpha: Flow<Float>
    val widgetTextAlpha: Flow<Float>
    suspend fun consumeIsFirstAppOpen(): Boolean
    suspend fun consumeHasSeenNotificationNudgeScreen(): Boolean
    suspend fun setSortType(sortType: SortType)
    suspend fun setNotificationEnabled(enabled: Boolean)
    suspend fun setAlarmTime(hour: String, minute: String)
    suspend fun setAlarmMessage(message: String)
    suspend fun setAppTheme(theme: Theme)
    suspend fun setWidgetTheme(theme: Theme)
    suspend fun setWidgetBackgroundAlpha(alpha: Float)
    suspend fun setWidgetTextAlpha(alpha: Float)
    suspend fun markFirstTodoAdded(): Boolean
    suspend fun getLastVersion(): Int
    suspend fun setLastVersion(versionCode: Int)
}
