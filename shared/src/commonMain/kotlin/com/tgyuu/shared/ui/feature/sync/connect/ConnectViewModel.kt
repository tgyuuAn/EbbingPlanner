package com.tgyuu.shared.ui.feature.sync.connect

import com.tgyuu.shared.base.BaseViewModel
import com.tgyuu.shared.common.now
import com.tgyuu.shared.domain.repository.SyncRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant

class ConnectViewModel(
    private val syncRepository: SyncRepository? = null,
    private val onNavigateBack: () -> Unit,
    private val onShowSnackbar: (String) -> Unit = {},
) : BaseViewModel<ConnectState, ConnectIntent>(ConnectState()) {

    private var timerJob: Job? = null

    init {
        loadInitData()
    }

    private fun loadInitData() {
        safeScope.launch {
            try {
                val uuid = syncRepository?.getUuid() ?: ""
                setState { copy(uuid = uuid) }
                loadExistingConnectCode()
            } catch (_: Exception) {}
        }
    }

    private suspend fun loadExistingConnectCode() {
        val code = syncRepository?.getMyConnectCode() ?: return
        val expiration = syncRepository?.getConnectCodeExpiration() ?: return

        val now = LocalDateTime.now()
        if (expiration > now) {
            val nowInstant = now.toInstant(TimeZone.currentSystemDefault())
            val expirationInstant = expiration.toInstant(TimeZone.currentSystemDefault())
            val remainingSec = (expirationInstant - nowInstant).inWholeSeconds

            setState {
                copy(
                    myCode = code,
                    isGenerateButtonEnabled = false,
                    isConnectButtonEnabled = false,
                    remainingTimeInSec = remainingSec,
                )
            }
            startTimer(fromSec = remainingSec)
        }
    }

    override suspend fun processIntent(intent: ConnectIntent) {
        when (intent) {
            ConnectIntent.OnBackClick -> onNavigateBack()
            is ConnectIntent.OnMyCodeChange -> setMyCode(intent.code)
            is ConnectIntent.OnAnotherCodeChange -> setAnotherCode(intent.code)
            ConnectIntent.OnClickGenerateCode -> generateCode()
            ConnectIntent.OnClickConnectAnother -> connectAnother()
        }
    }

    private fun setMyCode(code: String) {
        val filtered = code.replace("\\s+".toRegex(), "")
        if (filtered.length > 20) return
        setState { copy(myCode = filtered, anotherCode = "") }
    }

    private fun setAnotherCode(code: String) {
        val filtered = code.replace("\\s+".toRegex(), "")
        if (filtered.length > 20) return
        setState { copy(myCode = "", anotherCode = filtered) }
    }

    private suspend fun generateCode() {
        if (currentState.myCode.isEmpty()) {
            onShowSnackbar("연동 코드는 비어있을 수 없습니다.")
            return
        }

        try {
            syncRepository?.generateConnectCode(currentState.myCode)
            setState {
                copy(
                    isConnectButtonEnabled = false,
                    isGenerateButtonEnabled = false,
                )
            }
            onShowSnackbar("연동 코드 생성에 성공하였습니다.")
            startTimer()
        } catch (e: Exception) {
            onShowSnackbar("유효하지 않은 코드이거나, 네트워크가 불안정합니다.")
        }
    }

    private suspend fun connectAnother() {
        if (currentState.anotherCode.isEmpty()) {
            onShowSnackbar("연동 코드는 비어있을 수 없습니다.")
            return
        }

        try {
            val connectInfo = syncRepository?.connectAnother(currentState.anotherCode)
            if (connectInfo == null) {
                onShowSnackbar("생성되지 않은 코드이거나, 유효시간이 만료되었습니다.")
                return
            }
            if (connectInfo.uuid == currentState.uuid) {
                onShowSnackbar("나와는 연동할 수 없습니다.")
                return
            }
            onShowSnackbar("연동에 성공하였습니다.")
            onNavigateBack()
        } catch (e: Exception) {
            onShowSnackbar("생성되지 않은 코드이거나, 네트워크가 불안정합니다.")
        }
    }

    private fun startTimer(fromSec: Long = ConnectState.DEFAULT_DURATION_IN_SEC) {
        timerJob?.cancel()
        timerJob = safeScope.launch {
            var remaining = fromSec
            while (remaining > 0) {
                setState { copy(remainingTimeInSec = remaining) }
                delay(1000L)
                remaining--
            }
            setState {
                copy(
                    myCode = "",
                    isGenerateButtonEnabled = true,
                    isConnectButtonEnabled = true,
                    remainingTimeInSec = ConnectState.DEFAULT_DURATION_IN_SEC,
                )
            }
        }
    }
}
