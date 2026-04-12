package com.tgyuu.sync.graph.main

import androidx.lifecycle.viewModelScope
import com.tgyuu.analytics.AnalyticsEvent
import com.tgyuu.analytics.AnalyticsHelper
import com.tgyuu.common.base.BaseViewModel
import com.tgyuu.common.event.EbbingEvent
import com.tgyuu.common.event.EventBus
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
import java.time.Duration
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class SyncMainViewModel @Inject constructor(
    private val syncRepository: SyncRepository,
    private val networkMonitor: NetworkMonitor,
    private val navigationBus: NavigationBus,
    private val errorBus: ErrorBus,
    internal val eventBus: EventBus,
    private val analyticsHelper: AnalyticsHelper,
) : BaseViewModel<SyncMainState, SyncIntent>(SyncMainState()) {

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

        uuidJob.join()
        linkedUuidJob.join()
        serverLastUpdatedAtJob.join()
        localLastSyncedAtJob.join()
    }

    override suspend fun processIntent(intent: SyncIntent) {
        when (intent) {
            SyncIntent.OnBackClick -> {
                analyticsHelper.logEvent(
                    AnalyticsEvent.Click(screenName = "SyncMain", buttonName = "Back")
                )
                navigationBus.navigate(NavigationEvent.Up)
            }
            SyncIntent.OnSyncUpClick -> {
                analyticsHelper.logEvent(
                    AnalyticsEvent.Click(screenName = "SyncMain", buttonName = "Sync")
                )
                syncUpData()
            }
            SyncIntent.OnConnectClick -> {
                analyticsHelper.logEvent(
                    AnalyticsEvent.Click(screenName = "SyncMain", buttonName = "Connect")
                )
                navigationBus.navigate(NavigationEvent.To(SyncGraph.ConnectRoute))
            }
            SyncIntent.OnDisconnectClick -> {
                analyticsHelper.logEvent(
                    AnalyticsEvent.Click(screenName = "SyncMain", buttonName = "Disconnect")
                )
                disconnectAnother()
            }
            SyncIntent.OnSyncDialogBackClick -> analyticsHelper.logEvent(
                AnalyticsEvent.Click(screenName = "SyncDialog", buttonName = "Back")
            )
            SyncIntent.OnSyncDialogSyncClick -> {
                analyticsHelper.logEvent(
                    AnalyticsEvent.Click(screenName = "SyncDialog", buttonName = "Sync")
                )
                syncUpData()
            }
            SyncIntent.OnDisconnectDialogBackClick -> analyticsHelper.logEvent(
                AnalyticsEvent.Click(screenName = "DisconnectDialog", buttonName = "Back")
            )
            SyncIntent.OnDisconnectDialogDisconnectClick -> {
                analyticsHelper.logEvent(
                    AnalyticsEvent.Click(screenName = "DisconnectDialog", buttonName = "Disconnect")
                )
                disconnectAnother()
            }
        }
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
        private const val SYNC_UP_COOL_TIME = 10_000L
    }
}
