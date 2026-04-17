package com.tgyuu.shared.ui.feature.sync

import com.tgyuu.shared.base.BaseViewModel
import com.tgyuu.shared.domain.repository.SyncRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SyncViewModel(
    private val syncRepository: SyncRepository? = null,
    private val onNavigateBack: () -> Unit,
    private val onNavigateToConnect: () -> Unit,
    private val onShowSnackbar: (String) -> Unit = {},
) : BaseViewModel<SyncState, SyncIntent>(SyncState()) {

    init {
        loadInitData()
    }

    private fun loadInitData() {
        safeScope.launch {
            try {
                syncRepository?.ensureUUIDExists()
                val uuid = syncRepository?.getUuid() ?: ""
                val linkedUuid = syncRepository?.getConnectedUuid()
                val localSyncedAt = syncRepository?.getLocalSyncedAt()
                val serverUpdatedAt = syncRepository?.getServerLastUpdatedAt()

                setState {
                    copy(
                        uuid = uuid,
                        linkedUuid = linkedUuid,
                        localLastSyncedAt = localSyncedAt,
                        serverLastUpdatedAt = serverUpdatedAt,
                        isNetworkLoading = false,
                    )
                }
            } catch (e: Exception) {
                setState { copy(isNetworkLoading = false) }
            }
        }
    }

    override suspend fun processIntent(intent: SyncIntent) {
        when (intent) {
            SyncIntent.OnBackClick -> onNavigateBack()
            SyncIntent.OnSyncUpClick -> syncUpData()
            SyncIntent.OnConnectClick -> onNavigateToConnect()
            SyncIntent.OnDisconnectClick -> disconnectAnother()
        }
    }

    private suspend fun syncUpData() {
        if (!currentState.isSyncUpEnabled) return

        setState { copy(isSyncUpEnabled = false) }
        try {
            val syncedAt = syncRepository?.syncUpData()
            setState {
                copy(
                    localLastSyncedAt = syncedAt,
                    serverLastUpdatedAt = syncedAt,
                )
            }
            onShowSnackbar("동기화가 완료되었습니다")
        } catch (e: Exception) {
            onShowSnackbar("동기화에 실패했습니다")
        } finally {
            safeScope.launch {
                delay(10000)
                setState { copy(isSyncUpEnabled = true) }
            }
        }
    }

    private suspend fun disconnectAnother() {
        try {
            syncRepository?.disconnectAnother()
            setState {
                copy(
                    linkedUuid = null,
                    localLastSyncedAt = null,
                    serverLastUpdatedAt = null,
                )
            }
            onShowSnackbar("연결이 해제되었습니다")
        } catch (e: Exception) {
            onShowSnackbar("연결 해제에 실패했습니다")
        }
    }
}
