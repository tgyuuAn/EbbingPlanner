package com.tgyuu.common

import java.io.IOException

/**
 * 네트워크 연결/통신 오류 여부를 판단한다.
 *
 * 예외의 cause 체인을 따라가며 [IOException] 계열인지 확인한다.
 * Ktor/Supabase 의 소켓·타임아웃·호스트 관련 예외들은 모두 [IOException] 을 상속하므로 이를 기준으로 판별한다.
 */
fun Throwable.isNetworkError(): Boolean {
    var current: Throwable? = this
    var depth = 0
    while (current != null && depth < MAX_CAUSE_DEPTH) {
        if (current is IOException) return true
        if (current == current.cause) break
        current = current.cause
        depth++
    }
    return false
}

private const val MAX_CAUSE_DEPTH = 10
