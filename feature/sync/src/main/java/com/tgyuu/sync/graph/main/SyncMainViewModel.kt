package com.tgyuu.sync.graph.main

import androidx.lifecycle.viewModelScope
import com.tgyuu.common.base.BaseViewModel
import com.tgyuu.common.event.EbbingEvent
import com.tgyuu.common.event.EventBus
import com.tgyuu.domain.repository.SyncRepository
import com.tgyuu.navigation.NavigationBus
import com.tgyuu.navigation.NavigationEvent
import com.tgyuu.navigation.SyncGraph
import com.tgyuu.sync.graph.main.contract.SyncIntent
import com.tgyuu.sync.graph.main.contract.SyncMainState
import com.tgyuu.sync.network.NetworkMonitor
import com.tgyuu.sync.network.NetworkState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SyncMainViewModel @Inject constructor(
    private val syncRepository: SyncRepository,
    private val navigationBus: NavigationBus,
    private val eventBus: EventBus,
    private val networkMonitor: NetworkMonitor,
) : BaseViewModel<SyncMainState, SyncIntent>(SyncMainState()) {

    internal fun loadInitData() = viewModelScope.launch {
        val uuidJob = launch {
            val uuid = syncRepository.getUUID()
            setState { copy(uuid = uuid) }
        }

        val linkedUuidJob = launch {
            val linkedUuid = syncRepository.getLinkedUUID()
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
            SyncIntent.OnLinkClick -> navigationBus.navigate(NavigationEvent.To(SyncGraph.LinkRoute))
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
            .onFailure {
                eventBus.sendEvent(EbbingEvent.ShowSnackBar("업로드에 실패하였습니다."))
            }.also {
                setState { copy(isNetworkLoading = false) }
            }
    }
}
