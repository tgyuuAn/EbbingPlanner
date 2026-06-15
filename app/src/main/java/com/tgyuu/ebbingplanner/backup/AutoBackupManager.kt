package com.tgyuu.ebbingplanner.backup

import com.tgyuu.common.suspendRunCatching
import com.tgyuu.domain.repository.ConfigRepository
import com.tgyuu.domain.repository.FeatureFlag
import com.tgyuu.domain.repository.FeatureFlagRepository
import com.tgyuu.domain.repository.SyncRepository
import com.tgyuu.sync.network.NetworkMonitor
import com.tgyuu.sync.network.NetworkState
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.ZonedDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AutoBackupManager @Inject constructor(
    private val syncRepository: SyncRepository,
    private val configRepository: ConfigRepository,
    private val networkMonitor: NetworkMonitor,
    private val featureFlagRepository: FeatureFlagRepository,
) {
    private var backupJob: Job? = null

    suspend fun onAppStop() {
        if (!featureFlagRepository.getBoolean(FeatureFlag.USE_AUTO_BACKUP)) return

        val enabled = configRepository.getAutoBackupEnabled().first()
        if (!enabled) return

        if (networkMonitor.networkState.value == NetworkState.Connected) {
            if (!isCoolTimeElapsed()) {
                syncRepository.setBackupPending(true)
                return
            }

            cancelAndBackup()
        } else {
            syncRepository.setBackupPending(true)
        }
    }

    suspend fun tryPendingBackup() {
        if (!featureFlagRepository.getBoolean(FeatureFlag.USE_AUTO_BACKUP)) return

        val enabled = configRepository.getAutoBackupEnabled().first()
        if (!enabled) return

        val pending = syncRepository.getBackupPending().first()
        if (!pending) return

        if (networkMonitor.networkState.value != NetworkState.Connected) return
        if (!isCoolTimeElapsed()) return

        cancelAndBackup()
    }

    private suspend fun cancelAndBackup() = coroutineScope {
        backupJob?.cancel()
        backupJob = launch {
            suspendRunCatching {
                syncRepository.syncUpData()
            }.onSuccess {
                syncRepository.setBackupPending(false)
            }.onFailure {
                syncRepository.setBackupPending(true)
            }
        }
    }

    private suspend fun isCoolTimeElapsed(): Boolean {
        val serverLastUpdatedAt = suspendRunCatching {
            syncRepository.getServerLastUpdatedAt()
        }.getOrNull()
            ?: return true

        val elapsedMillis = Duration.between(serverLastUpdatedAt, ZonedDateTime.now()).toMillis()
        if (elapsedMillis < 0) return true
        return elapsedMillis >= SYNC_UP_COOL_TIME
    }

    private companion object {
        const val SYNC_UP_COOL_TIME = 10_000L
    }
}
