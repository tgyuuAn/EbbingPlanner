package com.tgyuu.shared.platform

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSUserDefaults
import platform.Foundation.NSUserDefaultsDidChangeNotification

actual class Settings {
    private val defaults: NSUserDefaults = NSUserDefaults.standardUserDefaults

    /**
     * 위젯 확장(별도 프로세스)은 standardUserDefaults 를 읽을 수 없어요.
     * 위젯이 쓰는 설정 키만 App Group suite 에 함께 써 두고(미러링), 위젯은 거기서 읽어요.
     * 위젯 타임라인 갱신은 앱 백그라운드 진입 시 EbbingPlannerApp.swift 의 reloadAllTimelines 로 반영돼요.
     */
    private val widgetDefaults: NSUserDefaults? = NSUserDefaults(suiteName = WIDGET_APP_GROUP)

    init {
        seedWidgetDefaults()
    }

    /** 기존 사용자 값(미러링 도입 이전에 저장된 값)을 App Group 으로 1회 복사해요. */
    private fun seedWidgetDefaults() {
        val group = widgetDefaults ?: return
        WIDGET_MIRRORED_KEYS.forEach { key ->
            val value = defaults.objectForKey(key)
            if (value != null) group.setObject(value, forKey = key) else group.removeObjectForKey(key)
        }
    }

    private fun mirror(key: String, write: NSUserDefaults.() -> Unit) {
        if (key in WIDGET_MIRRORED_KEYS) widgetDefaults?.write()
    }

    actual fun getString(key: String, defaultValue: String): String =
        defaults.stringForKey(key) ?: defaultValue

    actual fun putString(key: String, value: String) {
        defaults.setObject(value, forKey = key)
        mirror(key) { setObject(value, forKey = key) }
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
        mirror(key) { setBool(value, forKey = key) }
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
        mirror(key) { setFloat(value, forKey = key) }
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
        mirror(key) { removeObjectForKey(key) }
    }

    private companion object {
        const val WIDGET_APP_GROUP = "group.com.tgyuu.ebbingplanner"

        /** ConfigRepositoryImpl 의 위젯 관련 키. iOS 위젯(EbbingPlannerWidget.swift WidgetSettings)과 이름이 같아야 해요. */
        val WIDGET_MIRRORED_KEYS = setOf(
            "WIDGET_THEME",
            "WIDGET_BACKGROUND_ALPHA",
            "WIDGET_TEXT_ALPHA",
            "SORT_TYPE",
            "MONDAY_START",
        )
    }
}
