package com.tgyuu.datastore.datasource.sync

import kotlinx.coroutines.flow.Flow
import java.time.ZonedDateTime

interface LocalSyncDataSource {
    val uuid: Flow<String>
    val connectedUuid: Flow<String?>
    val connectCode: Flow<String?>
    val lastSyncTime: Flow<ZonedDateTime?>
    val connectCodeExpirationTime: Flow<ZonedDateTime?>
    val peerUuid: Flow<String?>
    val peerDeviceName: Flow<String?>
    val linkCode: Flow<String?>
    suspend fun ensureUUIDExists()
    suspend fun setUuid(uuid: String)
    suspend fun setConnectedUuid(uuid: String?)
    suspend fun setConnectCode(linkCode: String?)
    suspend fun setLastSyncTime(time: ZonedDateTime?)
    suspend fun setConnectCodeExpirationTime(time: ZonedDateTime?)
    suspend fun setPeer(uuid: String?, deviceName: String?)
    suspend fun setLinkCode(code: String?)
}
