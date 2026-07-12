package com.tgyuu.datastore.datasource.sync

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDateTime

interface LocalSyncDataSource {
    val uuid: Flow<String>
    val connectedUuid: Flow<String?>
    val connectCode: Flow<String?>
    val lastSyncTime: Flow<LocalDateTime?>
    val connectCodeExpirationTime: Flow<LocalDateTime?>
    val peerUuid: Flow<String?>
    val peerDeviceName: Flow<String?>
    val linkCode: Flow<String?>
    suspend fun ensureUUIDExists()
    suspend fun setUuid(uuid: String)
    suspend fun setConnectedUuid(uuid: String?)
    suspend fun setConnectCode(linkCode: String?)
    suspend fun setLastSyncTime(time: LocalDateTime?)
    suspend fun setConnectCodeExpirationTime(time: LocalDateTime?)
    suspend fun setPeer(uuid: String?, deviceName: String?)
    suspend fun setLinkCode(code: String?)
}
