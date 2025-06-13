package com.tgyuu.sync.graph.download

import androidx.lifecycle.viewModelScope
import com.tgyuu.common.base.BaseViewModel
import com.tgyuu.common.event.EventBus
import com.tgyuu.domain.repository.ConfigRepository
import com.tgyuu.navigation.NavigationBus
import com.tgyuu.navigation.NavigationEvent
import com.tgyuu.sync.graph.download.contract.DownloadIntent
import com.tgyuu.sync.graph.download.contract.DownloadState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DownloadViewModel @Inject constructor(
    private val configRepository: ConfigRepository,
    private val navigationBus: NavigationBus,
    private val eventBus: EventBus,
) : BaseViewModel<DownloadState, DownloadIntent>(DownloadState()) {

    init {
        viewModelScope.launch {
            val uuid = configRepository.getUUID()
            setState { copy(uuid = uuid) }
        }
    }

    override suspend fun processIntent(intent: DownloadIntent) {
        when (intent) {
            DownloadIntent.OnBackClick -> navigationBus.navigate(NavigationEvent.Up)
            DownloadIntent.OnDownloadClick -> Unit
        }
    }
}
