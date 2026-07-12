package com.tgyuu.data.repository

import com.tgyuu.database.source.repeatcycle.LocalRepeatCycleDataSource
import com.tgyuu.database.source.sync.LocalSyncTransactionDataSource
import com.tgyuu.database.source.tag.LocalTagDataSource
import com.tgyuu.database.source.todo.LocalTodoDataSource
import com.tgyuu.datastore.datasource.sync.LocalSyncDataSource
import com.tgyuu.deviceinfo.DeviceInfoProvider
import com.tgyuu.domain.repository.ErrorRepository
import com.tgyuu.network.source.SyncRemoteDataSource
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.ZonedDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class SyncRepositoryImplTest {
    private val syncDataSource = mockk<SyncRemoteDataSource>(relaxed = true)
    private val localTagDataSource = mockk<LocalTagDataSource>(relaxed = true)
    private val localTodoDataSource = mockk<LocalTodoDataSource>(relaxed = true)
    private val localRepeatCycleDataSource = mockk<LocalRepeatCycleDataSource>(relaxed = true)
    private val localSyncDataSource = mockk<LocalSyncDataSource>(relaxed = true)
    private val localSyncTransactionDataSource = mockk<LocalSyncTransactionDataSource>(relaxed = true)
    private val deviceInfoProvider = mockk<DeviceInfoProvider>(relaxed = true)
    private val errorRepository = mockk<ErrorRepository>(relaxed = true)

    private lateinit var repository: SyncRepositoryImpl

    @BeforeEach
    fun setUp() {
        every { localSyncDataSource.uuid } returns flowOf(UUID)
        every { localSyncDataSource.connectedUuid } returns flowOf(null)
        every { localSyncDataSource.lastSyncTime } returns flowOf(null)

        repository = SyncRepositoryImpl(
            syncDataSource = syncDataSource,
            localTagDataSource = localTagDataSource,
            localTodoDataSource = localTodoDataSource,
            localRepeatCycleDataSource = localRepeatCycleDataSource,
            localSyncDataSource = localSyncDataSource,
            localSyncTransactionDataSource = localSyncTransactionDataSource,
            deviceInfoProvider = deviceInfoProvider,
            errorRepository = errorRepository,
        )
    }

    @Test
    fun `업로드가 실패해도 연동 코드는 발급되고 실패는 로깅된다`() = runTest {
        coEvery {
            syncDataSource.uploadData(any(), any(), any(), any(), any(), any())
        } throws RuntimeException("server 500")
        coEvery {
            syncDataSource.generateConnectCode(any(), any(), any())
        } returns FIXED_TIME

        val result = repository.generateConnectCode(CONNECT_CODE)

        assertEquals(FIXED_TIME, result)
        coVerify { syncDataSource.generateConnectCode(UUID, CONNECT_CODE, any()) }
        coVerify { errorRepository.logError(any()) }
    }

    @Test
    fun `업로드 성공 시 연동 코드를 발급하고 에러를 로깅하지 않는다`() = runTest {
        coEvery {
            syncDataSource.uploadData(any(), any(), any(), any(), any(), any())
        } returns FIXED_TIME
        coEvery {
            syncDataSource.generateConnectCode(any(), any(), any())
        } returns FIXED_TIME

        val result = repository.generateConnectCode(CONNECT_CODE)

        assertEquals(FIXED_TIME, result)
        coVerify { syncDataSource.generateConnectCode(UUID, CONNECT_CODE, any()) }
        coVerify(exactly = 0) { errorRepository.logError(any()) }
    }

    private companion object {
        private const val UUID = "uuid"
        private const val CONNECT_CODE = "TESTCODE"
        private val FIXED_TIME: ZonedDateTime = ZonedDateTime.parse("2026-01-01T00:00:00Z")
    }
}
