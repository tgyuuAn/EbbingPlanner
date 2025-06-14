package com.tgyuu.network.source

import com.google.firebase.firestore.FieldValue.serverTimestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.tgyuu.common.suspendRunCatching
import com.tgyuu.domain.model.RepeatCycle
import com.tgyuu.domain.model.TodoSchedule
import com.tgyuu.domain.model.TodoTag
import com.tgyuu.network.model.GetSyncInfoResponse
import com.tgyuu.network.model.toRequestDto
import com.tgyuu.network.toResult
import com.tgyuu.network.toZonedDateTimeOrNull
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.ZonedDateTime
import javax.inject.Inject

class SyncDataSource @Inject constructor(
    private val firestore: FirebaseFirestore,
) {
    suspend fun getSyncInfo(uuid: String): Result<GetSyncInfoResponse> {
        val userDoc = firestore.collection("users").document(uuid)

        return userDoc.collection("info")
            .document("0")
            .get()
            .toResult<GetSyncInfoResponse>()
    }

    suspend fun uploadData(
        uuid: String,
        schedules: List<TodoSchedule>,
        repeatCycles: List<RepeatCycle>,
        tags: List<TodoTag>,
    ): Result<ZonedDateTime> = coroutineScope {
        suspendRunCatching {
            val userDoc = firestore.collection("users").document(uuid)

            val schedulesJob = launch {
                schedules.forEach { schedule ->
                    userDoc.collection("schedules")
                        .document(schedule.id.toString())
                        .set(schedule.toRequestDto())
                        .await()
                }
            }

            val repeatCyclesJob = launch {
                repeatCycles.forEach { repeat ->
                    userDoc.collection("repeatCycles")
                        .document(repeat.id.toString())
                        .set(repeat)
                        .await()
                }
            }

            val tagsJob = launch {
                tags.forEach { tag ->
                    userDoc.collection("tags")
                        .document(tag.id.toString())
                        .set(tag.toRequestDto())
                        .await()
                }
            }

            repeatCyclesJob.join()
            tagsJob.join()
            schedulesJob.join()

            val infoDocRef = userDoc.collection("info").document("0")
            infoDocRef.set(mapOf("lastUpdatedAt" to serverTimestamp())).await()

            val updatedSnapshot = infoDocRef.get().await()
            val updatedAt = updatedSnapshot
                .getTimestamp("lastUpdatedAt")
                .toZonedDateTimeOrNull()
                ?: throw IllegalStateException("lastUpdatedAt is null or invalid")

            return@suspendRunCatching updatedAt
        }
    }
}
