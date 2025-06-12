package com.tgyuu.datastore.datasource

import com.tgyuu.domain.model.SortType
import kotlinx.coroutines.flow.Flow

interface LocalUserConfigDataSource {
    val sortType: Flow<SortType>
    val notificationEnabled: Flow<Boolean>
    val alarmTime: Flow<Pair<Int, Int>>
    val uuid: Flow<String>
    suspend fun consumeIsFirstAppOpen(): Boolean
    suspend fun setSortType(sortType: SortType)
    suspend fun setNotificationEnabled(enabled: Boolean)
    suspend fun setAlarmTime(hour: String, minute: String)
    suspend fun ensureUuidExists()
    suspend fun setUuid(uuid: String)
}
