package com.tgyuu.database.source.linked

import com.tgyuu.database.dao.LinkedDevicesDao
import com.tgyuu.database.model.toEntity
import com.tgyuu.domain.model.sync.LinkedDevice
import javax.inject.Inject

class LocalLinkedDeviceDataSourceImpl @Inject constructor(
    private val linkedDevicesDao: LinkedDevicesDao
) : LocalLinkedDeviceDataSource {
    override suspend fun insertLinkedDevice(linkedDevice: LinkedDevice): Long =
        linkedDevicesDao.insertLinkedDevice(linkedDevice.toEntity())

    override suspend fun deleteLinkedDevice(linkedDevice: LinkedDevice) =
        linkedDevicesDao.deleteLinkedDevice(linkedDevice.toEntity())
}
