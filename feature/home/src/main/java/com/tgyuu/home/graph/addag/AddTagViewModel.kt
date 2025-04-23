package com.tgyuu.home.graph.addag

import com.tgyuu.common.base.BaseViewModel
import com.tgyuu.common.event.EventBus
import com.tgyuu.home.graph.addag.contract.AddTagIntent
import com.tgyuu.home.graph.addag.contract.AddTagState
import com.tgyuu.navigation.NavigationBus
import com.tgyuu.navigation.NavigationEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddTagViewModel @Inject constructor(
    private val eventBus: EventBus,
    private val navigationBus: NavigationBus,
) : BaseViewModel<AddTagState, AddTagIntent>(AddTagState()) {

    override suspend fun processIntent(intent: AddTagIntent) {
        when (intent) {
            AddTagIntent.OnBackClick -> navigationBus.navigate(NavigationEvent.Up)
            AddTagIntent.OnSaveClick -> onSaveClick()
        }
    }

    private fun onSaveClick() {}
}
