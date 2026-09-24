package com.tgyuu.network.source

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.tgyuu.domain.repository.FeatureFlag
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class FeatureFlagDataSource(
    private val remoteConfig: FirebaseRemoteConfig,
) {
    init {
        remoteConfig.setConfigSettingsAsync(
            FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(FETCH_INTERVAL_SECONDS)
                .build()
        )
        remoteConfig.setDefaultsAsync(
            mapOf(
                FeatureFlag.USE_AUTO_BACKUP to false,
            )
        )
        remoteConfig.fetchAndActivate()
    }

    suspend fun fetchAndAwait() {
        runCatching {
            suspendCancellableCoroutine { cont ->
                remoteConfig.fetchAndActivate().addOnCompleteListener { cont.resume(Unit) }
            }
        }
    }

    fun getBoolean(key: String): Boolean = remoteConfig.getBoolean(key)

    private companion object {
        const val FETCH_INTERVAL_SECONDS = 300L
    }
}
