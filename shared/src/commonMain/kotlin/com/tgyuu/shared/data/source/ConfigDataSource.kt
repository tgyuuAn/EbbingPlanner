package com.tgyuu.shared.data.source

import com.tgyuu.shared.domain.model.UpdateInfo

/**
 * Config data source interface - platform specific implementations
 * Uses Firebase Remote Config on Android and iOS
 */
interface ConfigDataSource {
    /**
     * Get string value from remote config
     */
    suspend fun getString(key: String, defaultValue: String): String

    /**
     * Get update info from remote config
     */
    suspend fun getUpdateInfo(): UpdateInfo?

    companion object Key {
        const val UPDATE = "update"
        const val HARD_UPDATE = "hard_update"
    }
}
