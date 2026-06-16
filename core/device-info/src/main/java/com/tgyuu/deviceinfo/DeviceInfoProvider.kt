package com.tgyuu.deviceinfo

interface DeviceInfoProvider {
    suspend fun getDeviceName(): String
}
