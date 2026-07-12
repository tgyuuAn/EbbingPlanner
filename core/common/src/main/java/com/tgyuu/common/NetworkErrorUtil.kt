package com.tgyuu.common

import java.io.IOException
import java.net.UnknownHostException

/**
 * 네트워크/DNS 도달 실패 여부를 판단한다.
 *
 * Supabase/Ktor 예외나 [com.tgyuu.network.source.SyncUploadException] 처럼
 * 원인 예외가 여러 겹으로 감싸져 오는 경우가 많으므로, 최상위 타입만 보지 않고
 * cause 체인 전체를 훑어 네트워크 계열 예외가 포함되어 있는지 확인한다.
 */
fun Throwable.isNetworkError(): Boolean = causeChain().any { throwable ->
    throwable is IOException ||
        throwable is UnknownHostException ||
        throwable.isUnresolvedHostMessage()
}

private fun Throwable.isUnresolvedHostMessage(): Boolean {
    val message = message ?: return false
    return "Unable to resolve host" in message ||
        "No address associated with hostname" in message
}

private fun Throwable.causeChain(): Sequence<Throwable> {
    val seen = mutableSetOf<Throwable>()
    // cause 가 자기 자신을 참조하는 순환 구조를 방어한다.
    return generateSequence(this) { it.cause }.takeWhile { seen.add(it) }
}
