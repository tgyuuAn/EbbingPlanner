package com.tgyuu.sync.graph.connect

import androidx.lifecycle.viewModelScope
import com.tgyuu.analytics.AnalyticsEvent
import com.tgyuu.analytics.AnalyticsHelper
import com.tgyuu.common.base.BaseViewModel
import com.tgyuu.common.event.EbbingEvent
import com.tgyuu.common.event.EventBus
import com.tgyuu.common.suspendRunCatching
import com.tgyuu.domain.model.ErrorBus
import com.tgyuu.domain.model.Timer
import com.tgyuu.domain.repository.SyncRepository
import com.tgyuu.navigation.NavigationBus
import com.tgyuu.navigation.NavigationEvent
import com.tgyuu.sync.graph.connect.contract.ConnectIntent
import com.tgyuu.sync.graph.connect.contract.ConnectState
import com.tgyuu.sync.network.NetworkMonitor
import com.tgyuu.sync.network.NetworkState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class ConnectViewModel @Inject constructor(
    private val syncRepository: SyncRepository,
    private val networkMonitor: NetworkMonitor,
    private val navigationBus: NavigationBus,
    private val errorBus: ErrorBus,
    private val eventBus: EventBus,
    private val analyticsHelper: AnalyticsHelper,
    private val timer: Timer,
) : BaseViewModel<ConnectState, ConnectIntent>(ConnectState()) {
    private var timerJob: Job? = null

    init {
        viewModelScope.launch {
            val uuid = syncRepository.getUuid()
            setState { copy(uuid = uuid) }
        }
    }

    internal suspend fun getMyConnectInfo() {
        val myConnectCode = syncRepository.getMyConnectCode()
        val expiration = syncRepository.getConnectCodeExpiration()

        if (myConnectCode != null && expiration != null) {
            val now = ZonedDateTime.now()
            if (expiration.isAfter(now)) {
                val remainingSec = Duration.between(now, expiration).seconds

                setState {
                    copy(
                        myCode = myConnectCode,
                        isGenerateButtonEnabled = false,
                        isConnectButtonEnabled = false,
                        remainingTimeInSec = remainingSec
                    )
                }

                startTimer(fromSec = remainingSec)
            }
        }
    }

    override suspend fun processIntent(intent: ConnectIntent) {
        when (intent) {
            ConnectIntent.OnBackClick -> {
                analyticsHelper.logEvent(
                    AnalyticsEvent.Click(screenName = "Connect", buttonName = "Back")
                )
                navigationBus.navigate(NavigationEvent.Up)
            }
            ConnectIntent.OnClickConnectAnother -> connectAnother()
            ConnectIntent.OnClickGenerateCode -> generateCode()
            is ConnectIntent.OnMyCodeChange -> setMyCode(intent.code)
            is ConnectIntent.OnAnotherCodeChange -> setAnotherCode(intent.code)
        }
    }

    private fun setMyCode(code: String) {
        val filtered = code.replace("\\s+".toRegex(), "")
        if (code.length > 20) return

        setState {
            copy(
                myCode = filtered,
                anotherCode = "",
            )
        }
    }

    private fun setAnotherCode(code: String) {
        val filtered = code.replace("\\s+".toRegex(), "")
        if (code.length > 20) return

        setState {
            copy(
                myCode = "",
                anotherCode = filtered,
            )
        }
    }

    private suspend fun generateCode() {
        analyticsHelper.logEvent(
            AnalyticsEvent.Click(screenName = "Connect", buttonName = "GenerateCode")
        )

        if (currentState.myCode.isEmpty()) {
            analyticsHelper.logEvent(
                AnalyticsEvent.Action(
                    screenName = "Connect",
                    actionName = "GenerateCode",
                    actionResult = "EmptyCode",
                )
            )
            eventBus.sendEvent(EbbingEvent.ShowSnackBar("연동 코드는 비어있을 수 없습니다."))
            return
        }

        val networkState = networkMonitor.networkState.value
        if (networkState != NetworkState.Connected) {
            analyticsHelper.logEvent(
                AnalyticsEvent.Action(
                    screenName = "Connect",
                    actionName = "GenerateCode",
                    actionResult = "NetworkNotConnected",
                    properties = mapOf("networkState" to networkState.toString()),
                )
            )
            eventBus.sendEvent(EbbingEvent.ShowSnackBar("네트워크가 연결되어 있지 않습니다."))
            return
        }

        suspendRunCatching {
            syncRepository.generateConnectCode(connectCode = currentState.myCode)
        }.onSuccess {
            analyticsHelper.logEvent(
                AnalyticsEvent.Action(
                    screenName = "Connect",
                    actionName = "GenerateCode",
                    actionResult = "Success",
                )
            )
            eventBus.sendEvent(EbbingEvent.ShowSnackBar("연동 코드 생성에 성공하였습니다."))

            setState {
                copy(
                    isConnectButtonEnabled = false,
                    isGenerateButtonEnabled = false,
                )
            }

            startTimer()
        }.onFailure { error ->
            errorBus.sendError(error)
            eventBus.sendEvent(EbbingEvent.ShowSnackBar("유효하지 않은 코드이거나, 네트워크가 불안정합니다."))
        }
    }

    private suspend fun connectAnother() {
        analyticsHelper.logEvent(
            AnalyticsEvent.Click(screenName = "Connect", buttonName = "ConnectCode")
        )

        if (currentState.anotherCode.isEmpty()) {
            analyticsHelper.logEvent(
                AnalyticsEvent.Action(
                    screenName = "Connect",
                    actionName = "ConnectAnother",
                    actionResult = "EmptyCode",
                )
            )
            eventBus.sendEvent(EbbingEvent.ShowSnackBar("연동 코드는 비어있을 수 없습니다."))
            return
        }

        val networkState = networkMonitor.networkState.value
        if (networkState != NetworkState.Connected) {
            analyticsHelper.logEvent(
                AnalyticsEvent.Action(
                    screenName = "Connect",
                    actionName = "ConnectAnother",
                    actionResult = "NetworkNotConnected",
                    properties = mapOf("networkState" to networkState.toString()),
                )
            )
            eventBus.sendEvent(EbbingEvent.ShowSnackBar("네트워크가 연결되어 있지 않습니다."))
            return
        }

        suspendRunCatching {
            syncRepository.connectAnother(connectCode = currentState.anotherCode)
        }.onSuccess { connectInfo ->
            if (connectInfo == null) {
                analyticsHelper.logEvent(
                    AnalyticsEvent.Action(
                        screenName = "Connect",
                        actionName = "ConnectAnother",
                        actionResult = "InvalidOrExpired",
                    )
                )
                eventBus.sendEvent(EbbingEvent.ShowSnackBar("생성되지 않은 코드이거나, 유효시간이 만료되었습니다."))
                return@onSuccess
            }

            if (connectInfo.uuid == currentState.uuid) {
                analyticsHelper.logEvent(
                    AnalyticsEvent.Action(
                        screenName = "Connect",
                        actionName = "ConnectAnother",
                        actionResult = "SelfConnect",
                    )
                )
                eventBus.sendEvent(EbbingEvent.ShowSnackBar("나와는 연동할 수 없습니다."))
                return@onSuccess
            }

            analyticsHelper.logEvent(
                AnalyticsEvent.Action(
                    screenName = "Connect",
                    actionName = "ConnectAnother",
                    actionResult = "Success",
                )
            )
            eventBus.sendEvent(EbbingEvent.ShowSnackBar("연동에 성공하였습니다."))
            navigationBus.navigate(NavigationEvent.Up)
        }.onFailure { error ->
            errorBus.sendError(error)
            eventBus.sendEvent(EbbingEvent.ShowSnackBar("생성되지 않은 코드이거나, 네트워크가 불안정합니다."))
        }
    }

    private fun startTimer(fromSec: Long = Timer.DEFAULT_DURATION_IN_SEC) {
        timerJob?.cancel()

        timerJob = viewModelScope.launch {
            timer.startTimer(durationInSec = fromSec)
                .collect { remaining ->
                    setState { copy(remainingTimeInSec = remaining) }
                    if (remaining <= 0L) {
                        setState {
                            copy(
                                myCode = "",
                                isGenerateButtonEnabled = true,
                                isConnectButtonEnabled = true,
                            )
                        }
                        timerJob?.cancel()
                    }
                }
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
