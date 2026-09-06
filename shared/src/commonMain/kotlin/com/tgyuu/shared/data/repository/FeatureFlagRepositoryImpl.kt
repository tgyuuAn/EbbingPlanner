package com.tgyuu.shared.data.repository

import com.tgyuu.shared.domain.repository.FeatureFlag
import com.tgyuu.shared.domain.repository.FeatureFlagRepository

/**
 * Stub FeatureFlagRepository.
 *
 * Android는 Firebase Remote Config로 플래그를 받지만, KMP에는 아직 Remote Config SDK가
 * 없으므로 기본값 맵으로 대체한다. Remote Config KMP 연동 시 이 구현만 교체하면 된다.
 * (자동 백업은 이미 운영 중인 기능이라 iOS 기본값은 활성으로 둔다.)
 */
class FeatureFlagRepositoryImpl : FeatureFlagRepository {
    private val defaults: Map<String, Boolean> = mapOf(
        FeatureFlag.USE_AUTO_BACKUP to true,
    )

    override suspend fun fetchAndAwait() {
        // Remote Config 미연동: no-op
    }

    override fun getBoolean(key: String): Boolean = defaults[key] ?: false
}
