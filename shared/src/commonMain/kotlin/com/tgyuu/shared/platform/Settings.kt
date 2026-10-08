package com.tgyuu.shared.platform

import kotlinx.coroutines.flow.Flow

expect class Settings {
    fun getString(key: String, defaultValue: String): String
    fun putString(key: String, value: String)
    fun getBoolean(key: String, defaultValue: Boolean): Boolean
    fun putBoolean(key: String, value: Boolean)
    fun getFloat(key: String, defaultValue: Float): Float
    fun putFloat(key: String, value: Float)
    fun observeString(key: String, defaultValue: String): Flow<String>
    fun observeBoolean(key: String, defaultValue: Boolean): Flow<Boolean>
    fun observeFloat(key: String, defaultValue: Float): Flow<Float>
    fun remove(key: String)
}
