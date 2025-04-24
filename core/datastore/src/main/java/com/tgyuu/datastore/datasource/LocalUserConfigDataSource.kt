package com.tgyuu.datastore.datasource

import com.tgyuu.domain.model.SortType
import kotlinx.coroutines.flow.Flow

interface LocalUserConfigDataSource {
    val sortType: Flow<SortType>
    suspend fun setSortType(sortType: SortType)
}
