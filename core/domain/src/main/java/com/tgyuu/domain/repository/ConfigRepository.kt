package com.tgyuu.domain.repository

import com.tgyuu.domain.model.SortType
import kotlinx.coroutines.flow.Flow

interface ConfigRepository {
    suspend fun isFirstAppOpen(): Boolean
    suspend fun setSortType(sortType: SortType)
    suspend fun getSortType(): SortType
    suspend fun getNotificationEnabled(): Flow<Boolean>
    suspend fun setNotificationEnabled(enabled: Boolean)
}
