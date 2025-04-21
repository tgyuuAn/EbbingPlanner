package com.tgyuu.dashboard

import com.tgyuu.common.base.BaseViewModel
import com.tgyuu.dashboard.contract.DashboardIntent
import com.tgyuu.dashboard.contract.DashboardState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(

) : BaseViewModel<DashboardState, DashboardIntent>(DashboardState()) {
    override suspend fun processIntent(event: DashboardIntent) {
        TODO("Not yet implemented")
    }
}
