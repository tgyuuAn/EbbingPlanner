package com.tgyuu.sync.graph.connect

import androidx.lifecycle.viewModelScope
import com.tgyuu.common.base.BaseViewModel
import com.tgyuu.common.event.EventBus
import com.tgyuu.domain.model.Timer
import com.tgyuu.domain.repository.SyncRepository
import com.tgyuu.navigation.NavigationBus
import com.tgyuu.navigation.NavigationEvent
import com.tgyuu.sync.graph.connect.contract.ConnectIntent
import com.tgyuu.sync.graph.connect.contract.ConnectState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConnectViewModel @Inject constructor(
    private val syncRepository: SyncRepository,
    private val navigationBus: NavigationBus,
    private val eventBus: EventBus,
    private val timer: Timer,
) : BaseViewModel<ConnectState, ConnectIntent>(ConnectState()) {
    private var timerJob: Job? = null

    init {
        viewModelScope.launch {
            val uuid = syncRepository.getUUID()
            setState { copy(uuid = uuid) }
        }
    }

    override suspend fun processIntent(intent: ConnectIntent) {
        when (intent) {
            ConnectIntent.OnBackClick -> navigationBus.navigate(NavigationEvent.Up)
            ConnectIntent.OnClickConnectAnother -> connectAnother()
            ConnectIntent.OnClickGenerateCode -> generateCode()
            is ConnectIntent.OnMyCodeChange -> setMyCode(intent.code)
            is ConnectIntent.OnAnotherCodeChange -> setAnotherCode(intent.code)
        }
    }

    private fun setMyCode(code: String) {
        setState { copy(myCode = code) }
    }

    private fun setAnotherCode(code: String) {
        setState { copy(myCode = code) }
    }

    private suspend fun generateCode() {
        setState { copy(isGenerateButtonEnabled = false) }
        startTimer()
    }

    private suspend fun connectAnother() {

    }

    private fun startTimer() {
        timerJob?.cancel()

        timerJob = viewModelScope.launch {
            timer.startTimer()
                .collect { remaining ->
                    setState { copy(remainingTimeInSec = remaining) }

                    if (remaining == 0L) {
                        setState { copy(isGenerateButtonEnabled = true) }
                        timerJob?.cancel()
                    }
                }
        }
    }

    internal fun stopTimer() = timerJob?.cancel()

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
