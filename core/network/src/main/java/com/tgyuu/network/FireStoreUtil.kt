package com.tgyuu.network

import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import java.util.Date
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.time.ExperimentalTime

val defaultDate = LocalDateTime(1970, 1, 1, 0, 0).toDate()

// FireStore timeStamp? -> ZonedDateTime? 로 변환해주는 확장함수
fun Timestamp?.toLocalDateTimeOrNull(): LocalDateTime? {
    return this?.toDate()?.toLocalDateTime()
}

fun LocalDate.toDate(): Date = LocalDateTime(
    year = this.year,
    monthNumber = this.monthNumber,
    dayOfMonth = this.dayOfMonth,
    hour = 0,
    minute = 0,
).toDate()

@OptIn(ExperimentalTime::class)
fun LocalDateTime.toDate(): Date {
    val instant = this.toInstant(TimeZone.currentSystemDefault())
    return Date(instant.toEpochMilliseconds())
}

@OptIn(ExperimentalTime::class)
fun Date.toLocalDate(): LocalDate {
    val instant = Instant.fromEpochMilliseconds(this.time)
    val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    return LocalDate(
        year = localDateTime.year,
        monthNumber = localDateTime.monthNumber,
        dayOfMonth = localDateTime.dayOfMonth
    )
}

@OptIn(ExperimentalTime::class)
fun Date.toLocalDateTime(): LocalDateTime {
    val instant = Instant.fromEpochMilliseconds(this.time)
    return instant.toLocalDateTime(TimeZone.currentSystemDefault())
}

// FireStore CallBack을 SuspendCancellableCoroutine으로 감싼 뒤 T를 반환
suspend inline fun <reified T> Task<DocumentSnapshot>.toResponse(): T =
    suspendCancellableCoroutine { cont ->
        this
            .addOnSuccessListener { snapshot ->
                if (!snapshot.exists()) {
                    cont.resumeWithException(NoSuchElementException("Document does not exist"))
                    return@addOnSuccessListener
                }

                val obj = snapshot.toObject(T::class.java)
                if (obj != null) {
                    cont.resume(obj)
                } else {
                    cont.resumeWithException(IllegalStateException("Failed to parse document to ${T::class.java.simpleName}"))
                }
            }
            .addOnFailureListener { exception ->
                cont.resumeWithException(exception)
            }
    }
