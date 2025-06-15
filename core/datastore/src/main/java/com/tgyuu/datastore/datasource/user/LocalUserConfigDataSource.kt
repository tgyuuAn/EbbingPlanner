package com.tgyuu.datastore.datasource.user

import com.tgyuu.domain.model.SortType
import kotlinx.coroutines.flow.Flow

interface LocalUserConfigDataSource {
    val sortType: Flow<SortType>
    val notificationEnabled: Flow<Boolean>
    val alarmTime: Flow<Pair<Int, Int>>
    suspend fun consumeIsFirstAppOpen(): Boolean
    suspend fun setSortType(sortType: SortType)
    suspend fun setNotificationEnabled(enabled: Boolean)
    suspend fun setAlarmTime(hour: String, minute: String)
}
