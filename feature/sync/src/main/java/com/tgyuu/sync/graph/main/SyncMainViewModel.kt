package com.tgyuu.sync.graph.main

import androidx.lifecycle.viewModelScope
import com.tgyuu.analytics.AnalyticsEvent
import com.tgyuu.analytics.AnalyticsHelper
import com.tgyuu.common.base.BaseViewModel
import com.tgyuu.common.event.BottomSheetContent
import com.tgyuu.common.event.EbbingEvent
import com.tgyuu.common.event.EbbingEvent.ShowBottomSheet
import com.tgyuu.common.event.EventBus
import com.tgyuu.common.isNetworkError
import com.tgyuu.common.suspendRunCatching
import com.tgyuu.domain.model.ErrorBus
import com.tgyuu.domain.model.Timer
import com.tgyuu.domain.model.sync.ConnectResult
import com.tgyuu.domain.model.sync.ConnectedPeer
import com.tgyuu.domain.repository.SyncRepository
import com.tgyuu.common.ui.resource.ResourceProvider
import com.tgyuu.designsystem.R
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
import kotlinx.coroutines.isActive
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
    private val resourceProvider: ResourceProvider,
) : BaseViewModel<SyncMainState, SyncIntent>(SyncMainState()) {
    private var timerJob: Job? = null
    private var pollingJob: Job? = null
    private var disconnectPollingJob: Job? = null
    private val isProcessing = AtomicBoolean(false)

    internal suspend fun loadInitData() = coroutineScope {
        val linkCode = syncRepository.getLinkCode()
        if (linkCode != null) {
            val alive = suspendRunCatching {
                syncRepository.isLinkAlive()
            }.getOrDefault(true)

            if (!alive) {
                disconnectPollingJob?.cancel()
                syncRepository.clearLinkLocal()
                eventBus.sendEvent(EbbingEvent.ShowSnackBar(resourceProvider.getString(R.string.sync_peer_disconnected)))
            }
        }

        val uuidJob = launch {
            val uuid = syncRepository.getUuid()
            setState { copy(uuid = uuid) }
        }

        val deviceNameJob = launch {
            suspendRunCatching {
                syncRepository.getDeviceName()
            }.onSuccess { deviceName ->
                setState { copy(deviceName = deviceName) }
            }
        }

        val linkedUuidJob = launch {
            val connectedUuid = syncRepository.getConnectedUuid()
            val linkedUuid = connectedUuid ?: syncRepository.getStoredPeer()?.uuid
            setState { copy(linkedUuid = linkedUuid) }
        }

        val isConnected = syncRepository.getLinkCode() != null

        val localLastSyncedAtJob = launch {
            if (isConnected) return@launch
            val lastSyncedAt = syncRepository.getLocalSyncedAt()
            setState { copy(localLastSyncedAt = lastSyncedAt) }
        }

        // 동기화 쿨타임은 서버가 아니라 이 디바이스의 마지막 동기화 기준 10초
        val syncUpEnabledJob = launch {
            val localSyncedAt = syncRepository.getLocalSyncedAt()
            val isSyncUpEnabled = localSyncedAt == null ||
                    Duration.between(localSyncedAt, ZonedDateTime.now())
                        .toMillis() >= SYNC_UP_COOL_TIME
            setState { copy(isSyncUpEnabled = isSyncUpEnabled) }
        }

        val serverLastUpdatedAtJob = launch {
            val connectedUuid = syncRepository.getConnectedUuid()
            suspendRunCatching {
                syncRepository.getServerLastUpdatedAt()
            }.onSuccess { serverSyncInfo ->
                val serverLastUpdatedAt = serverSyncInfo?.lastUpdatedAt
                val connectedDeviceName = if (connectedUuid != null) {
                    serverSyncInfo?.connectedDeviceName
                } else {
                    syncRepository.getStoredPeer()?.deviceName
                }
                setState {
                    copy(
                        serverLastUpdatedAt = serverLastUpdatedAt,
                        connectedDeviceName = connectedDeviceName,
                        localLastSyncedAt = if (isConnected) serverLastUpdatedAt else localLastSyncedAt,
                    )
                }
            }
        }

        val connectInfoJob = launch {
            if (syncRepository.getLinkCode() != null) return@launch

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
                    if (syncRepository.getStoredPeer() == null) {
                        startConnectionPolling()
                    }
                }
            }
        }

        val disconnectWatchJob = launch {
            if (syncRepository.getLinkCode() != null && disconnectPollingJob?.isActive != true) {
                startDisconnectPolling()
            }
        }

        uuidJob.join()
        deviceNameJob.join()
        linkedUuidJob.join()
        localLastSyncedAtJob.join()
        syncUpEnabledJob.join()
        serverLastUpdatedAtJob.join()
        connectInfoJob.join()
        disconnectWatchJob.join()

        setState { copy(isInitialLoading = false) }
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
            eventBus.sendEvent(EbbingEvent.ShowSnackBar(resourceProvider.getString(R.string.sync_network_required)))
            return@launch
        }

        setState { copy(isNetworkLoading = true) }
        suspendRunCatching {
            syncRepository.syncUpData()
        }.onSuccess {
            eventBus.sendEvent(EbbingEvent.ShowSnackBar(resourceProvider.getString(R.string.sync_synced)))
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
            val message = if (error.isNetworkError()) {
                resourceProvider.getString(R.string.sync_network_check)
            } else {
                error.message ?: resourceProvider.getString(R.string.sync_sync_failed)
            }
            eventBus.sendEvent(EbbingEvent.ShowSnackBar(message))
        }.also {
            setState { copy(isNetworkLoading = false) }
        }
    }

    private fun disconnectAnother() = viewModelScope.launch {
        if (networkMonitor.networkState.value != NetworkState.Connected) {
            eventBus.sendEvent(EbbingEvent.ShowSnackBar(resourceProvider.getString(R.string.sync_network_required)))
            return@launch
        }

        setState { copy(isNetworkLoading = true) }
        suspendRunCatching {
            syncRepository.disconnectAnother()
        }.onSuccess {
            disconnectPollingJob?.cancel()
            loadInitData()

            setState {
                copy(
                    localLastSyncedAt = null,
                    serverLastUpdatedAt = null,
                )
            }

            eventBus.sendEvent(EbbingEvent.ShowSnackBar(resourceProvider.getString(R.string.sync_disconnect_success)))
        }.onFailure { error ->
            errorBus.sendError(error)
            val message = if (error.isNetworkError()) {
                resourceProvider.getString(R.string.sync_network_check)
            } else {
                resourceProvider.getString(R.string.sync_disconnect_failed)
            }
            eventBus.sendEvent(EbbingEvent.ShowSnackBar(message))
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
            eventBus.sendEvent(EbbingEvent.ShowSnackBar(resourceProvider.getString(R.string.sync_network_required)))
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
            startConnectionPolling()
            eventBus.sendEvent(ShowBottomSheet(content))
        }.onFailure { error ->
            errorBus.sendError(error)
            val message = if (error.isNetworkError()) {
                resourceProvider.getString(R.string.sync_network_check)
            } else {
                resourceProvider.getString(R.string.sync_generate_failed)
            }
            eventBus.sendEvent(EbbingEvent.ShowSnackBar(message))
        }
    }

    private suspend fun handleQrDetected(rawValue: String) {
        if (!rawValue.startsWith(EBBINGPLANNER_SCHEME)) return
        if (!isProcessing.compareAndSet(false, true)) return

        analyticsHelper.logEvent(
            AnalyticsEvent.Click(screenName = SCREEN_NAME, buttonName = "QrDetected")
        )

        if (networkMonitor.networkState.value != NetworkState.Connected) {
            eventBus.sendEvent(EbbingEvent.ShowSnackBar(resourceProvider.getString(R.string.sync_network_required)))
            isProcessing.set(false)
            return
        }

        val connectCode = rawValue.removePrefix(EBBINGPLANNER_SCHEME)
        setState { copy(isScanLoading = true) }

        suspendRunCatching {
            syncRepository.connectAnother(connectCode = connectCode)
        }.onSuccess { result ->
            setState { copy(isScanLoading = false, isScanning = false) }

            when (result) {
                is ConnectResult.Success -> {
                    setState { copy(connectedDeviceName = result.info.deviceName.ifEmpty { null }) }
                    eventBus.sendEvent(EbbingEvent.ShowSnackBar(resourceProvider.getString(R.string.sync_connect_success)))
                    loadInitData()
                }

                ConnectResult.InvalidOrExpired -> {
                    eventBus.sendEvent(EbbingEvent.ShowSnackBar(resourceProvider.getString(R.string.sync_code_invalid_or_expired)))
                    isProcessing.set(false)
                }

                ConnectResult.AlreadyLinkedSelf -> {
                    eventBus.sendEvent(EbbingEvent.ShowSnackBar(resourceProvider.getString(R.string.sync_already_linked_self)))
                    isProcessing.set(false)
                }

                ConnectResult.CodeAlreadyTaken -> {
                    eventBus.sendEvent(EbbingEvent.ShowSnackBar(resourceProvider.getString(R.string.sync_code_already_taken)))
                    isProcessing.set(false)
                }
            }
        }.onFailure { error ->
            setState { copy(isScanLoading = false) }
            errorBus.sendError(error)
            val message = if (error.isNetworkError()) {
                resourceProvider.getString(R.string.sync_network_check)
            } else {
                resourceProvider.getString(R.string.sync_connect_failed)
            }
            eventBus.sendEvent(EbbingEvent.ShowSnackBar(message))
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
                        eventBus.sendEvent(EbbingEvent.HideBottomSheet)
                        pollingJob?.cancel()
                        timerJob?.cancel()
                    }
                }
        }
    }

    private fun startConnectionPolling() {
        pollingJob?.cancel()

        pollingJob = viewModelScope.launch {
            while (isActive) {
                delay(CONNECTION_POLLING_INTERVAL)
                val peer = suspendRunCatching {
                    syncRepository.pollConnectedPeer()
                }.getOrNull() ?: continue

                onPeerConnected(peer)
                break
            }
        }
    }

    private suspend fun onPeerConnected(peer: ConnectedPeer) {
        eventBus.sendEvent(EbbingEvent.HideBottomSheet)
        timerJob?.cancel()

        syncRepository.getMyConnectCode()?.let { syncRepository.setLinkCode(it) }
        syncRepository.setStoredPeer(peer)
        syncRepository.clearMyConnectCode()

        setState {
            copy(
                connectCode = "",
                isGenerateButtonEnabled = true,
            )
        }
        eventBus.sendEvent(EbbingEvent.ShowSnackBar(resourceProvider.getString(R.string.sync_peer_connected, peer.deviceName)))
        loadInitData()
    }

    private fun startDisconnectPolling() {
        disconnectPollingJob?.cancel()

        disconnectPollingJob = viewModelScope.launch {
            while (isActive) {
                delay(CONNECTION_POLLING_INTERVAL)
                val alive = suspendRunCatching {
                    syncRepository.isLinkAlive()
                }.getOrDefault(true)

                if (!alive) {
                    onRemoteDisconnected()
                    break
                }
            }
        }
    }

    private suspend fun onRemoteDisconnected() {
        syncRepository.clearLinkLocal()

        setState {
            copy(
                linkedUuid = null,
                connectedDeviceName = null,
                localLastSyncedAt = null,
                serverLastUpdatedAt = null,
            )
        }
        eventBus.sendEvent(EbbingEvent.ShowSnackBar(resourceProvider.getString(R.string.sync_peer_disconnected)))
        loadInitData()
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        pollingJob?.cancel()
        disconnectPollingJob?.cancel()
    }

    companion object {
        private const val SCREEN_NAME = "SyncMain"
        private const val SYNC_DIALOG = "SyncDialog"
        private const val DISCONNECT_DIALOG = "DisconnectDialog"
        private const val SYNC_UP_COOL_TIME = 10_000L
        private const val CONNECTION_POLLING_INTERVAL = 5_000L
    }
}
