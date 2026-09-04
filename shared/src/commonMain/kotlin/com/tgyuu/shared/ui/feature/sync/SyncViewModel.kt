package com.tgyuu.shared.ui.feature.sync

import com.tgyuu.shared.base.BaseViewModel
import com.tgyuu.shared.platform.logClick
import com.tgyuu.shared.common.now
import com.tgyuu.shared.common.suspendRunCatching
import com.tgyuu.shared.domain.model.Timer
import com.tgyuu.shared.domain.model.sync.ConnectResult
import com.tgyuu.shared.domain.model.sync.ConnectedPeer
import com.tgyuu.shared.domain.repository.SyncRepository
import ebbingplanner.shared.generated.resources.Res
import ebbingplanner.shared.generated.resources.sync_already_latest
import ebbingplanner.shared.generated.resources.sync_already_linked_self
import ebbingplanner.shared.generated.resources.sync_code_already_taken
import ebbingplanner.shared.generated.resources.sync_code_invalid_or_expired
import ebbingplanner.shared.generated.resources.sync_connect_failed
import ebbingplanner.shared.generated.resources.sync_connect_success
import ebbingplanner.shared.generated.resources.sync_disconnect_failed
import ebbingplanner.shared.generated.resources.sync_disconnect_success
import ebbingplanner.shared.generated.resources.sync_generate_failed
import ebbingplanner.shared.generated.resources.sync_device_info_copied
import ebbingplanner.shared.generated.resources.sync_peer_connected
import ebbingplanner.shared.generated.resources.sync_peer_disconnected
import ebbingplanner.shared.generated.resources.sync_sync_failed
import ebbingplanner.shared.generated.resources.sync_synced
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import org.jetbrains.compose.resources.getString
import kotlin.random.Random

