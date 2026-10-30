package com.tgyuu.network.util

import com.tgyuu.network.source.SyncUploadException
import io.github.jan.supabase.exceptions.HttpRequestException
import io.ktor.client.request.HttpRequestBuilder
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class TransportErrorUtilTest {
    @Test
    fun `Supabase 전송 실패는 전송 오류로 판단한다`() {
        val exception = HttpRequestException(
            "Unable to resolve host \"example.supabase.co\": No address associated with hostname",
            HttpRequestBuilder(),
        )

        assertTrue(exception.isTransportError())
    }

    @Test
    fun `업로드 예외로 감싸진 전송 실패도 전송 오류로 판단한다`() {
        val exception = SyncUploadException(
            table = "todo_infos",
            rows = listOf(1),
            cause = HttpRequestException("timeout", HttpRequestBuilder()),
        )

        assertTrue(exception.isTransportError())
    }

    @Test
    fun `java net 소켓 계열 예외는 전송 오류로 판단한다`() {
        assertTrue(UnknownHostException().isTransportError())
        assertTrue(ConnectException().isTransportError())
        assertTrue(SocketTimeoutException().isTransportError())
    }

    @Test
    fun `네트워크와 무관한 IOException과 일반 예외는 전송 오류가 아니다`() {
        assertFalse(IOException("datastore corrupted").isTransportError())
        assertFalse(IllegalStateException("bug").isTransportError())
        assertFalse(SyncUploadException("todo_infos", listOf(1), IllegalArgumentException()).isTransportError())
    }

    @Test
    fun `cause 순환 구조에서도 무한 루프 없이 판단한다`() {
        val first = Exception("first")
        val second = Exception("second", first)
        first.initCause(second)

        assertFalse(first.isTransportError())
    }
}
