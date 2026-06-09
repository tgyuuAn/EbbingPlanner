package com.tgyuu.network.source

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class NotificationLogDataSource @Inject constructor(
    private val firestore: FirebaseFirestore,
) {
    suspend fun logNotificationConfig(
        uuid: String,
        enabled: Boolean,
        alarmTime: String,
        message: String,
        usesPlaceholder: Boolean,
        isDefault: Boolean,
    ) {
        val data = mapOf(
            "notificationEnabled" to enabled,
            "alarmTime" to alarmTime,
            "alarmMessage" to message,
            "usesPlaceholder" to usesPlaceholder,
            "isDefaultMessage" to isDefault,
            "updatedAt" to FieldValue.serverTimestamp(),
        )

        firestore.collection(COLLECTION_ANALYTICS)
            .document(uuid)
            .set(data)
            .await()
    }

    private companion object {
        private const val COLLECTION_ANALYTICS = "notificationAnalytics"
    }
}
