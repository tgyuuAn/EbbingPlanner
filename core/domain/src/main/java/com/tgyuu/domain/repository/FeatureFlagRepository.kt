package com.tgyuu.domain.repository

interface FeatureFlagRepository {
    suspend fun fetchAndAwait()
    fun getBoolean(key: String): Boolean
}
