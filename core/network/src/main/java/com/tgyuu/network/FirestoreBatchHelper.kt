package com.tgyuu.network

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirestoreBatchHelper(private val firestore: FirebaseFirestore) {
    suspend fun <T> batchSet(
        items: List<T>,
        documentRefProvider: (T) -> DocumentReference,
        dataProvider: (T) -> Any
    ) {
        if (items.isEmpty()) return

        items.chunked(MAX_BATCH_SIZE).forEach { chunk ->
            val batch = firestore.batch()
            chunk.forEach { item ->
                batch.set(documentRefProvider(item), dataProvider(item))
            }
            batch.commit().await()
        }
    }

    private companion object {
        const val MAX_BATCH_SIZE = 500 // Firestore WriteBatch 최대 제한
    }
}