class SyncViewModel(
    private val syncRepository: SyncRepository,
    private val onNavigateBack: () -> Unit,
    private val onNavigateToRestore: () -> Unit = {},
    private val onShowSnackbar: (String) -> Unit = {},
    private val analyticsHelper: com.tgyuu.shared.platform.AnalyticsHelper? = null,
    private val timer: Timer = Timer(),
) : BaseViewModel<SyncState, SyncIntent>(SyncState()) {
    private var timerJob: Job? = null
    private var pollingJob: Job? = null
    private var disconnectPollingJob: Job? = null
    private var isProcessingQr: Boolean = false

    init {
        safeScope.launch { loadInitData() }
    }

    internal suspend fun loadInitData() = coroutineScope {
        val linkCode = syncRepository.getLinkCode()
        if (linkCode != null) {
            val alive = suspendRunCatching {
                syncRepository.isLinkAlive()
            }.getOrDefault(true)

            if (!alive) {
                disconnectPollingJob?.cancel()
                syncRepository.clearLinkLocal()
                onShowSnackbar(getString(Res.string.sync_peer_disconnected))
            }
        }

        val uuidJob = launch {
            suspendRunCatching {
                syncRepository.ensureUUIDExists()
                syncRepository.getUuid()
            }.onSuccess { uuid ->
                setState { copy(uuid = uuid) }
            }
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
                    millisBetween(localSyncedAt, LocalDateTime.now()) >= SYNC_UP_COOL_TIME
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
                val now = LocalDateTime.now()
                if (expiration > now) {
                    val remainingSec = millisBetween(now, expiration) / 1000L
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
            SyncIntent.OnBackClick -> {
                analyticsHelper.logClick("SyncMain", "Back")
                onBackClick()
            }
            SyncIntent.OnSyncUpClick -> {
                analyticsHelper.logClick("SyncMain", "Sync")
                syncUpData()
            }
            SyncIntent.OnSyncUpDisabledClick -> onShowSnackbar(getString(Res.string.sync_already_latest))
            SyncIntent.OnDisconnectClick -> {
                analyticsHelper.logClick("SyncMain", "Disconnect")
                disconnectAnother()
            }
            SyncIntent.OnRestoreClick -> onNavigateToRestore()
            SyncIntent.OnDeviceInfoCopied -> onShowSnackbar(getString(Res.string.sync_device_info_copied))
            // QR 연동
            SyncIntent.OnGenerateQrClick -> {
                analyticsHelper.logClick("SyncMain", "GenerateCode")
                onGenerateQrClick()
            }
            SyncIntent.OnDismissQrSheet -> setState { copy(isQrSheetVisible = false) }
            SyncIntent.OnScanQrClick -> {
                analyticsHelper.logClick("SyncMain", "ScanQr")
                isProcessingQr = false
                setState { copy(isScanning = true) }
            }

            SyncIntent.OnDismissScan -> setState { copy(isScanning = false) }
            is SyncIntent.OnQrDetected -> {
                analyticsHelper.logClick("SyncMain", "QrDetected")
                handleQrDetected(intent.rawValue)
            }
        }
    }

    private fun onBackClick() {
        if (currentState.isScanning) {
            setState { copy(isScanning = false) }
        } else {
            onNavigateBack()
        }
    }

    private fun syncUpData() = safeScope.launch {
        setState { copy(isNetworkLoading = true) }
        suspendRunCatching {
            syncRepository.syncUpData()
        }.onSuccess {
            onShowSnackbar(getString(Res.string.sync_synced))
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
        }.onFailure {
            onShowSnackbar(getString(Res.string.sync_sync_failed))
        }.also {
            setState { copy(isNetworkLoading = false) }
        }
    }

    private fun disconnectAnother() = safeScope.launch {
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

            onShowSnackbar(getString(Res.string.sync_disconnect_success))
        }.onFailure {
            onShowSnackbar(getString(Res.string.sync_disconnect_failed))
        }.also {
            setState { copy(isNetworkLoading = false) }
        }
    }

    // QR 연동

    private suspend fun onGenerateQrClick() {
        // 유효한 코드가 이미 있으면 바텀시트만 다시 노출
        if (currentState.connectCode.isNotEmpty()) {
            setState { copy(isQrSheetVisible = true) }
            return
        }

        val autoGeneratedCode = generateRandomCode()

        suspendRunCatching {
            syncRepository.generateConnectCode(connectCode = autoGeneratedCode)
        }.onSuccess {
            setState {
                copy(
                    connectCode = autoGeneratedCode,
                    isGenerateButtonEnabled = false,
                    isQrSheetVisible = true,
                )
            }
            startTimer()
            startConnectionPolling()
        }.onFailure {
            onShowSnackbar(getString(Res.string.sync_generate_failed))
        }
    }

    private suspend fun handleQrDetected(rawValue: String) {
        if (!rawValue.startsWith(EBBINGPLANNER_SCHEME)) return
        if (isProcessingQr) return
        isProcessingQr = true

        val connectCode = rawValue.removePrefix(EBBINGPLANNER_SCHEME)
        setState { copy(isScanLoading = true) }

        suspendRunCatching {
            syncRepository.connectAnother(connectCode = connectCode)
        }.onSuccess { result ->
            setState { copy(isScanLoading = false, isScanning = false) }

            when (result) {
                is ConnectResult.Success -> {
                    setState { copy(connectedDeviceName = result.info.deviceName.ifEmpty { null }) }
                    onShowSnackbar(getString(Res.string.sync_connect_success))
                    loadInitData()
                }

                ConnectResult.InvalidOrExpired -> {
                    onShowSnackbar(getString(Res.string.sync_code_invalid_or_expired))
                    isProcessingQr = false
                }

                ConnectResult.AlreadyLinkedSelf -> {
                    onShowSnackbar(getString(Res.string.sync_already_linked_self))
                    isProcessingQr = false
                }

                ConnectResult.CodeAlreadyTaken -> {
                    onShowSnackbar(getString(Res.string.sync_code_already_taken))
                    isProcessingQr = false
                }
            }
        }.onFailure {
            setState { copy(isScanLoading = false) }
            onShowSnackbar(getString(Res.string.sync_connect_failed))
            isProcessingQr = false
        }
    }

    private fun startTimer(fromSec: Long = Timer.DEFAULT_DURATION_IN_SEC) {
        timerJob?.cancel()

        timerJob = safeScope.launch {
            timer.startTimer(durationInSec = fromSec)
                .collect { remaining ->
                    setState { copy(remainingTimeInSec = remaining) }
                    if (remaining <= 0L) {
                        setState {
                            copy(
                                connectCode = "",
                                isGenerateButtonEnabled = true,
                                isQrSheetVisible = false,
                            )
                        }
                        pollingJob?.cancel()
                        timerJob?.cancel()
                    }
                }
        }
    }

    private fun startConnectionPolling() {
        pollingJob?.cancel()

        pollingJob = safeScope.launch {
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
        timerJob?.cancel()

        syncRepository.getMyConnectCode()?.let { syncRepository.setLinkCode(it) }
        syncRepository.setStoredPeer(peer)
        syncRepository.clearMyConnectCode()

        setState {
            copy(
                connectCode = "",
                isGenerateButtonEnabled = true,
                isQrSheetVisible = false,
            )
        }
        onShowSnackbar(getString(Res.string.sync_peer_connected, peer.deviceName))
        loadInitData()
    }

    private fun startDisconnectPolling() {
        disconnectPollingJob?.cancel()

        disconnectPollingJob = safeScope.launch {
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
        onShowSnackbar(getString(Res.string.sync_peer_disconnected))
        loadInitData()
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        pollingJob?.cancel()
        disconnectPollingJob?.cancel()
    }

    private fun millisBetween(from: LocalDateTime, to: LocalDateTime): Long {
        val zone = TimeZone.currentSystemDefault()
        return (to.toInstant(zone) - from.toInstant(zone)).inWholeMilliseconds
    }

    private fun generateRandomCode(): String {
        val chars = "0123456789abcdef"
        return buildString {
            repeat(CONNECT_CODE_LENGTH) { append(chars[Random.nextInt(chars.length)]) }
        }
    }

    companion object {
        private const val SYNC_UP_COOL_TIME = 10_000L
        private const val CONNECTION_POLLING_INTERVAL = 5_000L
        private const val CONNECT_CODE_LENGTH = 20
    }
}
