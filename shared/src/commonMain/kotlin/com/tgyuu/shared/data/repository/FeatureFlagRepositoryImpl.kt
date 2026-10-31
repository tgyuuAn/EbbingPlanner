package com.tgyuu.shared.data.repository

import com.tgyuu.shared.data.source.DefaultRemoteConfigDataSource
import com.tgyuu.shared.data.source.RemoteConfigDataSource
import com.tgyuu.shared.domain.repository.FeatureFlag
import com.tgyuu.shared.domain.repository.FeatureFlagRepository

/**
 * Android FeatureFlagRepositoryImpl(FeatureFlagDataSource) 대응.
 *
 * Remote Config 에 같은 키(use_auto_backup)가 있으면 그 값을 쓰고, 없으면 iOS 기본값을 쓴다.
 * (자동 백업은 이미 운영 중인 기능이라 iOS 기본값은 활성으로 둔다. Android 기본값은 false 이지만
 *  운영 Remote Config 값이 true 라 실제 동작은 같다.)
 */
class FeatureFlagRepositoryImpl(
    private val remoteConfigDataSource: RemoteConfigDataSource = DefaultRemoteConfigDataSource(),
) : FeatureFlagRepository {
    private val defaults: Map<String, Boolean> = mapOf(
        FeatureFlag.USE_AUTO_BACKUP to true,
    )

    override suspend fun fetchAndAwait() {
        runCatching { remoteConfigDataSource.fetchAndActivate() }
    }

    override fun getBoolean(key: String): Boolean =
        remoteConfigDataSource.getBoolean(key) ?: defaults[key] ?: false
}
