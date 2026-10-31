package com.tgyuu.shared.platform

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Android feature/sync NetworkState 대응 */
enum class NetworkState { None, Connected, NotConnected }

/** Android feature/sync NetworkMonitor 대응: 현재 네트워크 연결 상태 */
interface NetworkMonitor {
    val networkState: StateFlow<NetworkState>
}

val NetworkMonitor?.isConnected: Boolean
    get() = this == null || networkState.value == NetworkState.Connected

/** 플랫폼 구현 (iOS: NWPathMonitor) */
expect fun createNetworkMonitor(): NetworkMonitor

/** 테스트·미지원 플랫폼용: 항상 연결됨 */
class AlwaysConnectedNetworkMonitor : NetworkMonitor {
    override val networkState: StateFlow<NetworkState> =
        MutableStateFlow(NetworkState.Connected).asStateFlow()
}

/**
 * 네트워크 전송 실패인지 (Android core/common isNetworkError 대응).
 * KMP 에선 플랫폼별 예외 타입을 직접 참조할 수 없어 원인 체인의 클래스 이름으로 판단한다.
 */
fun Throwable.isNetworkError(): Boolean =
    generateSequence(this) { it.cause }.take(MAX_CAUSE_DEPTH).any { it::class.simpleName in NETWORK_ERROR_NAMES }

private const val MAX_CAUSE_DEPTH = 8

private val NETWORK_ERROR_NAMES = setOf(
    "IOException",
    "HttpRequestException", // supabase-kt: 요청 자체가 실패(오프라인 등)
    "HttpRequestTimeoutException",
    "ConnectTimeoutException",
    "SocketTimeoutException",
    "UnresolvedAddressException",
    "DarwinHttpRequestException", // ktor darwin: NSURLError
)
