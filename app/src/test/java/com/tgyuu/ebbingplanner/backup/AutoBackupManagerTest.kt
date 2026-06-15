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
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
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
    fun `네트워크 미연결 시 백업하지 않고 pending 상태가 된다`() = runTest {
        networkState.value = NetworkState.NotConnected

        autoBackupManager.onAppStop()

        assertEquals(0, syncRepository.syncUpCallCount)

        // 네트워크 복구 후 tryPendingBackup 호출 시 백업이 수행되어야 한다
        networkState.value = NetworkState.Connected
        autoBackupManager.tryPendingBackup()

        assertEquals(1, syncRepository.syncUpCallCount)
    }

    @Test
    fun `cool time 미경과 시 백업하지 않고 pending 상태가 된다`() = runTest {
        syncRepository.serverLastUpdatedAt = ZonedDateTime.now()

        autoBackupManager.onAppStop()

        assertEquals(0, syncRepository.syncUpCallCount)
    }

    @Test
    fun `정상 조건에서 syncUpData를 호출한다`() = runTest {
        autoBackupManager.onAppStop()

        assertEquals(1, syncRepository.syncUpCallCount)
    }

    @Test
    fun `syncUpData 실패 시 pending 상태가 된다`() = runTest {
        syncRepository.shouldSyncFail = true

        autoBackupManager.onAppStop()

        assertEquals(1, syncRepository.syncUpCallCount)

        // 실패 후 재시도 시 백업이 수행되어야 한다
        syncRepository.shouldSyncFail = false
        autoBackupManager.tryPendingBackup()

        assertEquals(2, syncRepository.syncUpCallCount)
    }

    @Test
    fun `tryPendingBackup은 pending이 아니면 백업하지 않는다`() = runTest {
        autoBackupManager.tryPendingBackup()

        assertEquals(0, syncRepository.syncUpCallCount)
    }

    @Test
    fun `tryPendingBackup은 pending 상태에서 백업을 수행한다`() = runTest {
        // onAppStop에서 네트워크 미연결로 pending 상태 만들기
        networkState.value = NetworkState.NotConnected
        autoBackupManager.onAppStop()

        // 네트워크 복구
        networkState.value = NetworkState.Connected
        autoBackupManager.tryPendingBackup()

        assertEquals(1, syncRepository.syncUpCallCount)
    }
}
