package com.tgyuu.domain.repository

import com.tgyuu.domain.model.SortType
import com.tgyuu.domain.model.UpdateInfo
import kotlinx.coroutines.flow.Flow

interface ConfigRepository {
    suspend fun isFirstAppOpen(): Boolean
    suspend fun setSortType(sortType: SortType)
    suspend fun getSortType(): SortType
    suspend fun getNotificationEnabled(): Flow<Boolean>
    suspend fun setNotificationEnabled(enabled: Boolean)
    suspend fun updateAlarmTime(hour: String, minute: String)
    suspend fun getAlarmTime(): Pair<Int, Int>
    suspend fun getUpdateInfo(): Result<UpdateInfo>
}
