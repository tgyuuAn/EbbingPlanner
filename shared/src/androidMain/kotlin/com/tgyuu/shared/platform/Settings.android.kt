package com.tgyuu.shared.platform

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

actual class Settings(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("ebbing_config", Context.MODE_PRIVATE)

    actual fun getString(key: String, defaultValue: String): String =
        prefs.getString(key, defaultValue) ?: defaultValue

    actual fun putString(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    actual fun getBoolean(key: String, defaultValue: Boolean): Boolean =
        prefs.getBoolean(key, defaultValue)

    actual fun putBoolean(key: String, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
    }

    actual fun getFloat(key: String, defaultValue: Float): Float =
        prefs.getFloat(key, defaultValue)

    actual fun putFloat(key: String, value: Float) {
        prefs.edit().putFloat(key, value).apply()
    }

    actual fun observeString(key: String, defaultValue: String): Flow<String> = callbackFlow {
        trySend(getString(key, defaultValue))
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, changedKey ->
            if (changedKey == key) {
                trySend(getString(key, defaultValue))
            }
        }
        prefs.registerOnSharedPreferenceChangeListener(listener)
        awaitClose { prefs.unregisterOnSharedPreferenceChangeListener(listener) }
    }

    actual fun observeBoolean(key: String, defaultValue: Boolean): Flow<Boolean> = callbackFlow {
        trySend(getBoolean(key, defaultValue))
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, changedKey ->
            if (changedKey == key) {
                trySend(getBoolean(key, defaultValue))
            }
        }
        prefs.registerOnSharedPreferenceChangeListener(listener)
        awaitClose { prefs.unregisterOnSharedPreferenceChangeListener(listener) }
    }

    actual fun observeFloat(key: String, defaultValue: Float): Flow<Float> = callbackFlow {
        trySend(getFloat(key, defaultValue))
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, changedKey ->
            if (changedKey == key) {
                trySend(getFloat(key, defaultValue))
            }
        }
        prefs.registerOnSharedPreferenceChangeListener(listener)
        awaitClose { prefs.unregisterOnSharedPreferenceChangeListener(listener) }
    }

    actual fun remove(key: String) {
        prefs.edit().remove(key).apply()
    }
}
