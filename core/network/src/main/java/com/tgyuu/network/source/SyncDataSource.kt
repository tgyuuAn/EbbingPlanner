package com.tgyuu.network.source

import com.tgyuu.common.suspendRunCatching
import com.tgyuu.domain.model.sync.RepeatCycleForSync
import com.tgyuu.domain.model.sync.TodoInfoForSync
import com.tgyuu.domain.model.sync.TodoScheduleForSync
import com.tgyuu.domain.model.sync.TodoTagForSync
import com.tgyuu.network.model.sync.ConnectDto
import com.tgyuu.network.model.sync.RepeatCycleDto
import com.tgyuu.network.model.sync.SyncDataDto
import com.tgyuu.network.model.sync.SyncInfoDto
import com.tgyuu.network.model.sync.TodoInfoDto
import com.tgyuu.network.model.sync.TodoScheduleDto
import com.tgyuu.network.model.sync.TodoTagDto
import com.tgyuu.network.model.sync.toDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

private val ISO_FORMAT = DateTimeFormatter.ISO_OFFSET_DATE_TIME

class SyncDataSource @Inject constructor(
    private val supabase: SupabaseClient,
) {
    suspend fun getSyncInfo(uuid: String): SyncInfoDto {
        return supabase.from(TABLE_SYNC_INFO)
            .select { filter { eq("uuid", uuid) } }
            .decodeSingle<SyncInfoDto>()
    }

    suspend fun uploadData(
        uuid: String,
        schedules: List<TodoScheduleForSync>,
        infos: List<TodoInfoForSync>,
        repeatCycles: List<RepeatCycleForSync>,
        tags: List<TodoTagForSync>,
    ): ZonedDateTime = coroutineScope {
        // sync_info에 uuid가 없으면 FK 제약조건 위반이므로 먼저 upsert
        supabase.from(TABLE_SYNC_INFO)
            .upsert(SyncInfoDto(uuid = uuid))

        val schedulesJob = async {
            if (schedules.isNotEmpty()) {
                supabase.from(TABLE_SCHEDULES)
                    .upsert(schedules.map { it.toDto(uuid) })
            }
        }

        val infosJob = async {
            if (infos.isNotEmpty()) {
                supabase.from(TABLE_TODO_INFOS)
                    .upsert(infos.map { it.toDto(uuid) })
            }
        }

        val repeatCyclesJob = async {
            if (repeatCycles.isNotEmpty()) {
                supabase.from(TABLE_REPEAT_CYCLES)
                    .upsert(repeatCycles.map { it.toDto(uuid) })
            }
        }

        val tagsJob = async {
            if (tags.isNotEmpty()) {
                supabase.from(TABLE_TAGS)
                    .upsert(tags.map { it.toDto(uuid) })
            }
        }

        schedulesJob.await()
        infosJob.await()
        repeatCyclesJob.await()
        tagsJob.await()

        // last_updated_at 갱신
        val now = ZonedDateTime.now()
        supabase.from(TABLE_SYNC_INFO)
            .update({ set("last_updated_at", now.format(ISO_FORMAT)) }) {
                filter { eq("uuid", uuid) }
            }

        now
    }

    suspend fun downloadData(
        uuid: String,
        lastSyncTime: java.util.Date,
    ): Result<SyncDataDto> = coroutineScope {
        suspendRunCatching {
            val lastSyncIso = lastSyncTime.toInstant()
                .atZone(ZoneId.systemDefault())
                .format(ISO_FORMAT)

            val schedulesDeferred = async {
                supabase.from(TABLE_SCHEDULES)
                    .select {
                        filter {
                            eq("uuid", uuid)
                            gt("uploaded_at", lastSyncIso)
                        }
                    }
                    .decodeList<TodoScheduleDto>()
            }

            val todoInfosDeferred = async {
                supabase.from(TABLE_TODO_INFOS)
                    .select {
                        filter {
                            eq("uuid", uuid)
                            gt("uploaded_at", lastSyncIso)
                        }
                    }
                    .decodeList<TodoInfoDto>()
            }

            val repeatCyclesDeferred = async {
                supabase.from(TABLE_REPEAT_CYCLES)
                    .select {
                        filter {
                            eq("uuid", uuid)
                            gt("uploaded_at", lastSyncIso)
                        }
                    }
                    .decodeList<RepeatCycleDto>()
            }

            val tagsDeferred = async {
                supabase.from(TABLE_TAGS)
                    .select {
                        filter {
                            eq("uuid", uuid)
                            gt("uploaded_at", lastSyncIso)
                        }
                    }
                    .decodeList<TodoTagDto>()
            }

            val syncInfoDeferred = async {
                supabase.from(TABLE_SYNC_INFO)
                    .select { filter { eq("uuid", uuid) } }
                    .decodeSingleOrNull<SyncInfoDto>()
            }

            val syncInfo = syncInfoDeferred.await()

            SyncDataDto(
                schedules = schedulesDeferred.await(),
                todoInfos = todoInfosDeferred.await(),
                repeatCycles = repeatCyclesDeferred.await(),
                tags = tagsDeferred.await(),
                syncedAt = syncInfo?.toDomain(),
            )
        }
    }

    suspend fun generateConnectCode(uuid: String, connectCode: String): ZonedDateTime {
        val expirationTime = LocalDateTime.now()
            .plusMinutes(10L)
            .atZone(ZoneId.systemDefault())

        supabase.from(TABLE_CONNECT_CODES)
            .upsert(
                ConnectDto(
                    uuid = uuid,
                    connectCode = connectCode,
                    expirationTime = expirationTime.format(ISO_FORMAT),
                )
            )

        return expirationTime
    }

    suspend fun connectAnother(connectCode: String): Result<ConnectDto?> =
        suspendRunCatching {
            supabase.from(TABLE_CONNECT_CODES)
                .select { filter { eq("connect_code", connectCode) } }
                .decodeSingleOrNull<ConnectDto>()
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
