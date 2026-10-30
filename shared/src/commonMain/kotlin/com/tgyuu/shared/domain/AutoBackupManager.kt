package com.tgyuu.shared.domain

import com.tgyuu.shared.common.currentInstant
import com.tgyuu.shared.domain.repository.ConfigRepository
import com.tgyuu.shared.domain.repository.ErrorRepository
import com.tgyuu.shared.domain.repository.FeatureFlag
import com.tgyuu.shared.domain.repository.FeatureFlagRepository
import com.tgyuu.shared.domain.repository.SyncRepository
import kotlinx.coroutines.flow.first
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant

/**
 * 자동 백업 매니저 (Android AutoBackupManager의 KMP 포팅).
 *
 * - 앱이 백그라운드로 갈 때 [onAppStop] 호출.
 * - 포그라운드 복귀/네트워크 회복 시 [tryPendingBackup] 호출.
 *
 * 피처 플래그(use_auto_backup) + 사용자 토글이 모두 켜져 있으면
 * SyncRepository.syncUpData()로 서버에 업로드한다. 실패 시 pending으로 두고 다음에 재시도한다.
 * Android처럼 서버 last_updated_at 기준 쿨타임(10초)을 적용한다.
 * (Android의 NetworkMonitor 기반 게이팅은 iOS에선 syncUpData 실패→pending 재시도로 대체)
 */
class AutoBackupManager(
    private val featureFlagRepository: FeatureFlagRepository,
    private val configRepository: ConfigRepository,
    private val syncRepository: SyncRepository,
    private val errorRepository: ErrorRepository? = null,
) {
    private var backupPending: Boolean = false

    suspend fun onAppStop() {
        if (!shouldBackup()) return

        if (!isCoolTimeElapsed()) {
            backupPending = true
            return
        }

        backup()
    }

    suspend fun tryPendingBackup() {
        if (!shouldBackup()) return
        if (!backupPending) return
        if (!isCoolTimeElapsed()) return

        backup()
    }

    // Android cancelAndBackup과 동일: 실패 시 pending 유지 + 에러 리포지토리에 기록
    private suspend fun backup() {
        try {
            syncRepository.syncUpData()
            backupPending = false
        } catch (e: kotlinx.coroutines.CancellationException) {
            throw e
        } catch (e: Throwable) {
            backupPending = true
            runCatching { errorRepository?.logError(e) }
        }
    }

    private suspend fun shouldBackup(): Boolean {
        if (!featureFlagRepository.getBoolean(FeatureFlag.USE_AUTO_BACKUP)) return false
        return configRepository.getAutoBackupEnabled().first()
    }

    private suspend fun isCoolTimeElapsed(): Boolean {
        val serverLastUpdatedAt = runCatching { syncRepository.getServerLastUpdatedAt() }
            .getOrNull()?.lastUpdatedAt
            ?: return true

        val elapsedMillis = (currentInstant() -
            serverLastUpdatedAt.toInstant(TimeZone.currentSystemDefault())).inWholeMilliseconds
        if (elapsedMillis < 0) return true
        return elapsedMillis >= SYNC_UP_COOL_TIME
    }

    private companion object {
        const val SYNC_UP_COOL_TIME = 10_000L
    }
}
