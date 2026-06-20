package com.tgyuu.shared.data.source

import com.tgyuu.shared.common.currentInstant
import com.tgyuu.shared.domain.model.sync.ConnectInfo
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
 * Supabase(Postgrest) 기반 SyncDataSource. Android(core/network)의 SupabaseSyncDataSource를
 * shared(KMP) SyncDataSource 인터페이스에 맞춰 포팅.
 */
class SupabaseSyncDataSource(
    private val supabase: SupabaseClient,
) : SyncDataSource {

    override suspend fun uploadData(
        uuid: String,
        schedules: List<TodoScheduleForSync>,
        infos: List<TodoInfoForSync>,
        repeatCycles: List<RepeatCycleForSync>,
        tags: List<TodoTagForSync>,
    ): LocalDateTime = coroutineScope {
        supabase.from(TABLE_SYNC_INFO).upsert(SyncInfoDto(uuid = uuid, deviceName = DEVICE_NAME))

        val jobs = listOf(
            async { if (schedules.isNotEmpty()) supabase.from(TABLE_SCHEDULES).upsert(schedules.map { it.toDto(uuid) }) },
            async { if (infos.isNotEmpty()) supabase.from(TABLE_TODO_INFOS).upsert(infos.map { it.toDto(uuid) }) },
            async { if (repeatCycles.isNotEmpty()) supabase.from(TABLE_REPEAT_CYCLES).upsert(repeatCycles.map { it.toDto(uuid) }) },
            async { if (tags.isNotEmpty()) supabase.from(TABLE_TAGS).upsert(tags.map { it.toDto(uuid) }) },
        )
        jobs.forEach { it.await() }

        val nowInstant = currentInstant()
        val now = nowInstant.toLocalDateTime(TimeZone.currentSystemDefault())
        supabase.from(TABLE_SYNC_INFO).update({
            set("last_updated_at", nowInstant.toString())
            set("device_name", DEVICE_NAME)
        }) {
            filter { eq("uuid", uuid) }
        }
        now
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

    override suspend fun generateConnectCode(uuid: String, connectCode: String): LocalDateTime {
        val expirationInstant = currentInstant().plus(10.minutes)
        supabase.from(TABLE_CONNECT_CODES).upsert(
            ConnectDto(
                uuid = uuid,
                connectCode = connectCode,
                expirationTime = expirationInstant.toString(),
                deviceName = DEVICE_NAME,
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

    override suspend fun getLastSyncTime(uuid: String): LocalDateTime? = runCatching {
        supabase.from(TABLE_SYNC_INFO)
            .select { filter { eq("uuid", uuid) } }
            .decodeSingleOrNull<SyncInfoDto>()
            ?.toLastUpdatedAt()
    }.getOrNull()

    private companion object {
        const val DEVICE_NAME = "iOS"
        const val TABLE_SYNC_INFO = "sync_info"
        const val TABLE_SCHEDULES = "todo_schedules"
        const val TABLE_TODO_INFOS = "todo_infos"
        const val TABLE_REPEAT_CYCLES = "repeat_cycles"
        const val TABLE_TAGS = "todo_tags"
        const val TABLE_CONNECT_CODES = "connect_codes"
    }
}
