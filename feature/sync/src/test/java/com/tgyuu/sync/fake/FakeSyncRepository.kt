package com.tgyuu.sync.fake

import com.tgyuu.domain.model.sync.ConnectInfo
import com.tgyuu.domain.repository.SyncRepository
import java.time.LocalDateTime
import java.time.ZonedDateTime

class FakeSyncRepository : SyncRepository {
    var uuid: String = "test-uuid-1234"
    var connectedUuid: String? = null
    var myConnectCode: String? = null
    var connectCodeExpiration: ZonedDateTime? = null

    var shouldGenerateFail: Boolean = false
    var shouldConnectFail: Boolean = false
    var connectResult: ConnectInfo? = ConnectInfo(
        uuid = "other-uuid-5678",
        connectCode = "testcode123",
        connectCodeExpirationTime = LocalDateTime.now().plusMinutes(10),
    )

    val generatedCodes = mutableListOf<String>()

    override suspend fun ensureUUIDExists() {}

    override suspend fun getUuid(): String = uuid

    override suspend fun getConnectedUuid(): String? = connectedUuid

    override suspend fun getServerLastUpdatedAt(): ZonedDateTime? = null

    override suspend fun getLocalSyncedAt(): ZonedDateTime? = null

    override suspend fun syncUpData(): ZonedDateTime = ZonedDateTime.now()

    override suspend fun generateConnectCode(connectCode: String): ZonedDateTime {
        if (shouldGenerateFail) throw Exception("서버 오류")
        generatedCodes += connectCode
        myConnectCode = connectCode
        val expiration = ZonedDateTime.now().plusMinutes(10)
        connectCodeExpiration = expiration
        return expiration
    }

    override suspend fun getMyConnectCode(): String? = myConnectCode

    override suspend fun getConnectCodeExpiration(): ZonedDateTime? = connectCodeExpiration

    override suspend fun connectAnother(connectCode: String): ConnectInfo? {
        if (shouldConnectFail) throw Exception("연동 오류")
        return connectResult
    }

    override suspend fun disconnectAnother() {
        connectedUuid = null
    }
}
