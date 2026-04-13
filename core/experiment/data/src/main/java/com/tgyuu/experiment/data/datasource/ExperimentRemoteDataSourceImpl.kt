package com.tgyuu.experiment.data.datasource

import android.util.Log
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.tgyuu.experiment.data.BuildConfig
import com.tgyuu.experiment.data.datasource.ExperimentRemoteDataSource
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

class ExperimentRemoteDataSourceImpl @Inject constructor(
    private val remoteConfig: FirebaseRemoteConfig,
) : ExperimentRemoteDataSource {

    override suspend fun fetchAllExperimentVariants(experimentKeys: List<String>): Map<String, String> =
        suspendCancellableCoroutine { continuation ->
            remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
                val result = mutableMapOf<String, String>()

                if (task.isSuccessful) {
                    experimentKeys.forEach { key ->
                        val configKey = getConfigKey(key)
                        val value = remoteConfig.getString(configKey)
                        Log.d("test", "key : $key value : $value")

                        if (value.isNotEmpty()) {
                            result[key] = value
                        }
                    }
                }

                continuation.resume(result)
            }
        }

    override suspend fun getExperimentVariant(experimentKey: String): String? =
        suspendCancellableCoroutine { continuation ->
            remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val configKey = getConfigKey(experimentKey)
                    val value = remoteConfig.getString(configKey)
                    continuation.resume(value.takeIf { it.isNotEmpty() })
                } else {
                    continuation.resume(null)
                }
            }
        }

    companion object {
        private fun getConfigKey(experimentKey: String): String =
            "${experimentKey}_${BuildConfig.BUILD_TYPE}"
    }
}
