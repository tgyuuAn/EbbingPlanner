package com.tgyuu.shared.data.source

import com.tgyuu.shared.common.now
import com.tgyuu.shared.domain.model.sync.ConnectInfo
import com.tgyuu.shared.domain.model.sync.RepeatCycleForSync
import com.tgyuu.shared.domain.model.sync.TodoInfoForSync
import com.tgyuu.shared.domain.model.sync.TodoScheduleForSync
import com.tgyuu.shared.domain.model.sync.TodoTagForSync
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * Firebase Firestore REST API based SyncDataSource.
 *
 * Requires:
 * - Firebase project ID
 * - Authentication token (Firebase Auth or service account)
 *
 * Firestore REST API base URL:
 * https://firestore.googleapis.com/v1/projects/{projectId}/databases/(default)/documents
 */
class FirestoreSyncDataSource(
    private val projectId: String,
    private val authToken: String = "",
) : SyncDataSource {

    private val baseUrl =
        "https://firestore.googleapis.com/v1/projects/$projectId/databases/(default)/documents"

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }

    override suspend fun uploadData(
        uuid: String,
        schedules: List<TodoScheduleForSync>,
        infos: List<TodoInfoForSync>,
        repeatCycles: List<RepeatCycleForSync>,
        tags: List<TodoTagForSync>,
    ): LocalDateTime {
        // Upload each data type to Firestore
        schedules.forEach { schedule ->
            client.patch("$baseUrl/users/$uuid/schedules/${schedule.id}") {
                contentType(ContentType.Application.Json)
                setBody(schedule.toFirestoreFields())
            }
        }

        infos.forEach { info ->
            client.patch("$baseUrl/users/$uuid/todoInfos/${info.id}") {
                contentType(ContentType.Application.Json)
                setBody(info.toFirestoreFields())
            }
        }

        repeatCycles.forEach { cycle ->
            client.patch("$baseUrl/users/$uuid/repeatCycles/${cycle.id}") {
                contentType(ContentType.Application.Json)
                setBody(cycle.toFirestoreFields())
            }
        }

        tags.forEach { tag ->
            client.patch("$baseUrl/users/$uuid/tags/${tag.id}") {
                contentType(ContentType.Application.Json)
                setBody(tag.toFirestoreFields())
            }
        }

        // Update sync timestamp
        val now = LocalDateTime.now()
        client.patch("$baseUrl/users/$uuid/info/0") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("fields" to mapOf("lastUpdatedAt" to mapOf("stringValue" to now.toString()))))
        }

        return now
    }

    override suspend fun downloadData(
        uuid: String,
        lastSyncTime: LocalDateTime,
    ): Result<SyncData> = runCatching {
        // TODO: Implement structured query to filter by uploadedAt > lastSyncTime
        // For now, download all data
        SyncData(
            schedules = emptyList(),
            todoInfos = emptyList(),
            repeatCycles = emptyList(),
            tags = emptyList(),
            syncedAt = LocalDateTime.now(),
        )
    }

    override suspend fun generateConnectCode(
        uuid: String,
        connectCode: String,
    ): LocalDateTime {
        val now = LocalDateTime.now()
        val expiration = LocalDateTime(
            now.date,
            kotlinx.datetime.LocalTime(now.hour, now.minute + 10, now.second),
        )

        client.patch("$baseUrl/connectCodes/$connectCode") {
            contentType(ContentType.Application.Json)
            setBody(
                mapOf(
                    "fields" to mapOf(
                        "uuid" to mapOf("stringValue" to uuid),
                        "connectCode" to mapOf("stringValue" to connectCode),
                        "expirationTime" to mapOf("stringValue" to expiration.toString()),
                    )
                )
            )
        }

        return expiration
    }

    override suspend fun connectAnother(connectCode: String): Result<ConnectInfo?> = runCatching {
        val response = client.get("$baseUrl/connectCodes/$connectCode")
        // TODO: Parse Firestore document response to ConnectInfo
        null
    }

    override suspend fun getLastSyncTime(uuid: String): LocalDateTime? {
        return try {
            val response = client.get("$baseUrl/users/$uuid/info/0")
            // TODO: Parse Firestore document to extract lastUpdatedAt
            null
        } catch (_: Exception) {
            null
        }
    }

    // Extension functions for Firestore field serialization
    private fun TodoScheduleForSync.toFirestoreFields(): Map<String, Any> = mapOf(
        "fields" to mapOf(
            "id" to mapOf("integerValue" to id.toString()),
            "infoId" to mapOf("integerValue" to infoId.toString()),
            "date" to mapOf("stringValue" to date.toString()),
            "memo" to mapOf("stringValue" to memo),
            "priority" to mapOf("integerValue" to priority.toString()),
            "isDone" to mapOf("booleanValue" to isDone),
            "isDeleted" to mapOf("booleanValue" to isDeleted),
            "createdAt" to mapOf("stringValue" to createdAt.toString()),
            "updatedAt" to mapOf("stringValue" to updatedAt.toString()),
        )
    )

    private fun TodoInfoForSync.toFirestoreFields(): Map<String, Any> = mapOf(
        "fields" to mapOf(
            "id" to mapOf("integerValue" to id.toString()),
            "title" to mapOf("stringValue" to title),
            "tagId" to mapOf("integerValue" to (tagId ?: 0).toString()),
            "createdAt" to mapOf("stringValue" to createdAt.toString()),
            "updatedAt" to mapOf("stringValue" to updatedAt.toString()),
        )
    )

    private fun RepeatCycleForSync.toFirestoreFields(): Map<String, Any> = mapOf(
        "fields" to mapOf(
            "id" to mapOf("integerValue" to id.toString()),
            "intervals" to mapOf("stringValue" to intervals.joinToString(",")),
            "isDeleted" to mapOf("booleanValue" to isDeleted),
            "updatedAt" to mapOf("stringValue" to updatedAt.toString()),
        )
    )

    private fun TodoTagForSync.toFirestoreFields(): Map<String, Any> = mapOf(
        "fields" to mapOf(
            "id" to mapOf("integerValue" to id.toString()),
            "name" to mapOf("stringValue" to name),
            "color" to mapOf("integerValue" to color.toString()),
            "isDeleted" to mapOf("booleanValue" to isDeleted),
            "createdAt" to mapOf("stringValue" to createdAt.toString()),
            "updatedAt" to mapOf("stringValue" to updatedAt.toString()),
        )
    )
}
