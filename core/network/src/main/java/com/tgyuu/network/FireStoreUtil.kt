package com.tgyuu.network

import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.suspendCancellableCoroutine
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Date
import kotlin.coroutines.resume

// FireStore timeStamp? -> ZonedDateTime? 로 변환해주는 확장함수
fun Timestamp?.toZonedDateTimeOrNull(): ZonedDateTime? {
    return this?.toDate()
        ?.toInstant()
        ?.atZone(ZoneId.systemDefault())
}

fun LocalDate.toDate(): Date = Date.from(this.atStartOfDay(ZoneId.systemDefault()).toInstant())

// FireStore CallBack을 SuspendCancellableCoroutine으로 감싼 뒤, Result<T>로 래핑
suspend inline fun <reified T> Task<DocumentSnapshot>.toResult(): Result<T> =
    suspendCancellableCoroutine { cont ->
        this
            .addOnSuccessListener { snapshot ->
                if (!snapshot.exists()) {
                    cont.resume(Result.failure(NoSuchElementException("Document does not exist")))
                    return@addOnSuccessListener
                }

                val obj = snapshot.toObject(T::class.java)
                if (obj != null) {
                    cont.resume(Result.success(obj))
                } else {
                    cont.resume(Result.failure(IllegalStateException("Failed to parse document to ${T::class.java.simpleName}")))
                }
            }
            .addOnFailureListener { exception ->
                cont.resume(Result.failure(exception))
            }
    }
