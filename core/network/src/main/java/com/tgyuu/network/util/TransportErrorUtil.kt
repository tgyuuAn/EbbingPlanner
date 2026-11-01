package com.tgyuu.network.util

import io.github.jan.supabase.exceptions.HttpRequestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * 서버 응답을 받기 전에 전송 단계에서 실패했는지 판단한다.
 *
 * Supabase는 전송 단계 실패(DNS, 연결 거부, 소켓 끊김 등)를 원인 없이 [HttpRequestException]으로 감싸 던지고,
 * 타임아웃은 [HttpRequestTimeoutException]으로 그대로 던진다. 서버가 응답한 4xx/5xx는
 * RestException 계열이라 여기에 걸리지 않는다.
 * DataStore 손상(CorruptionException) 같은 다른 IOException은 제외하려고 IOException 전체를 보지 않는다.
 */
fun Throwable.isTransportError(): Boolean = causeChain().any { throwable ->
    throwable is HttpRequestException ||
        throwable is HttpRequestTimeoutException ||
        throwable is UnknownHostException ||
        throwable is SocketTimeoutException ||
        throwable is SocketException
}

private fun Throwable.causeChain(): Sequence<Throwable> {
    val seen = mutableSetOf<Throwable>()
    // cause 가 자기 자신을 참조하는 순환 구조를 방어한다.
    return generateSequence(this) { it.cause }.takeWhile { seen.add(it) }
}
