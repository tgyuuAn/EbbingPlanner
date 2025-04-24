package com.tgyuu.domain.repository

import com.tgyuu.domain.model.SortType

interface ConfigRepository {
    suspend fun setSortType(sortType: SortType)
    suspend fun getSortType(): SortType
}
