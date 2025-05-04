package com.tgyuu.datastore.datasource

import com.tgyuu.domain.model.SortType
import kotlinx.coroutines.flow.Flow

interface LocalUserConfigDataSource {
    val sortType: Flow<SortType>
    val notificationEnabled: Flow<Boolean>
    suspend fun consumeIsFirstAppOpen(): Boolean
    suspend fun setSortType(sortType: SortType)
    suspend fun setNotificationEnabled(enabled: Boolean)
}
