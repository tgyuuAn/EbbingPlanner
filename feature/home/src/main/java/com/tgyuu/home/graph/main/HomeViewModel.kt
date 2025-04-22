package com.tgyuu.home.graph.main

import com.tgyuu.common.base.BaseViewModel
import com.tgyuu.common.toFormattedString
import com.tgyuu.home.graph.main.contract.HomeIntent
import com.tgyuu.home.graph.main.contract.HomeState
import com.tgyuu.navigation.HomeGraph
import com.tgyuu.navigation.NavigationBus
import com.tgyuu.navigation.NavigationEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val navigationBus: NavigationBus,
) : BaseViewModel<HomeState, HomeIntent>(HomeState()) {
    override suspend fun processIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.OnAddTodoClick -> navigationBus.navigate(
                NavigationEvent.To(HomeGraph.AddTodoRoute(intent.selectedDate.toFormattedString()))
            )
        }
    }
}
