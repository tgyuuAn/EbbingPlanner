package com.tgyuu.shared.domain

import com.tgyuu.shared.domain.repository.ConfigRepository
import com.tgyuu.shared.domain.repository.FeatureFlag
import com.tgyuu.shared.domain.repository.FeatureFlagRepository
import com.tgyuu.shared.domain.repository.SyncRepository
import kotlinx.coroutines.flow.first

/**
 * 자동 백업 매니저 (Android AutoBackupManager의 KMP 포팅).
 *
 * - 앱이 백그라운드로 갈 때 [onAppStop] 호출.
 * - 포그라운드 복귀/네트워크 회복 시 [tryPendingBackup] 호출.
 *
 * 피처 플래그(use_auto_backup) + 사용자 토글이 모두 켜져 있으면
 * SyncRepository.syncUpData()로 서버에 업로드한다. 실패 시 pending으로 두고 다음에 재시도한다.
 * (Android의 NetworkMonitor 기반 게이팅은 iOS에선 syncUpData 실패→pending 재시도로 대체)
 */
class AutoBackupManager(
    private val featureFlagRepository: FeatureFlagRepository,
    private val configRepository: ConfigRepository,
    private val syncRepository: SyncRepository,
) {
    private var backupPending: Boolean = false

    suspend fun onAppStop() {
        if (!shouldBackup()) return
        runCatching { syncRepository.syncUpData() }
            .onSuccess { backupPending = false }
            .onFailure { backupPending = true }
    }

    suspend fun tryPendingBackup() {
        if (!backupPending) return
        onAppStop()
    }

    private suspend fun shouldBackup(): Boolean {
        if (!featureFlagRepository.getBoolean(FeatureFlag.USE_AUTO_BACKUP)) return false
        return configRepository.getAutoBackupEnabled().first()
    }
}
