package com.tgyuu.shared.data.source

import com.tgyuu.shared.common.currentInstant
import com.tgyuu.shared.domain.model.sync.ConnectInfo
import com.tgyuu.shared.domain.model.sync.ConnectedPeer
import com.tgyuu.shared.domain.model.sync.RepeatCycleForSync
import com.tgyuu.shared.domain.model.sync.TodoInfoForSync
import com.tgyuu.shared.domain.model.sync.TodoScheduleForSync
import com.tgyuu.shared.domain.model.sync.TodoTagForSync
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration.Companion.minutes

/**
 * 업로드 중 어떤 테이블에서 실패했는지 진단하기 위한 예외 (Android core/network 미러링).
 */
class SyncUploadException(
    val table: String,
    val rowCount: Int,
    cause: Throwable,
) : Exception("Sync upload failed for table=$table rows=$rowCount", cause)

/**
 * Supabase(Postgrest) 기반 SyncDataSource. Android(core/network)의 SupabaseSyncDataSource를
 * shared(KMP) SyncDataSource 인터페이스에 맞춰 포팅.
 */
class SupabaseSyncDataSource(
    private val supabase: SupabaseClient,
) : SyncDataSource {

    override suspend fun getSyncInfo(uuid: String): SyncInfoResult? {
        val dto = supabase.from(TABLE_SYNC_INFO)
            .select { filter { eq("uuid", uuid) } }
            .decodeSingleOrNull<SyncInfoDto>()
            ?: return null

        return SyncInfoResult(
            lastUpdatedAt = dto.toLastUpdatedAt(),
            deviceName = dto.deviceName,
        )
    }

    override suspend fun findSyncInfosByUuidPrefix(prefix: String): Result<List<SyncDeviceMatch>> =
        runCatching {
            supabase.from(TABLE_SYNC_INFO)
                .select { filter { ilike("uuid", "$prefix%") } }
                .decodeList<SyncInfoDto>()
                .map { SyncDeviceMatch(uuid = it.uuid, deviceName = it.deviceName) }
        }

    override suspend fun uploadData(
        uuid: String,
        deviceName: String,
        schedules: List<TodoScheduleForSync>,
        infos: List<TodoInfoForSync>,
        repeatCycles: List<RepeatCycleForSync>,
        tags: List<TodoTagForSync>,
    ): LocalDateTime {
        runCatching {
            supabase.from(TABLE_SYNC_INFO)
                .upsert(SyncInfoDto(uuid = uuid, deviceName = deviceName))
        }

        // upsert 업데이트 시 DB가 uploaded_at을 갱신해주지 않으므로 클라이언트가 직접 찍어준다.
        // (증분 다운로드가 uploaded_at > lastSyncTime 필터를 사용)
        val nowInstant = currentInstant()
        val uploadedAtIso = nowInstant.toString()

        val scheduleDtos = schedules.map { it.toDto(uuid).copy(uploadedAt = uploadedAtIso) }
        val infoDtos = infos.map { it.toDto(uuid).copy(uploadedAt = uploadedAtIso) }
        val repeatCycleDtos = repeatCycles.map { it.toDto(uuid).copy(uploadedAt = uploadedAtIso) }
        val tagDtos = tags.map { it.toDto(uuid).copy(uploadedAt = uploadedAtIso) }

        val failures = mutableListOf<Throwable>()

        uploadTable(TABLE_REPEAT_CYCLES, repeatCycleDtos.size) {
            supabase.from(TABLE_REPEAT_CYCLES).upsert(repeatCycleDtos)
        }?.let { failures += it }

        val tagFailure = uploadTable(TABLE_TAGS, tagDtos.size) {
            supabase.from(TABLE_TAGS).upsert(tagDtos)
        }
        tagFailure?.let { failures += it }

        val infoFailure = if (tagFailure != null) {
            null
        } else {
            uploadTable(TABLE_TODO_INFOS, infoDtos.size) {
                supabase.from(TABLE_TODO_INFOS).upsert(infoDtos)
            }
        }
        infoFailure?.let { failures += it }

        if (tagFailure == null && infoFailure == null) {
            uploadTable(TABLE_SCHEDULES, scheduleDtos.size) {
                supabase.from(TABLE_SCHEDULES).upsert(scheduleDtos)
            }?.let { failures += it }
        }

        if (failures.isNotEmpty()) throw failures.first()

        supabase.from(TABLE_SYNC_INFO)
            .update({
                set("last_updated_at", uploadedAtIso)
                set("device_name", deviceName)
            }) {
                filter { eq("uuid", uuid) }
            }

        return nowInstant.toLocalDateTime(TimeZone.currentSystemDefault())
    }

    private suspend fun uploadTable(
        table: String,
        rowCount: Int,
        upsert: suspend () -> Unit,
    ): Throwable? {
        if (rowCount == 0) return null
        return runCatching { upsert() }
            .exceptionOrNull()
            ?.let { SyncUploadException(table, rowCount, it) }
    }

    override suspend fun downloadData(
        uuid: String,
        lastSyncTime: LocalDateTime,
    ): Result<SyncData> = runCatching {
        coroutineScope {
            val lastSyncIso = lastSyncTime.toUtcIsoString()

            val schedules = async {
                supabase.from(TABLE_SCHEDULES)
                    .select { filter { eq("uuid", uuid); gt("uploaded_at", lastSyncIso) } }
                    .decodeList<TodoScheduleDto>()
            }
            val infos = async {
                supabase.from(TABLE_TODO_INFOS)
                    .select { filter { eq("uuid", uuid); gt("uploaded_at", lastSyncIso) } }
                    .decodeList<TodoInfoDto>()
            }
            val cycles = async {
                supabase.from(TABLE_REPEAT_CYCLES)
                    .select { filter { eq("uuid", uuid); gt("uploaded_at", lastSyncIso) } }
                    .decodeList<RepeatCycleDto>()
            }
            val tags = async {
                supabase.from(TABLE_TAGS)
                    .select { filter { eq("uuid", uuid); gt("uploaded_at", lastSyncIso) } }
                    .decodeList<TodoTagDto>()
            }
            val info = async {
                supabase.from(TABLE_SYNC_INFO)
                    .select { filter { eq("uuid", uuid) } }
                    .decodeSingleOrNull<SyncInfoDto>()
            }

            SyncData(
                schedules = schedules.await().map { it.toDomain() },
                todoInfos = infos.await().map { it.toDomain() },
                repeatCycles = cycles.await().map { it.toDomain() },
                tags = tags.await().map { it.toDomain() },
                syncedAt = info.await()?.toLastUpdatedAt(),
            )
        }
    }

    override suspend fun generateConnectCode(
        uuid: String,
        connectCode: String,
        deviceName: String,
    ): LocalDateTime {
        val expirationInstant = currentInstant().plus(10.minutes)
        supabase.from(TABLE_CONNECT_CODES).upsert(
            ConnectDto(
                uuid = uuid,
                connectCode = connectCode,
                expirationTime = expirationInstant.toString(),
                deviceName = deviceName,
            ),
        )
        return expirationInstant.toLocalDateTime(TimeZone.currentSystemDefault())
    }

    override suspend fun connectAnother(connectCode: String): Result<ConnectInfo?> = runCatching {
        supabase.from(TABLE_CONNECT_CODES)
            .select { filter { eq("connect_code", connectCode) } }
            .decodeSingleOrNull<ConnectDto>()
            ?.toDomain()
    }

    override suspend fun markConnected(
        connectCode: String,
        connectorUuid: String,
        connectorDeviceName: String,
    ) {
        supabase.from(TABLE_CONNECT_CODES).update(
            {
                set("connected_uuid", connectorUuid)
                set("connected_device_name", connectorDeviceName)
            },
        ) {
            filter { eq("connect_code", connectCode) }
        }
    }

    override suspend fun getConnectedPeer(connectCode: String): ConnectedPeer? {
        val dto = supabase.from(TABLE_CONNECT_CODES)
            .select { filter { eq("connect_code", connectCode) } }
            .decodeSingleOrNull<ConnectDto>()
            ?: return null

        val peerUuid = dto.connectedUuid
        val peerName = dto.connectedDeviceName
        if (peerUuid.isNullOrEmpty() || peerName.isNullOrEmpty()) return null

        return ConnectedPeer(
            uuid = peerUuid,
            deviceName = peerName,
        )
    }

    override suspend fun deleteConnectCode(connectCode: String) {
        supabase.from(TABLE_CONNECT_CODES).delete {
            filter { eq("connect_code", connectCode) }
        }
    }

    private companion object {
        const val TABLE_SYNC_INFO = "sync_info"
        const val TABLE_SCHEDULES = "todo_schedules"
        const val TABLE_TODO_INFOS = "todo_infos"
        const val TABLE_REPEAT_CYCLES = "repeat_cycles"
        const val TABLE_TAGS = "todo_tags"
        const val TABLE_CONNECT_CODES = "connect_codes"
    }
}
