package com.tgyuu.database.source.linked

import com.tgyuu.domain.model.sync.LinkedDevice

interface LocalLinkedDeviceDataSource {
    suspend fun insertLinkedDevice(linkedDevice: LinkedDevice): Long
    suspend fun updateLinkedDevice(linkedDevice: LinkedDevice)
    suspend fun deleteLinkedDevice(linkedDevice: LinkedDevice)
}
