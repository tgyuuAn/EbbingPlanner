package com.tgyuu.ebbingplanner.backup

import com.tgyuu.domain.repository.FeatureFlag
import com.tgyuu.ebbingplanner.backup.fake.FakeConfigRepository
import com.tgyuu.ebbingplanner.backup.fake.FakeFeatureFlagRepository
import com.tgyuu.ebbingplanner.backup.fake.FakeSyncRepository
import com.tgyuu.sync.network.NetworkMonitor
import com.tgyuu.sync.network.NetworkState
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.ZonedDateTime

class AutoBackupManagerTest {
    private lateinit var syncRepository: FakeSyncRepository
    private lateinit var configRepository: FakeConfigRepository
    private lateinit var featureFlagRepository: FakeFeatureFlagRepository
    private lateinit var networkMonitor: NetworkMonitor
    private lateinit var autoBackupManager: AutoBackupManager

    private val networkState = MutableStateFlow<NetworkState>(NetworkState.Connected)

    @BeforeEach
    fun setUp() {
        syncRepository = FakeSyncRepository()
        configRepository = FakeConfigRepository()
        featureFlagRepository = FakeFeatureFlagRepository()
        networkMonitor = mockk {
            every { this@mockk.networkState } returns this@AutoBackupManagerTest.networkState
        }

        featureFlagRepository.set(FeatureFlag.USE_AUTO_BACKUP, true)

        autoBackupManager = AutoBackupManager(
            syncRepository = syncRepository,
            configRepository = configRepository,
            networkMonitor = networkMonitor,
            featureFlagRepository = featureFlagRepository,
        )
    }

    @Test
    fun `feature flag가 꺼져있으면 백업하지 않는다`() = runTest {
        featureFlagRepository.set(FeatureFlag.USE_AUTO_BACKUP, false)

        autoBackupManager.onAppStop()

        assertEquals(0, syncRepository.syncUpCallCount)
    }

    @Test
    fun `autoBackupEnabled가 꺼져있으면 백업하지 않는다`() = runTest {
        configRepository.setAutoBackupEnabledValue(false)

        autoBackupManager.onAppStop()

        assertEquals(0, syncRepository.syncUpCallCount)
    }

    @Test
    fun `네트워크 미연결 시 pending을 true로 설정한다`() = runTest {
        networkState.value = NetworkState.NotConnected

        autoBackupManager.onAppStop()

        assertEquals(0, syncRepository.syncUpCallCount)
        assertTrue(syncRepository.getBackupPending().first())
    }

    @Test
    fun `cool time 미경과 시 pending을 true로 설정한다`() = runTest {
        syncRepository.serverLastUpdatedAt = ZonedDateTime.now()

        autoBackupManager.onAppStop()

        assertEquals(0, syncRepository.syncUpCallCount)
        assertTrue(syncRepository.getBackupPending().first())
    }

    @Test
    fun `정상 조건에서 syncUpData를 호출하고 pending을 false로 설정한다`() = runTest {
        autoBackupManager.onAppStop()

        assertEquals(1, syncRepository.syncUpCallCount)
        assertFalse(syncRepository.getBackupPending().first())
    }

    @Test
    fun `syncUpData 실패 시 pending을 true로 설정한다`() = runTest {
        syncRepository.shouldSyncFail = true

        autoBackupManager.onAppStop()

        assertEquals(1, syncRepository.syncUpCallCount)
        assertTrue(syncRepository.getBackupPending().first())
    }

    @Test
    fun `tryPendingBackup은 pending이 false면 백업하지 않는다`() = runTest {
        autoBackupManager.tryPendingBackup()

        assertEquals(0, syncRepository.syncUpCallCount)
    }

    @Test
    fun `tryPendingBackup은 정상 조건에서 syncUpData를 호출하고 pending을 false로 설정한다`() = runTest {
        syncRepository.setBackupPending(true)

        autoBackupManager.tryPendingBackup()

        assertEquals(1, syncRepository.syncUpCallCount)
        assertFalse(syncRepository.getBackupPending().first())
    }
}
