package com.tgyuu.home.graph.main

import com.tgyuu.common.base.BaseViewModel
import com.tgyuu.home.graph.main.contract.HomeIntent
import com.tgyuu.home.graph.main.contract.HomeState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : BaseViewModel<HomeState, HomeIntent>(HomeState()) {
    override suspend fun processIntent(event: HomeIntent) {
        TODO("Not yet implemented")
    }
}
