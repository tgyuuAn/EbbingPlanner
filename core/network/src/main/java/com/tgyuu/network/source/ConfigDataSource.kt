package com.tgyuu.network.source

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigValue
import com.google.firebase.remoteconfig.get
import com.tgyuu.network.BuildConfig
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.serialization.json.Json
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class ConfigDataSource @Inject constructor(
    private val remoteConfig: FirebaseRemoteConfig,
    val json: Json,
) {
    suspend inline fun <reified T> getReferenceType(key: String, defaultValue: T): T {
        return getReferenceType<T>(key) ?: defaultValue
    }

    suspend inline fun <reified T> getReferenceType(key: String): T? {
        return json.decodeFromString<T>(getString(key, ""))
    }

    suspend fun getString(key: String, defaultValue: String): String =
        getValue(key)?.asString() ?: defaultValue

    suspend fun getValue(key: String): FirebaseRemoteConfigValue? =
        suspendCancellableCoroutine { continuation ->
            remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    continuation.resume(remoteConfig[key])
                } else {
                    task.exception?.let { continuation.resumeWithException(it) }
                        ?: continuation.resumeWithException(Exception("Unknown error occurred when use remoteConfig"))
                }
            }
        }

    companion object Key {
        const val UPDATE = "update"

        fun getKey(key: String): String = "${key}_AN_${BuildConfig.BUILD_TYPE}"
    }
}
