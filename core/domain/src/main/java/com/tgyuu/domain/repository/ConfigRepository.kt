package com.tgyuu.domain.repository

import com.tgyuu.domain.model.SortType
import com.tgyuu.domain.model.Theme
import com.tgyuu.domain.model.UpdateInfo
import kotlinx.coroutines.flow.Flow

interface ConfigRepository {
    fun getAppTheme(): Flow<Theme>
    fun getWidgetTheme(): Flow<Theme>
    fun getWidgetBackgroundAlpha(): Flow<Float>
    fun getWidgetTextAlpha(): Flow<Float>
    suspend fun isFirstAppOpen(): Boolean
    suspend fun setSortType(sortType: SortType)
    suspend fun getSortType(): SortType
    suspend fun getNotificationEnabled(): Flow<Boolean>
    suspend fun setNotificationEnabled(enabled: Boolean)
    suspend fun setAppTheme(theme: Theme)
    suspend fun setWidgetTheme(theme: Theme)
    suspend fun setWidgetBackgroundAlpha(alpha: Float)
    suspend fun setWidgetTextAlpha(alpha: Float)
    suspend fun updateAlarmTime(hour: String, minute: String)
    suspend fun getAlarmTime(): Pair<Int, Int>
    suspend fun getSoftUpdateInfo(): UpdateInfo
    suspend fun getHardUpdateInfo(): UpdateInfo

    suspend fun getClearSyncFlag(): Boolean
}
