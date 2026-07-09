package com.tgyuu.sync.graph.restore

import com.tgyuu.analytics.AnalyticsEvent
import com.tgyuu.analytics.AnalyticsHelper
import com.tgyuu.common.base.BaseViewModel
import com.tgyuu.common.event.EbbingEvent
import com.tgyuu.common.event.EventBus
import com.tgyuu.common.isNetworkError
import com.tgyuu.common.suspendRunCatching
import com.tgyuu.common.ui.resource.ResourceProvider
import com.tgyuu.designsystem.R
import com.tgyuu.domain.model.ErrorBus
import com.tgyuu.domain.model.sync.RestoreResult
import com.tgyuu.domain.repository.SyncRepository
import com.tgyuu.navigation.NavigationBus
import com.tgyuu.navigation.NavigationEvent
import com.tgyuu.sync.graph.restore.contract.RestoreIntent
import com.tgyuu.sync.graph.restore.contract.RestoreState
import com.tgyuu.sync.network.NetworkMonitor
import com.tgyuu.sync.network.NetworkState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RestoreViewModel @Inject constructor(
    private val syncRepository: SyncRepository,
    private val networkMonitor: NetworkMonitor,
    private val navigationBus: NavigationBus,
    private val errorBus: ErrorBus,
    private val eventBus: EventBus,
    private val analyticsHelper: AnalyticsHelper,
    private val resourceProvider: ResourceProvider,
) : BaseViewModel<RestoreState, RestoreIntent>(RestoreState()) {

    init {
        analyticsHelper.logEvent(AnalyticsEvent.View(screenName = "RestoreByDeviceId"))
    }

    override suspend fun processIntent(intent: RestoreIntent) {
        when (intent) {
            RestoreIntent.OnBackClick -> onBackClick()
            is RestoreIntent.OnDeviceIdChange -> setState { copy(deviceId = intent.deviceId) }
            RestoreIntent.OnRestoreClick -> onRestoreClick()
        }
    }

    private suspend fun onBackClick() {
        analyticsHelper.logEvent(
            AnalyticsEvent.Click(screenName = "RestoreByDeviceId", buttonName = "Back")
        )
        navigationBus.navigate(NavigationEvent.Up)
    }

    private suspend fun onRestoreClick() {
        analyticsHelper.logEvent(
            AnalyticsEvent.Click(screenName = "RestoreByDeviceId", buttonName = "Restore")
        )

        if (!currentState.isRestoreEnabled) return

        if (networkMonitor.networkState.value != NetworkState.Connected) {
            eventBus.sendEvent(EbbingEvent.ShowSnackBar(resourceProvider.getString(R.string.sync_network_required)))
            return
        }

        setState { copy(isRestoring = true) }

        val result = suspendRunCatching { syncRepository.restoreByDeviceId(currentState.deviceId) }

        setState { copy(isRestoring = false) }

        result.onSuccess { restoreResult ->
            when (restoreResult) {
                is RestoreResult.Success -> {
                    eventBus.sendEvent(
                        EbbingEvent.ShowSnackBar(
                            resourceProvider.getString(
                                R.string.sync_restore_done,
                                restoreResult.deviceName,
                            )
                        )
                    )
                    navigationBus.navigate(NavigationEvent.Up)
                }

                RestoreResult.NotFound -> eventBus.sendEvent(
                    EbbingEvent.ShowSnackBar(resourceProvider.getString(R.string.sync_restore_not_found))
                )

                RestoreResult.EmptyData -> eventBus.sendEvent(
                    EbbingEvent.ShowSnackBar(resourceProvider.getString(R.string.sync_restore_empty_data))
                )

                RestoreResult.Ambiguous -> eventBus.sendEvent(
                    EbbingEvent.ShowSnackBar(resourceProvider.getString(R.string.sync_restore_ambiguous))
                )

                RestoreResult.SelfDevice -> eventBus.sendEvent(
                    EbbingEvent.ShowSnackBar(resourceProvider.getString(R.string.sync_restore_self))
                )
            }
        }.onFailure { exception ->
            errorBus.sendError(exception)
            val message = if (exception.isNetworkError()) {
                resourceProvider.getString(R.string.sync_network_check)
            } else {
                resourceProvider.getString(R.string.sync_restore_failed)
            }
            eventBus.sendEvent(EbbingEvent.ShowSnackBar(message))
        }
    }
}
