package com.tgyuu.datastore.datasource.user

import com.tgyuu.domain.model.SortType
import com.tgyuu.domain.model.Theme
import kotlinx.coroutines.flow.Flow

interface LocalUserConfigDataSource {
    val sortType: Flow<SortType>
    val notificationEnabled: Flow<Boolean>
    val alarmTime: Flow<Pair<Int, Int>>
    val theme: Flow<Theme>
    suspend fun consumeIsFirstAppOpen(): Boolean
    suspend fun setSortType(sortType: SortType)
    suspend fun setNotificationEnabled(enabled: Boolean)
    suspend fun setAlarmTime(hour: String, minute: String)
    suspend fun setTheme(theme: Theme)
}
