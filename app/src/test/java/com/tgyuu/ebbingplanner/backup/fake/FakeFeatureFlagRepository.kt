package com.tgyuu.ebbingplanner.backup.fake

import com.tgyuu.domain.repository.FeatureFlagRepository

class FakeFeatureFlagRepository : FeatureFlagRepository {
    private val flags = mutableMapOf<String, Boolean>()

    fun set(key: String, value: Boolean) {
        flags[key] = value
    }

    override suspend fun fetchAndAwait() {}

    override fun getBoolean(key: String): Boolean = flags[key] ?: false
}
