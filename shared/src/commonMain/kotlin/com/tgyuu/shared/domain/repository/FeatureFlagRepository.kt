package com.tgyuu.shared.domain.repository

/**
 * Android의 FeatureFlagRepository(Firebase Remote Config 백엔드)와 동일한 인터페이스.
 * KMP에는 아직 Remote Config SDK가 없어, 현재는 기본값 기반 stub 구현을 사용한다.
 */
interface FeatureFlagRepository {
    suspend fun fetchAndAwait()
    fun getBoolean(key: String): Boolean
}
