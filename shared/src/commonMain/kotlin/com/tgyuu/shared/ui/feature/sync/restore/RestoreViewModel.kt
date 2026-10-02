package com.tgyuu.shared.ui.feature.sync.restore

import com.tgyuu.shared.base.BaseViewModel
import com.tgyuu.shared.common.suspendRunCatching
import com.tgyuu.shared.domain.model.sync.RestoreResult
import com.tgyuu.shared.domain.repository.SyncRepository
import com.tgyuu.shared.platform.AnalyticsHelper
import com.tgyuu.shared.platform.logClick
import ebbingplanner.shared.generated.resources.Res
import ebbingplanner.shared.generated.resources.sync_restore_ambiguous
import ebbingplanner.shared.generated.resources.sync_restore_done
import ebbingplanner.shared.generated.resources.sync_restore_empty_data
import ebbingplanner.shared.generated.resources.sync_restore_failed
import ebbingplanner.shared.generated.resources.sync_restore_linked
import ebbingplanner.shared.generated.resources.sync_restore_not_found
import ebbingplanner.shared.generated.resources.sync_restore_self
import org.jetbrains.compose.resources.getString

class RestoreViewModel(
    private val syncRepository: SyncRepository,
    private val onNavigateBack: () -> Unit,
    private val onShowSnackbar: (String) -> Unit = {},
    private val analyticsHelper: AnalyticsHelper? = null,
) : BaseViewModel<RestoreState, RestoreIntent>(RestoreState()) {

    override suspend fun processIntent(intent: RestoreIntent) {
        when (intent) {
            RestoreIntent.OnBackClick -> {
                analyticsHelper.logClick("RestoreByDeviceId", "Back")
                onBackClick()
            }
            is RestoreIntent.OnDeviceIdChange -> setState { copy(deviceId = intent.deviceId) }
            RestoreIntent.OnRestoreClick -> {
                analyticsHelper.logClick("RestoreByDeviceId", "Restore")
                onRestoreClick()
            }
        }
    }

    private fun onBackClick() {
        if (currentState.isRestoring) return
        onNavigateBack()
    }

    private suspend fun onRestoreClick() {
        if (!currentState.isRestoreEnabled) return

        setState { copy(isRestoring = true) }

        val result = suspendRunCatching { syncRepository.restoreByDeviceId(currentState.deviceId) }

        setState { copy(isRestoring = false) }

        result.onSuccess { restoreResult ->
            when (restoreResult) {
                is RestoreResult.Success -> {
                    onShowSnackbar(
                        getString(Res.string.sync_restore_done, restoreResult.deviceName)
                    )
                    onNavigateBack()
                }

                RestoreResult.NotFound ->
                    onShowSnackbar(getString(Res.string.sync_restore_not_found))

                RestoreResult.EmptyData ->
                    onShowSnackbar(getString(Res.string.sync_restore_empty_data))

                RestoreResult.Ambiguous ->
                    onShowSnackbar(getString(Res.string.sync_restore_ambiguous))

                RestoreResult.SelfDevice ->
                    onShowSnackbar(getString(Res.string.sync_restore_self))

                RestoreResult.LinkedDevice ->
                    onShowSnackbar(getString(Res.string.sync_restore_linked))
            }
        }.onFailure {
            onShowSnackbar(getString(Res.string.sync_restore_failed))
        }
    }
}
