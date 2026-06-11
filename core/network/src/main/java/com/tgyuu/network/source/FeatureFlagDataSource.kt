package com.tgyuu.network.source

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Singleton
class FeatureFlagDataSource @Inject constructor(
    private val remoteConfig: FirebaseRemoteConfig,
) {
    init {
        remoteConfig.setDefaultsAsync(
            mapOf(USE_SUPABASE_SYNC to false)
        )
        remoteConfig.fetchAndActivate()
    }

    fun getBoolean(key: String): Boolean = remoteConfig.getBoolean(key)

    companion object Flag {
        const val USE_SUPABASE_SYNC = "featureflag/sync_use_supabase"
    }
}
