package com.tgyuu.sync.graph.upload

import androidx.lifecycle.viewModelScope
import com.tgyuu.common.base.BaseViewModel
import com.tgyuu.common.event.EventBus
import com.tgyuu.domain.repository.ConfigRepository
import com.tgyuu.navigation.NavigationBus
import com.tgyuu.navigation.NavigationEvent
import com.tgyuu.sync.graph.upload.contract.UploadIntent
import com.tgyuu.sync.graph.upload.contract.UploadState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UploadViewModel @Inject constructor(
    private val configRepository: ConfigRepository,
    private val navigationBus: NavigationBus,
    private val eventBus: EventBus,
) : BaseViewModel<UploadState, UploadIntent>(UploadState()) {

    init {
        viewModelScope.launch {
            val uuid = configRepository.getUUID()
            setState { copy(uuid = uuid) }
        }
    }

    override suspend fun processIntent(intent: UploadIntent) {
        when (intent) {
            UploadIntent.OnBackClick -> navigationBus.navigate(NavigationEvent.Up)
            UploadIntent.OnUploadClick -> Unit
        }
    }
}
