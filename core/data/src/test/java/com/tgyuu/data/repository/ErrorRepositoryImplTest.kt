package com.tgyuu.data.repository

import com.tgyuu.network.source.SyncUploadException
import com.tgyuu.network.source.error.ErrorDataSource
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import java.net.UnknownHostException

class ErrorRepositoryImplTest {
    private val errorDataSource = mockk<ErrorDataSource>(relaxed = true)
    private val errorRepository = ErrorRepositoryImpl(errorDataSource)

    @Test
    fun `전송 실패는 non-fatal로 올리지 않고 로그 메시지로만 남긴다`() = runTest {
        val exception = SyncUploadException("todo_infos", listOf(1), UnknownHostException("Unable to resolve host"))

        errorRepository.logError(exception)

        coVerify(exactly = 0) { errorDataSource.logError(any()) }
        coVerify(exactly = 1) { errorDataSource.logMessage(match { "SyncUploadException" in it }) }
    }

    @Test
    fun `전송 실패가 아닌 예외는 non-fatal로 기록한다`() = runTest {
        val exception = IllegalStateException("bug")

        errorRepository.logError(exception)

        coVerify(exactly = 1) { errorDataSource.logError(exception) }
        coVerify(exactly = 0) { errorDataSource.logMessage(any()) }
    }
}
