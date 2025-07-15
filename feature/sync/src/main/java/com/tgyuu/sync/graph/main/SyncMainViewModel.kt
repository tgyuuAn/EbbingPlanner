package com.tgyuu.sync.graph.main

import androidx.lifecycle.viewModelScope
import com.tgyuu.common.base.BaseViewModel
import com.tgyuu.common.event.EbbingEvent
import com.tgyuu.common.event.EventBus
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
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SyncMainViewModel @Inject constructor(
    private val syncRepository: SyncRepository,
    private val networkMonitor: NetworkMonitor,
    private val navigationBus: NavigationBus,
    private val errorBus: ErrorBus,
    internal val eventBus: EventBus,
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
            syncRepository.getServerLastUpdatedAt()
                .onSuccess { setState { copy(serverLastUpdatedAt = it) } }
        }

        uuidJob.join()
        linkedUuidJob.join()
        serverLastUpdatedAtJob.join()
        localLastSyncedAtJob.join()
    }

    override suspend fun processIntent(intent: SyncIntent) {
        when (intent) {
            SyncIntent.OnBackClick -> navigationBus.navigate(NavigationEvent.Up)
            SyncIntent.OnSyncUpClick -> syncUpData()
            SyncIntent.OnConnectClick -> navigationBus.navigate(NavigationEvent.To(SyncGraph.ConnectRoute))
            SyncIntent.OnDisconnectClick -> disconnectAnother()
        }
    }

    private fun syncUpData() = viewModelScope.launch {
        if (networkMonitor.networkState.value != NetworkState.Connected) {
            eventBus.sendEvent(EbbingEvent.ShowSnackBar("네트워크가 연결되어 있지 않습니다."))
            return@launch
        }

        setState { copy(isNetworkLoading = true) }
        syncRepository.syncUpData()
            .onSuccess {
                eventBus.sendEvent(EbbingEvent.ShowSnackBar("데이터를 업로드 하였습니다."))
                setState {
                    copy(
                        localLastSyncedAt = it,
                        serverLastUpdatedAt = it,
                    )
                }
            }
            .onFailure { error ->
                errorBus.sendError(error)
                eventBus.sendEvent(EbbingEvent.ShowSnackBar("업로드에 실패하였습니다."))
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
        syncRepository.disconnectAnother()
            .onSuccess {
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
}
