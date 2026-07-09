package com.tgyuu.network.source

import com.tgyuu.common.suspendRunCatching
import com.tgyuu.domain.model.sync.ConnectInfo
import com.tgyuu.domain.model.sync.ConnectedPeer
import com.tgyuu.domain.model.sync.RepeatCycleForSync
import com.tgyuu.domain.model.sync.TodoInfoForSync
import com.tgyuu.domain.model.sync.TodoScheduleForSync
import com.tgyuu.domain.model.sync.TodoTagForSync
import com.tgyuu.network.model.sync.ConnectDto
import com.tgyuu.network.model.sync.SyncInfoDto
import com.tgyuu.network.model.sync.TodoInfoDto
import com.tgyuu.network.model.sync.TodoScheduleDto
import com.tgyuu.network.model.sync.TodoTagDto
import com.tgyuu.network.model.sync.RepeatCycleDto
import com.tgyuu.network.model.sync.toDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import javax.inject.Inject

private val ISO_FORMAT = DateTimeFormatter.ISO_OFFSET_DATE_TIME

class SupabaseSyncDataSource @Inject constructor(
    private val supabase: SupabaseClient,
) : SyncRemoteDataSource {

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
        suspendRunCatching {
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
    ): ZonedDateTime {
        suspendRunCatching {
            supabase.from(TABLE_SYNC_INFO)
                .upsert(SyncInfoDto(uuid = uuid, deviceName = deviceName))
        }

        val scheduleDtos = schedules.map { it.toDto(uuid) }
        val infoDtos = infos.map { it.toDto(uuid) }
        val repeatCycleDtos = repeatCycles.map { it.toDto(uuid) }
        val tagDtos = tags.map { it.toDto(uuid) }

        val failures = mutableListOf<Throwable>()

        uploadTable(TABLE_REPEAT_CYCLES, repeatCycleDtos) {
            supabase.from(TABLE_REPEAT_CYCLES).upsert(repeatCycleDtos)
        }?.let { failures += it }

        val tagFailure = uploadTable(TABLE_TAGS, tagDtos) {
            supabase.from(TABLE_TAGS).upsert(tagDtos)
        }
        tagFailure?.let { failures += it }

        val infoFailure = if (tagFailure != null) {
            null
        } else {
            uploadTable(TABLE_TODO_INFOS, infoDtos) {
                supabase.from(TABLE_TODO_INFOS).upsert(infoDtos)
            }
        }
        infoFailure?.let { failures += it }

        if (tagFailure == null && infoFailure == null) {
            uploadTable(TABLE_SCHEDULES, scheduleDtos) {
                supabase.from(TABLE_SCHEDULES).upsert(scheduleDtos)
            }?.let { failures += it }
        }

        if (failures.isNotEmpty()) throw failures.first()

        val now = ZonedDateTime.now()
        supabase.from(TABLE_SYNC_INFO)
            .update({
                set("last_updated_at", now.format(ISO_FORMAT))
                set("device_name", deviceName)
            }) {
                filter { eq("uuid", uuid) }
            }

        return now
    }

    private suspend fun uploadTable(
        table: String,
        rows: List<*>,
        upsert: suspend () -> Unit,
    ): Throwable? {
        if (rows.isEmpty()) return null
        return suspendRunCatching { upsert() }
            .exceptionOrNull()
            ?.let { SyncUploadException(table, rows, it) }
    }

    override suspend fun downloadData(
        uuid: String,
        lastSyncTime: Date,
    ): Result<SyncDownloadResult> = coroutineScope {
        suspendRunCatching {
            val lastSyncIso = lastSyncTime.toInstant()
                .atZone(ZoneId.systemDefault())
                .format(ISO_FORMAT)

            val schedulesDeferred = async {
                supabase.from(TABLE_SCHEDULES)
                    .select { filter { eq("uuid", uuid); gt("uploaded_at", lastSyncIso) } }
                    .decodeList<TodoScheduleDto>()
            }

            val todoInfosDeferred = async {
                supabase.from(TABLE_TODO_INFOS)
                    .select { filter { eq("uuid", uuid); gt("uploaded_at", lastSyncIso) } }
                    .decodeList<TodoInfoDto>()
            }

            val repeatCyclesDeferred = async {
                supabase.from(TABLE_REPEAT_CYCLES)
                    .select { filter { eq("uuid", uuid); gt("uploaded_at", lastSyncIso) } }
                    .decodeList<RepeatCycleDto>()
            }

            val tagsDeferred = async {
                supabase.from(TABLE_TAGS)
                    .select { filter { eq("uuid", uuid); gt("uploaded_at", lastSyncIso) } }
                    .decodeList<TodoTagDto>()
            }

            val syncInfoDeferred = async {
                supabase.from(TABLE_SYNC_INFO)
                    .select { filter { eq("uuid", uuid) } }
                    .decodeSingleOrNull<SyncInfoDto>()
            }

            SyncDownloadResult(
                schedules = schedulesDeferred.await().map { it.toDomain() },
                todoInfos = todoInfosDeferred.await().map { it.toDomain() },
                repeatCycles = repeatCyclesDeferred.await().map { it.toDomain() },
                tags = tagsDeferred.await().map { it.toDomain() },
                syncedAt = syncInfoDeferred.await()?.toLastUpdatedAt(),
            )
        }
    }

    override suspend fun generateConnectCode(uuid: String, connectCode: String, deviceName: String): ZonedDateTime {
        val expirationTime = LocalDateTime.now()
            .plusMinutes(10L)
            .atZone(ZoneId.systemDefault())

        supabase.from(TABLE_CONNECT_CODES)
            .upsert(
                ConnectDto(
                    uuid = uuid,
                    connectCode = connectCode,
                    expirationTime = expirationTime.format(ISO_FORMAT),
                    deviceName = deviceName,
                )
            )

        return expirationTime
    }

    override suspend fun connectAnother(connectCode: String): Result<ConnectInfo?> =
        suspendRunCatching {
            supabase.from(TABLE_CONNECT_CODES)
                .select { filter { eq("connect_code", connectCode) } }
                .decodeSingleOrNull<ConnectDto>()
                ?.toDomain()
        }

    override suspend fun markConnected(connectCode: String, connectorUuid: String, connectorDeviceName: String) {
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
        private const val TABLE_SYNC_INFO = "sync_info"
        private const val TABLE_SCHEDULES = "todo_schedules"
        private const val TABLE_TODO_INFOS = "todo_infos"
        private const val TABLE_REPEAT_CYCLES = "repeat_cycles"
        private const val TABLE_TAGS = "todo_tags"
        private const val TABLE_CONNECT_CODES = "connect_codes"
    }
}
