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
import ebbingplanner.shared.generated.resources.Res
import ebbingplanner.shared.generated.resources.snack_cannot_link_self
import ebbingplanner.shared.generated.resources.snack_code_empty
import ebbingplanner.shared.generated.resources.snack_code_expired
import ebbingplanner.shared.generated.resources.snack_code_generated
import ebbingplanner.shared.generated.resources.snack_code_invalid_or_network
import ebbingplanner.shared.generated.resources.snack_code_invalid_or_network2
import ebbingplanner.shared.generated.resources.snack_link_success
import org.jetbrains.compose.resources.getString

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
            onShowSnackbar(getString(Res.string.snack_code_empty))
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
            onShowSnackbar(getString(Res.string.snack_code_generated))
            startTimer()
        } catch (e: Exception) {
            onShowSnackbar(getString(Res.string.snack_code_invalid_or_network))
        }
    }

    private suspend fun connectAnother() {
        if (currentState.anotherCode.isEmpty()) {
            onShowSnackbar(getString(Res.string.snack_code_empty))
            return
        }

        try {
            val connectInfo = syncRepository?.connectAnother(currentState.anotherCode)
            if (connectInfo == null) {
                onShowSnackbar(getString(Res.string.snack_code_expired))
                return
            }
            if (connectInfo.uuid == currentState.uuid) {
                onShowSnackbar(getString(Res.string.snack_cannot_link_self))
                return
            }
            onShowSnackbar(getString(Res.string.snack_link_success))
            onNavigateBack()
        } catch (e: Exception) {
            onShowSnackbar(getString(Res.string.snack_code_invalid_or_network2))
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
