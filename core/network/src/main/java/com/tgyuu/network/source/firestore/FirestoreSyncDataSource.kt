package com.tgyuu.network.source.firestore

import com.google.firebase.firestore.FieldValue.serverTimestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp
import com.tgyuu.common.suspendRunCatching
import com.tgyuu.domain.model.sync.ConnectInfo
import com.tgyuu.domain.model.sync.RepeatCycleForSync
import com.tgyuu.domain.model.sync.TodoInfoForSync
import com.tgyuu.domain.model.sync.TodoScheduleForSync
import com.tgyuu.domain.model.sync.TodoTagForSync
import com.tgyuu.network.FirestoreBatchHelper
import com.tgyuu.network.source.SyncDownloadResult
import com.tgyuu.network.source.SyncRemoteDataSource
import com.tgyuu.network.toDate
import com.tgyuu.network.toLocalDate
import com.tgyuu.network.toLocalDateTime
import com.tgyuu.network.toZonedDateTimeOrNull
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Date
import javax.inject.Inject

class FirestoreSyncDataSource @Inject constructor(
    private val firestore: FirebaseFirestore,
) : SyncRemoteDataSource {

    override suspend fun getSyncInfo(uuid: String): ZonedDateTime? {
        val userDoc = firestore.collection(COLLECTION_USERS).document(uuid)
        val snapshot = userDoc.collection(COLLECTION_INFO)
            .document(INFO_DOCUMENT_ID)
            .get()
            .await()

        return snapshot.getTimestamp(FIELD_LAST_UPDATED_AT).toZonedDateTimeOrNull()
    }

    override suspend fun uploadData(
        uuid: String,
        schedules: List<TodoScheduleForSync>,
        infos: List<TodoInfoForSync>,
        repeatCycles: List<RepeatCycleForSync>,
        tags: List<TodoTagForSync>,
    ): ZonedDateTime = coroutineScope {
        val userDoc = firestore.collection(COLLECTION_USERS).document(uuid)
        val batchHelper = FirestoreBatchHelper(firestore)

        val schedulesJob = launch {
            batchHelper.batchSet(
                items = schedules,
                documentRefProvider = { userDoc.collection(COLLECTION_SCHEDULES).document(it.id.toString()) },
                dataProvider = { it.toFirestoreScheduleDto() }
            )
        }

        val todoInfosJob = launch {
            batchHelper.batchSet(
                items = infos,
                documentRefProvider = { userDoc.collection(COLLECTION_TODO_INFOS).document(it.id.toString()) },
                dataProvider = { it.toFirestoreInfoDto() }
            )
        }

        val repeatCyclesJob = launch {
            batchHelper.batchSet(
                items = repeatCycles,
                documentRefProvider = { userDoc.collection(COLLECTION_REPEAT_CYCLES).document(it.id.toString()) },
                dataProvider = { it.toFirestoreCycleDto() }
            )
        }

        val tagsJob = launch {
            batchHelper.batchSet(
                items = tags,
                documentRefProvider = { userDoc.collection(COLLECTION_TAGS).document(it.id.toString()) },
                dataProvider = { it.toFirestoreTagDto() }
            )
        }

        repeatCyclesJob.join()
        tagsJob.join()
        todoInfosJob.join()
        schedulesJob.join()

        val infoDocRef = userDoc.collection(COLLECTION_INFO).document(INFO_DOCUMENT_ID)
        infoDocRef.set(mapOf(FIELD_LAST_UPDATED_AT to serverTimestamp())).await()

        val updatedSnapshot = infoDocRef.get().await()
        updatedSnapshot.getTimestamp(FIELD_LAST_UPDATED_AT).toZonedDateTimeOrNull()
            ?: throw IllegalStateException("lastUpdatedAt 가 비었습니다.")
    }

    override suspend fun downloadData(
        uuid: String,
        lastSyncTime: Date,
    ): Result<SyncDownloadResult> = coroutineScope {
        suspendRunCatching {
            val userDoc = firestore.collection(COLLECTION_USERS).document(uuid)

            val schedulesDeferred = async {
                userDoc.collection(COLLECTION_SCHEDULES)
                    .whereGreaterThan(FIELD_UPLOADED_AT, lastSyncTime)
                    .get().await()
                    .documents.mapNotNull { it.toObject(FsScheduleDto::class.java)?.toDomain() }
            }

            val todoInfosDeferred = async {
                userDoc.collection(COLLECTION_TODO_INFOS)
                    .whereGreaterThan(FIELD_UPLOADED_AT, lastSyncTime)
                    .get().await()
                    .documents.mapNotNull { it.toObject(FsInfoDto::class.java)?.toDomain() }
            }

            val repeatCyclesDeferred = async {
                userDoc.collection(COLLECTION_REPEAT_CYCLES)
                    .whereGreaterThan(FIELD_UPLOADED_AT, lastSyncTime)
                    .get().await()
                    .documents.mapNotNull { it.toObject(FsCycleDto::class.java)?.toDomain() }
            }

            val tagsDeferred = async {
                userDoc.collection(COLLECTION_TAGS)
                    .whereGreaterThan(FIELD_UPLOADED_AT, lastSyncTime)
                    .get().await()
                    .documents.mapNotNull { it.toObject(FsTagDto::class.java)?.toDomain() }
            }

            val infoDeferred = async {
                userDoc.collection(COLLECTION_INFO).document(INFO_DOCUMENT_ID).get().await()
                    .getTimestamp(FIELD_LAST_UPDATED_AT).toZonedDateTimeOrNull()
            }

            SyncDownloadResult(
                schedules = schedulesDeferred.await(),
                todoInfos = todoInfosDeferred.await(),
                repeatCycles = repeatCyclesDeferred.await(),
                tags = tagsDeferred.await(),
                syncedAt = infoDeferred.await(),
            )
        }
    }

    override suspend fun generateConnectCode(uuid: String, connectCode: String): ZonedDateTime {
        val expirationTime = LocalDateTime.now().plusMinutes(10L).toDate()

        firestore.collection(COLLECTION_CONNECT_CODES).document(connectCode)
            .set(FsConnectDto(uuid = uuid, connectCode = connectCode, connectCodeExpirationTime = expirationTime))
            .await()

        return expirationTime.toInstant().atZone(ZoneId.systemDefault())
    }

    override suspend fun connectAnother(connectCode: String): Result<ConnectInfo?> = suspendRunCatching {
        val snapshot = firestore.collection(COLLECTION_CONNECT_CODES).document(connectCode).get().await()
        if (snapshot.exists()) {
            snapshot.toObject(FsConnectDto::class.java)?.toDomain()
        } else null
    }

    private companion object {
        private const val COLLECTION_USERS = "users"
        private const val COLLECTION_INFO = "info"
        private const val COLLECTION_SCHEDULES = "schedules"
        private const val COLLECTION_TODO_INFOS = "todoInfos"
        private const val COLLECTION_REPEAT_CYCLES = "repeatCycles"
        private const val COLLECTION_TAGS = "tags"
        private const val COLLECTION_CONNECT_CODES = "connectCodes"
        private const val INFO_DOCUMENT_ID = "0"
        private const val FIELD_LAST_UPDATED_AT = "lastUpdatedAt"
        private const val FIELD_UPLOADED_AT = "uploadedAt"
    }
}

