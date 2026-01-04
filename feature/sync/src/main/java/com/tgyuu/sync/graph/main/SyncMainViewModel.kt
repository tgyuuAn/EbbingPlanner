package com.tgyuu.sync.graph.main

import androidx.lifecycle.viewModelScope
import com.tgyuu.analytics.AnalyticsEvent
import com.tgyuu.analytics.AnalyticsHelper
import com.tgyuu.common.base.BaseViewModel
import com.tgyuu.common.event.EbbingEvent
import com.tgyuu.common.event.EventBus
import com.tgyuu.common.now
import com.tgyuu.common.suspendRunCatching
import com.tgyuu.domain.model.ErrorBus
import com.tgyuu.domain.repository.SyncRepository
import com.tgyuu.navigation.NavigationBus
import com.tgyuu.navigation.NavigationEvent
import com.tgyuu.navigation.SyncGraph
import com.tgyuu.sync.graph.main.contract.SyncIntent
import com.tgyuu.sync.graph.main.contract.SyncMainState
import com.tgyuu.sync.network.NetworkMonitor
import com.tgyuu.sync.network.NetworkState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import javax.inject.Inject
import kotlin.time.ExperimentalTime

@HiltViewModel
class SyncMainViewModel @Inject constructor(
    private val syncRepository: SyncRepository,
    private val networkMonitor: NetworkMonitor,
    private val navigationBus: NavigationBus,
    private val errorBus: ErrorBus,
    internal val eventBus: EventBus,
    private val analyticsHelper: AnalyticsHelper,
) : BaseViewModel<SyncMainState, SyncIntent>(SyncMainState()) {

    @OptIn(ExperimentalTime::class)
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
                val now = LocalDateTime.now()
                val isSyncUpEnabled = serverLastUpdatedAt == null ||
                        (now.toInstant(TimeZone.currentSystemDefault()) -
                                serverLastUpdatedAt.toInstant(TimeZone.currentSystemDefault()))
                            .inWholeMilliseconds >= SYNC_UP_COOL_TIME

                setState {
                    copy(
                        serverLastUpdatedAt = serverLastUpdatedAt,
                        isSyncUpEnabled = isSyncUpEnabled,
                    )
                }
            }
        }

        uuidJob.join()
        linkedUuidJob.join()
        serverLastUpdatedAtJob.join()
        localLastSyncedAtJob.join()
    }

    override suspend fun processIntent(intent: SyncIntent) {
        when (intent) {
            SyncIntent.OnBackClick -> onBackClick()
            SyncIntent.OnSyncUpClick -> onSyncUpClick()
            SyncIntent.OnConnectClick -> onConnectClick()
            SyncIntent.OnDisconnectClick -> onDisconnectClick()
            SyncIntent.OnSyncDialogBackClick -> onSyncDialogBackClick()
            SyncIntent.OnSyncDialogSyncClick -> onSyncDialogSyncClick()
            SyncIntent.OnDisconnectDialogBackClick -> onDisconnectDialogBackClick()
            SyncIntent.OnDisconnectDialogDisconnectClick -> onDisconnectDialogDisconnectClick()
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

    private suspend fun onConnectClick() {
        analyticsHelper.logEvent(
            AnalyticsEvent.Click(screenName = SCREEN_NAME, buttonName = "Connect")
        )
        navigationBus.navigate(NavigationEvent.To(SyncGraph.ConnectRoute))
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

    companion object {
        private const val SCREEN_NAME = "SyncMain"
        private const val SYNC_DIALOG = "SyncDialog"
        private const val DISCONNECT_DIALOG = "DisconnectDialog"
        private const val SYNC_UP_COOL_TIME = 10_000L
    }
}
