package com.tgyuu.network.source

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue.serverTimestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.tgyuu.common.suspendRunCatching
import com.tgyuu.domain.model.sync.RepeatCycleForSync
import com.tgyuu.domain.model.sync.TodoInfoForSync
import com.tgyuu.domain.model.sync.TodoScheduleForSync
import com.tgyuu.domain.model.sync.TodoTagForSync
import com.tgyuu.network.model.sync.GetDownloadDataResponse
import com.tgyuu.network.model.sync.GetSyncInfoResponse
import com.tgyuu.network.model.sync.RepeatCycleDto
import com.tgyuu.network.model.sync.TodoInfoDto
import com.tgyuu.network.model.sync.TodoScheduleDto
import com.tgyuu.network.model.sync.TodoTagDto
import com.tgyuu.network.model.sync.toDto
import com.tgyuu.network.toResult
import com.tgyuu.network.toZonedDateTimeOrNull
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.ZonedDateTime
import java.util.Date
import javax.inject.Inject

class SyncDataSource @Inject constructor(
    private val firestore: FirebaseFirestore,
) {
    suspend fun getSyncInfo(uuid: String): Result<GetSyncInfoResponse> {
        val userDoc = firestore.collection(COLLECTION_USERS).document(uuid)

        return userDoc.collection(COLLECTION_INFO)
            .document(INFO_DOCUMENT_ID)
            .get()
            .toResult<GetSyncInfoResponse>()
    }

    suspend fun uploadData(
        uuid: String,
        schedules: List<TodoScheduleForSync>,
        infos: List<TodoInfoForSync>,
        repeatCycles: List<RepeatCycleForSync>,
        tags: List<TodoTagForSync>,
    ): Result<ZonedDateTime> = coroutineScope {
        suspendRunCatching {
            val userDoc = firestore.collection(COLLECTION_USERS).document(uuid)

            val schedulesJob = launch {
                schedules.forEach { schedule ->
                    userDoc.collection(COLLECTION_SCHEDULES)
                        .document(schedule.id.toString())
                        .set(schedule.toDto())
                        .await()
                }
            }

            val todoInfosJob = launch {
                infos.forEach { info ->
                    userDoc.collection(COLLECTION_TODO_INFOS)
                        .document(info.id.toString())
                        .set(info.toDto())
                        .await()
                }
            }

            val repeatCyclesJob = launch {
                repeatCycles.forEach { repeat ->
                    userDoc.collection(COLLECTION_REPEAT_CYCLES)
                        .document(repeat.id.toString())
                        .set(repeat)
                        .await()
                }
            }

            val tagsJob = launch {
                tags.forEach { tag ->
                    userDoc.collection(COLLECTION_TAGS)
                        .document(tag.id.toString())
                        .set(tag.toDto())
                        .await()
                }
            }

            repeatCyclesJob.join()
            tagsJob.join()
            todoInfosJob.join()
            schedulesJob.join()

            val infoDocRef = userDoc.collection(COLLECTION_INFO).document(INFO_DOCUMENT_ID)
            infoDocRef.set(mapOf(FIELD_LAST_UPDATED_AT to serverTimestamp())).await()

            val updatedSnapshot = infoDocRef.get().await()
            val updatedAt = updatedSnapshot
                .getTimestamp(FIELD_LAST_UPDATED_AT)
                .toZonedDateTimeOrNull()
                ?: throw IllegalStateException("lastUpdatedAt 가 비었습니다.")

            return@suspendRunCatching updatedAt
        }
    }

    suspend fun downloadData(
        uuid: String,
        lastSyncTime: Date,
    ): Result<GetDownloadDataResponse> = coroutineScope {
        suspendRunCatching {
            val userDoc = firestore.collection(COLLECTION_USERS).document(uuid)

            val schedulesDeferred = async {
                val snapshot = userDoc.collection(COLLECTION_SCHEDULES)
                    .whereGreaterThan(FIELD_UPLOADED_AT, lastSyncTime)
                    .get()
                    .await()

                snapshot.documents.mapNotNull { it.toObject(TodoScheduleDto::class.java) }
            }

            val repeatCyclesDeferred = async {
                val snapshot = userDoc.collection(COLLECTION_REPEAT_CYCLES)
                    .whereGreaterThan(FIELD_UPLOADED_AT, lastSyncTime)
                    .get()
                    .await()

                snapshot.documents.mapNotNull { it.toObject(RepeatCycleDto::class.java) }
            }

            val tagsDeferred = async {
                val snapshot = userDoc.collection(COLLECTION_TAGS)
                    .whereGreaterThan(FIELD_UPLOADED_AT, lastSyncTime)
                    .get()
                    .await()

                snapshot.documents.mapNotNull { it.toObject(TodoTagDto::class.java) }
            }

            val todoInfosDeferred = async {
                val snapshot = userDoc.collection(COLLECTION_TODO_INFOS)
                    .whereGreaterThan(FIELD_UPLOADED_AT, lastSyncTime)
                    .get()
                    .await()

                snapshot.documents.mapNotNull { it.toObject(TodoInfoDto::class.java) }
            }

            val infoSnapshotDeferred = async {
                userDoc.collection(COLLECTION_INFO)
                    .document(INFO_DOCUMENT_ID)
                    .get()
                    .await()
            }

            GetDownloadDataResponse(
                schedules = schedulesDeferred.await(),
                todoInfos = todoInfosDeferred.await(),
                repeatCycles = repeatCyclesDeferred.await(),
                tags = tagsDeferred.await(),
                syncedAt = infoSnapshotDeferred.await().getTimestamp(FIELD_LAST_UPDATED_AT)
                    ?: Timestamp.now()
            )
        }
    }

    private companion object {
        // 컬렉션 상수
        private const val COLLECTION_USERS = "users"
        private const val COLLECTION_INFO = "info"
        private const val COLLECTION_SCHEDULES = "schedules"
        private const val COLLECTION_TODO_INFOS = "todoInfos"
        private const val COLLECTION_REPEAT_CYCLES = "repeatCycles"
        private const val COLLECTION_TAGS = "tags"

        // 다큐먼트 상수
        private const val INFO_DOCUMENT_ID = "0"

        // 필드 상수
        private const val FIELD_LAST_UPDATED_AT = "lastUpdatedAt"
        private const val FIELD_UPLOADED_AT = "uploadedAt"
    }
}
