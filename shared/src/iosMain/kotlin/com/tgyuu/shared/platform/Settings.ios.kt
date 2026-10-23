package com.tgyuu.shared.platform

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSUserDefaults
import platform.Foundation.NSUserDefaultsDidChangeNotification

actual class Settings {
    private val defaults: NSUserDefaults = NSUserDefaults.standardUserDefaults

    actual fun getString(key: String, defaultValue: String): String =
        defaults.stringForKey(key) ?: defaultValue

    actual fun putString(key: String, value: String) {
        defaults.setObject(value, forKey = key)
    }

    actual fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return if (defaults.objectForKey(key) != null) {
            defaults.boolForKey(key)
        } else {
            defaultValue
        }
    }

    actual fun putBoolean(key: String, value: Boolean) {
        defaults.setBool(value, forKey = key)
    }

    actual fun getFloat(key: String, defaultValue: Float): Float {
        return if (defaults.objectForKey(key) != null) {
            defaults.floatForKey(key)
        } else {
            defaultValue
        }
    }

    actual fun putFloat(key: String, value: Float) {
        defaults.setFloat(value, forKey = key)
    }

    actual fun observeString(key: String, defaultValue: String): Flow<String> = callbackFlow {
        trySend(getString(key, defaultValue))
        val observer = NSNotificationCenter.defaultCenter.addObserverForName(
            name = NSUserDefaultsDidChangeNotification,
            `object` = defaults,
            queue = null,
        ) { _ ->
            trySend(getString(key, defaultValue))
        }
        awaitClose { NSNotificationCenter.defaultCenter.removeObserver(observer) }
    }

    actual fun observeBoolean(key: String, defaultValue: Boolean): Flow<Boolean> = callbackFlow {
        trySend(getBoolean(key, defaultValue))
        val observer = NSNotificationCenter.defaultCenter.addObserverForName(
            name = NSUserDefaultsDidChangeNotification,
            `object` = defaults,
            queue = null,
        ) { _ ->
            trySend(getBoolean(key, defaultValue))
        }
        awaitClose { NSNotificationCenter.defaultCenter.removeObserver(observer) }
    }

    actual fun observeFloat(key: String, defaultValue: Float): Flow<Float> = callbackFlow {
        trySend(getFloat(key, defaultValue))
        val observer = NSNotificationCenter.defaultCenter.addObserverForName(
            name = NSUserDefaultsDidChangeNotification,
            `object` = defaults,
            queue = null,
        ) { _ ->
            trySend(getFloat(key, defaultValue))
        }
        awaitClose { NSNotificationCenter.defaultCenter.removeObserver(observer) }
    }

    actual fun remove(key: String) {
        defaults.removeObjectForKey(key)
    }
}