// Firestore 전용 DTO (내부에서만 사용)
private val defaultDate: Date = Date(0L)

data class FsScheduleDto(
    val id: Int = -1,
    val infoId: Int = -1,
    val date: Date = defaultDate,
    val memo: String = "",
    val priority: Int = 0,
    @PropertyName("done") val isDone: Boolean = false,
    val createdAt: Date = defaultDate,
    @PropertyName("deleted") val isDeleted: Boolean = false,
    val updatedAt: Date = defaultDate,
    @ServerTimestamp var uploadedAt: Date? = null,
) {
    fun toDomain() = TodoScheduleForSync(id = id, infoId = infoId, date = date.toLocalDate(), memo = memo, priority = priority, isDone = isDone, createdAt = createdAt.toLocalDate(), isDeleted = isDeleted, updatedAt = updatedAt.toLocalDateTime())
}

data class FsInfoDto(
    val id: Int = -1,
    val title: String = "",
    val tagId: Int = -1,
    val createdAt: Date = defaultDate,
    val updatedAt: Date = defaultDate,
    val restDays: String = "",
    @ServerTimestamp var uploadedAt: Date? = null,
) {
    fun toDomain() = TodoInfoForSync(id = id, title = title, tagId = tagId, createdAt = createdAt.toLocalDate(), updatedAt = updatedAt.toLocalDateTime(), restDays = restDays)
}

data class FsTagDto(
    val id: Int = -1,
    val name: String = "",
    val color: Int = -1,
    val createdAt: Date = defaultDate,
    @PropertyName("deleted") val isDeleted: Boolean = false,
    val updatedAt: Date = defaultDate,
    @ServerTimestamp var uploadedAt: Date? = null,
) {
    fun toDomain() = TodoTagForSync(id = id, name = name, color = color, createdAt = createdAt.toLocalDate(), isDeleted = isDeleted, updatedAt = updatedAt.toLocalDateTime())
}

data class FsCycleDto(
    val id: Int = -1,
    val intervals: List<Int> = emptyList(),
    @PropertyName("deleted") val isDeleted: Boolean = false,
    val updatedAt: Date = defaultDate,
    @ServerTimestamp var uploadedAt: Date? = null,
) {
    fun toDomain() = RepeatCycleForSync(id = id, intervals = intervals, isDeleted = isDeleted, updatedAt = updatedAt.toLocalDateTime())
}

data class FsConnectDto(
    val uuid: String = "",
    val connectCode: String = "",
    val connectCodeExpirationTime: Date = Date(),
) {
    fun toDomain() = ConnectInfo(uuid = uuid, connectCode = connectCode, connectCodeExpirationTime = connectCodeExpirationTime.toLocalDateTime())
}

private fun TodoScheduleForSync.toFirestoreScheduleDto() = FsScheduleDto(id = id, infoId = infoId, date = date.toDate(), memo = memo, priority = priority, isDone = isDone, createdAt = createdAt.toDate(), isDeleted = isDeleted, updatedAt = updatedAt.toDate())
private fun TodoInfoForSync.toFirestoreInfoDto() = FsInfoDto(id = id, title = title, tagId = tagId, createdAt = createdAt.toDate(), updatedAt = updatedAt.toDate(), restDays = restDays)
private fun TodoTagForSync.toFirestoreTagDto() = FsTagDto(id = id, name = name, color = color, createdAt = createdAt.toDate(), isDeleted = isDeleted, updatedAt = updatedAt.toDate())
private fun RepeatCycleForSync.toFirestoreCycleDto() = FsCycleDto(id = id, intervals = intervals, isDeleted = isDeleted, updatedAt = updatedAt.toDate())
