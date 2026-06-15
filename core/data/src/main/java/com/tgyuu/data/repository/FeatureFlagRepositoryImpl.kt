package com.tgyuu.data.repository

import com.tgyuu.domain.repository.FeatureFlagRepository
import com.tgyuu.network.source.FeatureFlagDataSource
import javax.inject.Inject

class FeatureFlagRepositoryImpl @Inject constructor(
    private val featureFlagDataSource: FeatureFlagDataSource,
) : FeatureFlagRepository {
    override suspend fun fetchAndAwait() = featureFlagDataSource.fetchAndAwait()

    override fun getBoolean(key: String): Boolean =
        featureFlagDataSource.getBoolean(key)
}
