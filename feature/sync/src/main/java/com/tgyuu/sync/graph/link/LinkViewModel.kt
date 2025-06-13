package com.tgyuu.sync.graph.link

import androidx.lifecycle.viewModelScope
import com.tgyuu.common.base.BaseViewModel
import com.tgyuu.common.event.EventBus
import com.tgyuu.domain.repository.ConfigRepository
import com.tgyuu.navigation.NavigationBus
import com.tgyuu.navigation.NavigationEvent
import com.tgyuu.sync.graph.link.contract.LinkIntent
import com.tgyuu.sync.graph.link.contract.LinkState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LinkViewModel @Inject constructor(
    private val configRepository: ConfigRepository,
    private val navigationBus: NavigationBus,
    private val eventBus: EventBus,
) : BaseViewModel<LinkState, LinkIntent>(LinkState()) {

    init {
        viewModelScope.launch {
            val uuid = configRepository.getUUID()
            setState { copy(uuid = uuid) }
        }
    }

    override suspend fun processIntent(intent: LinkIntent) {
        when (intent) {
            LinkIntent.OnBackClick -> navigationBus.navigate(NavigationEvent.Up)
            LinkIntent.OnLinkClick -> Unit
        }
    }
}
