package com.tgyuu.data.repository

import com.tgyuu.network.source.error.ErrorDataSource
import com.tgyuu.network.util.isTransportError
import com.tgyuu.domain.repository.ErrorRepository

class ErrorRepositoryImpl constructor(
    private val errorDataSource: ErrorDataSource,
) : ErrorRepository {
    override suspend fun logError(exception: Throwable) {
        // 오프라인·백그라운드 네트워크 차단 같은 전송 실패는 코드로 고칠 수 없는 환경 요인이라
        // non-fatal 이슈로 올리지 않고, 다른 리포트의 맥락으로만 남긴다.
        if (exception.isTransportError()) {
            errorDataSource.logMessage(
                "transport error: ${exception::class.java.name}: ${exception.message?.take(MESSAGE_LOG_LIMIT)}"
            )
            return
        }
        errorDataSource.logError(exception)
    }

    override suspend fun setUserId(userId: String) = errorDataSource.setUserId(userId)
    override suspend fun clearUserId() = errorDataSource.clearUserId()

    private companion object {
        private const val MESSAGE_LOG_LIMIT = 300
    }
}
