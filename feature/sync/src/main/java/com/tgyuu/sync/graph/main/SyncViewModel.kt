package com.tgyuu.sync.graph.main

import androidx.lifecycle.viewModelScope
import com.tgyuu.common.base.BaseViewModel
import com.tgyuu.common.event.EbbingEvent
import com.tgyuu.common.event.EventBus
import com.tgyuu.domain.repository.ConfigRepository
import com.tgyuu.navigation.NavigationBus
import com.tgyuu.navigation.NavigationEvent
import com.tgyuu.navigation.SyncGraph
import com.tgyuu.sync.graph.main.contract.SyncIntent
import com.tgyuu.sync.graph.main.contract.SyncMainState
import com.tgyuu.sync.network.NetworkMonitor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SyncViewModel @Inject constructor(
    private val configRepository: ConfigRepository,
    private val navigationBus: NavigationBus,
    private val eventBus: EventBus,
    internal val networkMonitor: NetworkMonitor,
) : BaseViewModel<SyncMainState, SyncIntent>(SyncMainState()) {

    init {
        viewModelScope.launch {
            val uuid = configRepository.getUUID()
            setState { copy(uuid = uuid) }
        }
    }

    override suspend fun processIntent(intent: SyncIntent) {
        when (intent) {
            SyncIntent.OnBackClick -> navigationBus.navigate(NavigationEvent.Up)
            SyncIntent.OnUploadClick -> navigationBus.navigate(NavigationEvent.To(SyncGraph.UploadRoute))
            SyncIntent.OnDownloadClick -> navigationBus.navigate(NavigationEvent.To(SyncGraph.DownloadRoute))
            SyncIntent.OnLinkClick -> navigationBus.navigate(NavigationEvent.To(SyncGraph.LinkRoute))
            SyncIntent.OnUuidClick -> eventBus.sendEvent(EbbingEvent.ShowSnackBar("ID를 복사하였습니다."))
        }
    }
}
