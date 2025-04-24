package com.tgyuu.data.repository

import com.tgyuu.datastore.datasource.LocalUserConfigDataSource
import com.tgyuu.domain.model.SortType
import com.tgyuu.domain.repository.ConfigRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class ConfigRepositoryImpl @Inject constructor(
    private val localUserConfigDataSource: LocalUserConfigDataSource,
) : ConfigRepository {
    override suspend fun setSortType(sortType: SortType) =
        localUserConfigDataSource.setSortType(sortType)

    override suspend fun getSortType(): SortType = localUserConfigDataSource.sortType.first()
}
