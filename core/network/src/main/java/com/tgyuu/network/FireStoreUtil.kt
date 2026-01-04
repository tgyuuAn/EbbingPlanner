package com.tgyuu.network

import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toInstant
import java.time.ZoneId
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
    month = this.month.number,
    day = this.day,
    hour = 0,
    minute = 0,
).toDate()

@OptIn(ExperimentalTime::class)
fun LocalDateTime.toDate(): Date {
    val instant = this.toInstant(TimeZone.currentSystemDefault())
    return Date(instant.toEpochMilliseconds())
}

fun Date.toLocalDate(): LocalDate {
    val instant = this.toInstant()
    val zdt = instant.atZone(java.time.ZoneId.systemDefault())
    return LocalDate(
        year = zdt.year,
        month = zdt.monthValue,
        day = zdt.dayOfMonth
    )
}

fun Date.toLocalDateTime(): LocalDateTime {
    val instant = this.toInstant()
    val ldt = instant.atZone(ZoneId.systemDefault()).toLocalDateTime()
    return LocalDateTime(
        year = ldt.year,
        month = ldt.monthValue,
        day = ldt.dayOfMonth,
        hour = ldt.hour,
        minute = ldt.minute,
        second = ldt.second,
        nanosecond = ldt.nano
    )
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
