package com.tgyuu.shared.platform

import com.tgyuu.shared.data.source.RemoteConfigDataSource
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.concurrent.AtomicInt
import kotlin.coroutines.resume

/**
 * Swift RemoteConfigBridge(FirebaseRemoteConfig)를 람다로 감싼 RemoteConfigDataSource.
 *
 * @param onFetchAndActivate Swift 가 fetchAndActivate 를 호출하고 끝나면(성공/실패 무관) completion 을 부른다.
 * @param onGetString 활성화된 값. 키가 없으면 빈 문자열.
 */
class IosRemoteConfigDataSource(
    private val onFetchAndActivate: (completion: () -> Unit) -> Unit,
    private val onGetString: (String) -> String,
) : RemoteConfigDataSource {

    override suspend fun fetchAndActivate() {
        // 네트워크가 느려도 시작 흐름이 멈추지 않도록 상한을 둔다. 시간이 지나면 마지막 활성화 값을 쓴다.
        withTimeoutOrNull(FETCH_TIMEOUT_MILLIS) {
            suspendCancellableCoroutine { continuation ->
                val resumed = AtomicInt(0)
                onFetchAndActivate {
                    if (resumed.compareAndSet(0, 1) && continuation.isActive) continuation.resume(Unit)
                }
            }
        }
    }

    override fun getString(key: String): String? = onGetString(key).takeIf { it.isNotBlank() }

    private companion object {
        const val FETCH_TIMEOUT_MILLIS = 10_000L
    }
}
