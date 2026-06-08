package com.tgyuu.sync.graph.main

import androidx.lifecycle.viewModelScope
import com.tgyuu.analytics.AnalyticsEvent
import com.tgyuu.analytics.AnalyticsHelper
import com.tgyuu.common.base.BaseViewModel
import com.tgyuu.common.event.BottomSheetContent
import com.tgyuu.common.event.EbbingEvent
import com.tgyuu.common.event.EbbingEvent.ShowBottomSheet
import com.tgyuu.common.event.EventBus
import com.tgyuu.common.suspendRunCatching
import com.tgyuu.domain.model.ErrorBus
import com.tgyuu.domain.model.Timer
import com.tgyuu.domain.repository.SyncRepository
import com.tgyuu.navigation.NavigationBus
import com.tgyuu.navigation.NavigationEvent
import com.tgyuu.sync.graph.main.contract.SyncIntent
import com.tgyuu.sync.graph.main.contract.SyncMainState
import com.tgyuu.sync.network.NetworkMonitor
import com.tgyuu.sync.network.NetworkState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.ZonedDateTime
import java.util.UUID
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

private const val EBBINGPLANNER_SCHEME = "ebbingplanner://"

@HiltViewModel
class SyncMainViewModel @Inject constructor(
    private val syncRepository: SyncRepository,
    private val networkMonitor: NetworkMonitor,
    private val navigationBus: NavigationBus,
    private val errorBus: ErrorBus,
    internal val eventBus: EventBus,
    private val analyticsHelper: AnalyticsHelper,
    private val timer: Timer,
) : BaseViewModel<SyncMainState, SyncIntent>(SyncMainState()) {
    private var timerJob: Job? = null
    private val isProcessing = AtomicBoolean(false)

    internal suspend fun loadInitData() = coroutineScope {
        val uuidJob = launch {
            val uuid = syncRepository.getUuid()
            setState { copy(uuid = uuid) }
        }

        val linkedUuidJob = launch {
            val linkedUuid = syncRepository.getConnectedUuid()
            setState { copy(linkedUuid = linkedUuid) }
        }

        val localLastSyncedAtJob = launch {
            val lastSyncedAt = syncRepository.getLocalSyncedAt()
            setState { copy(localLastSyncedAt = lastSyncedAt) }
        }

        val serverLastUpdatedAtJob = launch {
            suspendRunCatching {
                syncRepository.getServerLastUpdatedAt()
            }.onSuccess { serverLastUpdatedAt ->
                val isSyncUpEnabled = serverLastUpdatedAt == null ||
                        Duration.between(serverLastUpdatedAt, ZonedDateTime.now())
                            .toMillis() >= SYNC_UP_COOL_TIME
                setState {
                    copy(
                        serverLastUpdatedAt = serverLastUpdatedAt,
                        isSyncUpEnabled = isSyncUpEnabled,
                    )
                }
            }
        }

        val connectInfoJob = launch {
            val myConnectCode = syncRepository.getMyConnectCode()
            val expiration = syncRepository.getConnectCodeExpiration()

            if (myConnectCode != null && expiration != null) {
                val now = ZonedDateTime.now()
                if (expiration.isAfter(now)) {
                    val remainingSec = Duration.between(now, expiration).seconds
                    setState {
                        copy(
                            connectCode = myConnectCode,
                            isGenerateButtonEnabled = false,
                            remainingTimeInSec = remainingSec,
                        )
                    }
                    startTimer(fromSec = remainingSec)
                }
            }
        }

        uuidJob.join()
        linkedUuidJob.join()
        serverLastUpdatedAtJob.join()
        localLastSyncedAtJob.join()
        connectInfoJob.join()
    }

    override suspend fun processIntent(intent: SyncIntent) {
        when (intent) {
            SyncIntent.OnBackClick -> onBackClick()
            SyncIntent.OnSyncUpClick -> onSyncUpClick()
            SyncIntent.OnDisconnectClick -> onDisconnectClick()
            SyncIntent.OnSyncDialogBackClick -> onSyncDialogBackClick()
            SyncIntent.OnSyncDialogSyncClick -> onSyncDialogSyncClick()
            SyncIntent.OnDisconnectDialogBackClick -> onDisconnectDialogBackClick()
            SyncIntent.OnDisconnectDialogDisconnectClick -> onDisconnectDialogDisconnectClick()
            // QR 연동
            is SyncIntent.OnClickGenerateCode -> generateCode(intent.content)
            SyncIntent.OnScanQrClick -> {
                analyticsHelper.logEvent(
                    AnalyticsEvent.Click(screenName = SCREEN_NAME, buttonName = "ScanQr")
                )
                isProcessing.set(false)
                setState { copy(isScanning = true) }
            }
            SyncIntent.OnDismissScan -> setState { copy(isScanning = false) }
            is SyncIntent.OnQrDetected -> handleQrDetected(intent.rawValue)
        }
    }

    private suspend fun onBackClick() {
        analyticsHelper.logEvent(
            AnalyticsEvent.Click(screenName = SCREEN_NAME, buttonName = "Back")
        )
        navigationBus.navigate(NavigationEvent.Up)
    }

    private fun onSyncUpClick() {
        analyticsHelper.logEvent(
            AnalyticsEvent.Click(screenName = SCREEN_NAME, buttonName = "Sync")
        )
        syncUpData()
    }

    private fun onDisconnectClick() {
        analyticsHelper.logEvent(
            AnalyticsEvent.Click(screenName = SCREEN_NAME, buttonName = "Disconnect")
        )
        disconnectAnother()
    }

    private fun onSyncDialogBackClick() {
        analyticsHelper.logEvent(
            AnalyticsEvent.Click(screenName = SYNC_DIALOG, buttonName = "Back")
        )
    }

    private fun onSyncDialogSyncClick() {
        analyticsHelper.logEvent(
            AnalyticsEvent.Click(screenName = SYNC_DIALOG, buttonName = "Sync")
        )
        syncUpData()
    }

    private fun onDisconnectDialogBackClick() {
        analyticsHelper.logEvent(
            AnalyticsEvent.Click(screenName = DISCONNECT_DIALOG, buttonName = "Back")
        )
    }

    private fun onDisconnectDialogDisconnectClick() {
        analyticsHelper.logEvent(
            AnalyticsEvent.Click(screenName = DISCONNECT_DIALOG, buttonName = "Disconnect")
        )
        disconnectAnother()
    }

    private fun syncUpData() = viewModelScope.launch {
        if (networkMonitor.networkState.value != NetworkState.Connected) {
            eventBus.sendEvent(EbbingEvent.ShowSnackBar("네트워크가 연결되어 있지 않습니다."))
            return@launch
        }

        setState { copy(isNetworkLoading = true) }
        suspendRunCatching {
            syncRepository.syncUpData()
        }.onSuccess {
            eventBus.sendEvent(EbbingEvent.ShowSnackBar("데이터를 동기화 하였습니다."))
            setState {
                copy(
                    localLastSyncedAt = it,
                    serverLastUpdatedAt = it,
                    isSyncUpEnabled = false,
                )
            }

            launch {
                delay(SYNC_UP_COOL_TIME)
                setState { copy(isSyncUpEnabled = true) }
            }
        }.onFailure { error ->
            errorBus.sendError(error)
            eventBus.sendEvent(EbbingEvent.ShowSnackBar(error.message ?: "동기화에 실패하였습니다."))
        }.also {
            setState { copy(isNetworkLoading = false) }
        }
    }

    private fun disconnectAnother() = viewModelScope.launch {
        if (networkMonitor.networkState.value != NetworkState.Connected) {
            eventBus.sendEvent(EbbingEvent.ShowSnackBar("네트워크가 연결되어 있지 않습니다."))
            return@launch
        }

        setState { copy(isNetworkLoading = true) }
        suspendRunCatching {
            syncRepository.disconnectAnother()
        }.onSuccess {
            loadInitData()

            setState {
                copy(
                    localLastSyncedAt = null,
                    serverLastUpdatedAt = null,
                )
            }

            eventBus.sendEvent(EbbingEvent.ShowSnackBar("연동 해제에 성공하였습니다."))
        }.onFailure { error ->
            errorBus.sendError(error)
            eventBus.sendEvent(EbbingEvent.ShowSnackBar("연동 해제에 실패하였습니다."))
        }.also {
            setState { copy(isNetworkLoading = false) }
        }
    }

    // QR 연동

    private suspend fun generateCode(content: BottomSheetContent) {
        analyticsHelper.logEvent(
            AnalyticsEvent.Click(screenName = SCREEN_NAME, buttonName = "GenerateCode")
        )

        if (networkMonitor.networkState.value != NetworkState.Connected) {
            eventBus.sendEvent(EbbingEvent.ShowSnackBar("네트워크가 연결되어 있지 않습니다."))
            return
        }

        val autoGeneratedCode = UUID.randomUUID().toString().replace("-", "").take(20)

        suspendRunCatching {
            syncRepository.generateConnectCode(connectCode = autoGeneratedCode)
        }.onSuccess {
            setState {
                copy(
                    connectCode = autoGeneratedCode,
                    isGenerateButtonEnabled = false,
                )
            }
            startTimer()
            eventBus.sendEvent(ShowBottomSheet(content))
        }.onFailure { error ->
            errorBus.sendError(error)
            eventBus.sendEvent(EbbingEvent.ShowSnackBar("QR 코드 생성에 실패했습니다. 네트워크를 확인해 주세요."))
        }
    }

    private suspend fun handleQrDetected(rawValue: String) {
        if (!rawValue.startsWith(EBBINGPLANNER_SCHEME)) return
        if (!isProcessing.compareAndSet(false, true)) return

        analyticsHelper.logEvent(
            AnalyticsEvent.Click(screenName = SCREEN_NAME, buttonName = "QrDetected")
        )

        if (networkMonitor.networkState.value != NetworkState.Connected) {
            eventBus.sendEvent(EbbingEvent.ShowSnackBar("네트워크가 연결되어 있지 않습니다."))
            isProcessing.set(false)
            return
        }

        val connectCode = rawValue.removePrefix(EBBINGPLANNER_SCHEME)
        setState { copy(isScanLoading = true) }

        suspendRunCatching {
            syncRepository.connectAnother(connectCode = connectCode)
        }.onSuccess { connectInfo ->
            setState { copy(isScanLoading = false, isScanning = false) }

            if (connectInfo == null) {
                eventBus.sendEvent(EbbingEvent.ShowSnackBar("생성되지 않은 코드이거나, 유효시간이 만료되었습니다."))
                isProcessing.set(false)
                return@onSuccess
            }

            eventBus.sendEvent(EbbingEvent.ShowSnackBar("연동에 성공하였습니다."))
            loadInitData()
        }.onFailure { error ->
            setState { copy(isScanLoading = false) }
            errorBus.sendError(error)
            eventBus.sendEvent(EbbingEvent.ShowSnackBar("연동에 실패했습니다. 다시 시도해 주세요."))
            isProcessing.set(false)
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
                                connectCode = "",
                                isGenerateButtonEnabled = true,
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

    companion object {
        private const val SCREEN_NAME = "SyncMain"
        private const val SYNC_DIALOG = "SyncDialog"
        private const val DISCONNECT_DIALOG = "DisconnectDialog"
        private const val SYNC_UP_COOL_TIME = 10_000L
    }
}
